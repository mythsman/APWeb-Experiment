import java.util.ArrayList;

public class QuerySet {
	private final double inf = 1e99;
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
			Location start = query.getUser();
			Location end = query.getPoi();

			Vertex start1 = graph.getEdges().get(start.getInEdgeId()).getSvertex(),
					start2 = graph.getEdges().get(start.getInEdgeId()).getEvertex();

			Vertex end1 = graph.getEdges().get(end.getInEdgeId()).getSvertex(),
					end2 = graph.getEdges().get(end.getInEdgeId()).getEvertex();

			int len = graph.getVertices().size();
			double[] dist = new double[len];
			boolean[] vis = new boolean[len];
			int[] before = new int[len];
			for (int i = 0; i < len; i++) {
				dist[i] = inf;
				vis[i] = false;
				before[i] = -1;
			}
			dist[start1.getVid()] = start1.sphericalDistance(start);
			dist[start2.getVid()] = start2.sphericalDistance(start);

			vis[start1.getVid()] = true;
			vis[start2.getVid()] = true;
			for (int eid : start1.getNearbyEdgeId()) {
				int vid = graph.getEdges().get(eid).except(start1).getVid();
				if (vis[vid])
					continue;
				if (dist[vid] > dist[start1.getVid()] + graph.getEdges().get(eid).getLength()) {
					dist[vid] = dist[start1.getVid()] + graph.getEdges().get(eid).getLength();
					before[vid] = start1.getVid();
				}
			}

			for (int eid : start2.getNearbyEdgeId()) {
				int vid = graph.getEdges().get(eid).except(start2).getVid();
				if (vis[vid])
					continue;
				if (dist[vid] > dist[start2.getVid()] + graph.getEdges().get(eid).getLength()) {
					dist[vid] = dist[start2.getVid()] + graph.getEdges().get(eid).getLength();
					before[vid] = start2.getVid();
				}
			}

			for (int cnt = 0; cnt < len; cnt++) {
				double mindis = inf;
				int minId = -1;
				for (int i = 0; i < len; i++) {
					if (!vis[i] && dist[i] < mindis) {
						mindis = dist[i];
						minId = i;
					}
				}
				if (mindis == inf)
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
				if (vis[end1.getVid()] && vis[end2.getVid()])
					break;
			}
			double d1 = end1.sphericalDistance(end);
			double d2 = end2.sphericalDistance(end);
			int endVid;
			if (dist[end1.getVid()] + d1 < dist[end2.getVid()] + d2) {
				endVid = end1.getVid();
				query.setDist(dist[end1.getVid()] + d1);
			} else {
				endVid = end2.getVid();
				query.setDist(dist[end2.getVid()] + d2);
			}
			ArrayList<Location> waypoints = new ArrayList<Location>();
			endVid = end1.getVid();
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

	public QuerySet initSa() {
		for (int i = 0; i < queries.size(); i++) {
			for (int j = 0; j < queries.size(); j++) {
				if (i != j && queries.get(i).getSa() != -1 && queries.get(j).getSa() != -1
						&& queries.get(i).in(queries.get(j))) {
					queries.get(j).setSa(queries.get(i).getSa() + queries.get(j).getSa());
					queries.get(i).setSa(-1);
				}
			}
		}
		return this;
	}
}
