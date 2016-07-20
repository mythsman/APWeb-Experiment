import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class QuerySet {
	private final double INF = 1e99;
	private ArrayList<Query> queries;
	private Graph graph;
	private int sendWindow = 2;

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

	public int getSendWindow() {
		return sendWindow;
	}

	public void setSendWindow(int sendWindow) {
		this.sendWindow = sendWindow;
	}

	public ArrayList<Location> dijikstra(int userEdgeId, int poiEdgeId) {
		int len = graph.getVertices().size();
		double[] dist = new double[len];
		boolean[] vis = new boolean[len];
		int[] before = new int[len];
		for (int i = 0; i < len; i++) {
			dist[i] = INF;
			vis[i] = false;
			before[i] = -1;
		}
		dist[userEdgeId] = 0;

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
			if (vis[poiEdgeId])
				break;
		}

		int endVid;
		ArrayList<Location> waypoints = new ArrayList<Location>();
		endVid = before[poiEdgeId];

		while (endVid != -1) {

			waypoints.add(graph.getVertices().get(endVid));
			endVid = before[endVid];
		}
		for (int i = 0; i < waypoints.size() / 2; i++) {
			Location tmp = waypoints.get(i);
			waypoints.set(i, waypoints.get(waypoints.size() - i - 1));
			waypoints.set(waypoints.size() - i - 1, tmp);
		}
		return waypoints;
	}

	public QuerySet initPath() {
		Clock.start();
		for (int k = 0; k < size(); k++) {
			Query query = get(k);
			query.setWaypoints(dijikstra(query.getUser().getInVertexId(), query.getPoi().getInVertexId()));
		}
		Clock.end();
		Clock.show("InitPath in ");
		return this;
	}

	/**
	 * Sort by euclidiean length Des;
	 */
	private void sortByLengthDes() {
		Collections.sort(queries, new Comparator<Query>() {
			@Override
			public int compare(Query q1, Query q2) {
				if (q1.getLength() < q2.getLength()) {
					return 1;
				} else if (q1.getLength() > q2.getLength()) {
					return -1;
				} else
					return 0;
			}
		});
	}

	private void sortBySaDesc() {
		Collections.sort(queries, new Comparator<Query>() {
			@Override
			public int compare(Query q1, Query q2) {
				if (q1.getSa() < q2.getSa()) {
					return 1;
				} else if (q1.getSa() > q2.getSa()) {
					return -1;
				} else
					return 0;
			}
		});
	}

	public QuerySet initSa() {
		Clock.start();
		sortByLengthDes();
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
		Clock.end();
		Clock.show("Init Sa in ");
		return this;
	}

	public void mergeBySelectSort() {

		sortBySaDesc();
		ArrayList<Query> qs = new ArrayList<Query>();
		int len = 0;
		while (len < queries.size()) {
			int cnt = 0;
			Query q = new Query(queries.get(len).getUser(), queries.get(len).getPoi());
			len++;
			cnt++;
			while (cnt < sendWindow / 2) {
				q.append(queries.get(len));
				len++;
				cnt++;
				if (len >= queries.size())
					break;
			}
			qs.add(q);
		}

		queries = qs;

	}

	public void mergeByGreedy() {
		sortBySaDesc();

		int len = queries.size();
		int cnt = 0;
		for (int i = 0; i < len; i++) {
			for (int j = i + 1; j < len; j++) {
				Query q = new Query(queries.get(j).getUser(), queries.get(i).getPoi(), 0);
				q.setSa(0);
				for (Query qu : queries) {
					if (qu.in(q)) {
						q.setSa(q.getSa() + 1);
					}
				}
				if (q.getSa() > 0) {
					cnt++;
				}
			}

		}
		System.out.println(cnt);

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
