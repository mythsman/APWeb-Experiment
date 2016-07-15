import java.util.ArrayList;
import java.util.Collections;

public class QuerySet {
	private final double INF = 1e99;
	private ArrayList<Query> queries;
	private Graph graph;

	public QuerySet(Graph graph) {
		this.graph = graph;
		queries = new ArrayList<Query>();
	}

	public void add(Query query) {
		queries.add(query);
	}

	public Query get(int i) {
		return queries.get(i);
	}

	public int size() {
		return queries.size();
	}

	public QuerySet initPath() {
		for (int k = 0; k < size(); k++) {
			Query query = get(k);
			Location user = query.getUser();
			Location poi = query.getPoi();

			int len = graph.getVertices().size();
			double[] dist = new double[len];
			boolean[] vis = new boolean[len];
			int[] before = new int[len];
			for (int i = 0; i < len; i++) {
				dist[i] = INF;
				vis[i] = false;
				before[i] = -1;
			}
			vis[user.getInVertexId()] = true;

			for (int eid : graph.getVertices().get(user.getInVertexId()).getNearbyEdgeId()) {
				int vid = graph.getEdges().get(eid).except(graph.getVertices().get(user.getInVertexId())).getVid();
				dist[vid] = graph.getEdges().get(eid).getLength();
				before[vid] = user.getInVertexId();
			}

			for (int cnt = 0; cnt < len; cnt++) {
				double mindis = INF;
				int minId = -1;
				for (int i = 0; i < len; i++) {
					if (!vis[i] && dist[i] < mindis) {
						mindis = dist[i];
						minId = i;
					}
				}
				if (mindis == INF)
					break;
				vis[minId] = true;
				for (int eid : graph.getVertices().get(minId).getNearbyEdgeId()) {
					int vid = graph.getEdges().get(eid).except(graph.getVertices().get(minId)).getVid();
					if (vis[vid])
						continue;
					if (mindis + graph.getEdges().get(eid).getLength() < dist[vid]) {
						dist[vid] = mindis + graph.getEdges().get(eid).getLength();
						before[vid] = minId;
					}
				}
				if (vis[poi.getInVertexId()])
					break;
			}

			int endVid;
			ArrayList<Location> waypoints = new ArrayList<Location>();
			endVid = before[poi.getInVertexId()];
			while (endVid != -1) {
				waypoints.add(graph.getVertices().get(endVid));
				endVid = before[endVid];
			}
			for (int i = 0; i < waypoints.size() / 2; i++) {
				Location tmp = waypoints.get(i);
				waypoints.set(i, waypoints.get(waypoints.size() - i - 1));
				waypoints.set(waypoints.size() - i - 1, tmp);
			}
			query.setWaypoints(waypoints);
		}
		return this;

	}

	/**
	 * Sort by euclidiean length Des;
	 */
	private void sort() {
		Collections.sort(queries);
	}

	public QuerySet initSa() {
		sort();
		for (int i = 0; i < queries.size(); i++) {
			if (queries.get(i).getSa() == -1)
				continue;
			for (int j = i + 1; j < queries.size(); j++) {
				if (queries.get(j).getSa() == -1)
					continue;
				boolean isIn = queries.get(j).in(queries.get(i));
				if (isIn) {
					queries.get(i).setSa(queries.get(i).getSa() + queries.get(j).getSa());
					queries.get(j).setSa(-1);
				}
			}
		}
		return this;
	}

	public void testSaNum() {
		int cnt = 0;
		for (int i = 0; i < size(); i++) {
			if (get(i).getSa() == -1) {
				cnt++;
			}
		}
		System.out.println("We have shared " + cnt + " queries in " + size() + " queries.");
	}
}
