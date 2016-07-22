import java.io.IOException;
import java.util.ArrayList;

public class LBS {
	private Graph graph;
	private final double INF = 1e99;
	private int wp = 10;

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public void setMaxPointNum(int maxPointNum) {
		this.wp = maxPointNum;
	}

	public LBS() throws ClassNotFoundException, IOException {
		graph = new Graph();
		graph.loadGraph();
	}

	public void dijikstra(QuerySet qs) {
		Clock.start();
		for (Query q : qs.getQueries()) {
			dijikstra(q);
		}
		Clock.end();
		Clock.show("Dijikstra in ");
	}

	public void dijikstra(Query q) {
		int userEdgeId = q.getWaypoints().get(0).getVertexId();
		int poiEdgeId = q.getWaypoints().get(1).getVertexId();
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
		ArrayList<Vertex> waypoints = new ArrayList<Vertex>();
		endVid = before[poiEdgeId];
		waypoints.add(graph.getVertices().get(poiEdgeId));
		while (endVid != -1 && before[endVid] != -1) {
			waypoints.add(graph.getVertices().get(endVid));
			endVid = before[endVid];
		}
		waypoints.add(q.getWaypoints().get(0));
		for (int i = 0; i < waypoints.size() / 2; i++) {
			Vertex tmp = waypoints.get(i);
			waypoints.set(i, waypoints.get(waypoints.size() - i - 1));
			waypoints.set(waypoints.size() - i - 1, tmp);
		}
		q.setWaypoints(waypoints);
		q.setDist(dist[poiEdgeId]);
	}

	public void initSa(QuerySet qs) {
		Clock.start();
		for (int i = 0; i < qs.size(); i++) {
			if (qs.get(i).getSa() == -1)
				continue;
			for (int j = 0; j < qs.size(); j++) {
				if (i == j)
					continue;
				if (qs.get(j).getSa() == -1)
					continue;
				boolean isIn = qs.get(j).in(qs.get(i));
				if (isIn) {
					qs.get(i).setSa(qs.get(i).getSa() + qs.get(j).getSa());
					qs.get(j).setSa(-1);
				}
			}
		}
		Clock.end();
		int cnt = 0;
		for (Query q : qs.getQueries()) {
			if (q.getSa() == -1) {
				cnt++;
			}
		}
		Clock.show("InitSa in ", ", with " + cnt + "/" + qs.size() + " Shared.");
	}

	public ArrayList<LocationList> mergeByGreedy(QuerySet qs) {
		Clock.start();
		qs.sortByLengthDes();
		ArrayList<LocationList> locLists = new ArrayList<LocationList>();
		boolean[] vis = new boolean[qs.getQueries().size()];
		for (int i = 0; i < qs.getQueries().size(); i++) {
			if (qs.getQueries().get(i).getSa() == -1)
				vis[i] = true;
			else
				vis[i] = false;
		}

		while (true) {
			LocationList llist = new LocationList();
			int cnt = 0;
			for (int i = 0; i < qs.getQueries().size(); i++) {
				if (vis[i])
					continue;
				llist.add(qs.getQueries().get(i).getWaypoints().get(0));
				llist.add(qs.getQueries().get(i).getWaypoints().get(qs.getQueries().get(i).getWaypoints().size() - 1));
				vis[i] = true;
				cnt += 2;
				if (cnt + 2 > wp)
					break;
			}
			if (cnt == 0)
				break;
			for (int i = 1; i + 1 < llist.size(); i += 2) {
				Vertex start = llist.get(i);
				Vertex end = llist.get(i + 1);
				Query tmp = new Query(start, end);
				tmp.getWaypoints().add(start);
				tmp.getWaypoints().add(end);
				dijikstra(tmp);
				for (int j = 0; j < qs.getQueries().size(); j++) {
					if (vis[j])
						continue;
					if (qs.getQueries().get(j).in(tmp)) {
						vis[j] = true;
					}
				}
			}

			locLists.add(llist);
		}
		Clock.end();
		int cnt = 0;
		for (int i = 0; i < locLists.size(); i++) {
			cnt += locLists.get(i).size();
		}
		Clock.show("MergeByGreedy in ", " with " + cnt / 2 + " queries left.");
		return locLists;

	}

	public ArrayList<LocationList> directMerge(QuerySet qs) {
		Clock.start();
		ArrayList<LocationList> res = new ArrayList<LocationList>();
		int cnt = 0;
		LocationList list = new LocationList();
		while (cnt < qs.size()) {
			if (qs.getQueries().get(cnt).getSa() == -1) {
				cnt++;
				if (cnt >= qs.size()) {
					res.add(list);
					list = new LocationList();
				}
				continue;
			}
			list.add(qs.getQueries().get(cnt).getWaypoints().get(0));
			list.add(qs.getQueries().get(cnt).getWaypoints().get(qs.getQueries().get(cnt).getWaypoints().size() - 1));
			cnt++;
			if (list.size() + 2 > wp || cnt >= qs.size()) {
				res.add(list);
				list = new LocationList();
			}
		}
		Clock.end();
		cnt = 0;
		for (int i = 0; i < res.size(); i++) {
			cnt += res.get(i).size();
		}
		Clock.show("DirectMerge in ", " with " + cnt / 2 + " queries left.");
		return res;
	}

	public ArrayList<LocationList> mergeBySelectSort(QuerySet qs) {
		Clock.start();
		qs.sortBySaDesc();
		ArrayList<LocationList> locLists = new ArrayList<LocationList>();
		boolean[] vis = new boolean[qs.getQueries().size()];
		for (int i = 0; i < qs.getQueries().size(); i++) {
			if (qs.getQueries().get(i).getSa() == -1)
				vis[i] = true;
			else
				vis[i] = false;
		}
		while (true) {
			LocationList llist = new LocationList();
			int cnt = 0;
			for (int i = 0; i < qs.getQueries().size(); i++) {
				if (vis[i])
					continue;
				llist.add(qs.getQueries().get(i).getWaypoints().get(0));
				llist.add(qs.getQueries().get(i).getWaypoints().get(qs.getQueries().get(i).getWaypoints().size() - 1));
				vis[i] = true;
				cnt += 2;
				if (cnt + 2 > wp)
					break;
			}
			if (cnt == 0)
				break;
			int num = 1;
			for (int i = 0; i < llist.size() / 2; i++) {
				num *= 2;
			}
			int ansTest = 0;
			int maxSa = 0;
			for (int test = 0; test < num; test++) {
				for (int k = 0; k < llist.size() / 2; k++) {
					if (((1 << k) & test) != 0) {
						Vertex tmp = llist.getList().get(2 * k);
						llist.getList().set(2 * k, llist.getList().get(2 * k + 1));
						llist.getList().set(2 * k + 1, tmp);
					}
				}
				int cc = 0;
				for (int i = 1; i + 1 < llist.size(); i += 2) {
					Vertex start = llist.get(i);
					Vertex end = llist.get(i + 1);
					Query tmp = new Query(start, end);
					tmp.getWaypoints().add(start);
					tmp.getWaypoints().add(end);
					dijikstra(tmp);
					for (int j = 0; j < qs.getQueries().size(); j++) {
						if (vis[j])
							continue;
						if (qs.getQueries().get(j).in(tmp)) {
							cc++;
						}
					}
				}
				if (cc > maxSa) {
					ansTest = test;
				}
				for (int k = 0; k < llist.size() / 2; k++) {
					if (((1 << k) & test) != 0) {
						Vertex tmp = llist.getList().get(2 * k);
						llist.getList().set(2 * k, llist.getList().get(2 * k + 1));
						llist.getList().set(2 * k + 1, tmp);
					}
				}
			}
			for (int k = 0; k < llist.size() / 2; k++) {
				if (((1 << k) & ansTest) != 0) {
					Vertex tmp = llist.getList().get(2 * k);
					llist.getList().set(2 * k, llist.getList().get(2 * k + 1));
					llist.getList().set(2 * k + 1, tmp);
				}
			}
			for (int i = 1; i + 1 < llist.size(); i += 2) {
				Vertex start = llist.get(i);
				Vertex end = llist.get(i + 1);
				Query tmp = new Query(start, end);
				tmp.getWaypoints().add(start);
				tmp.getWaypoints().add(end);
				dijikstra(tmp);
				for (int j = 0; j < qs.getQueries().size(); j++) {
					if (vis[j])
						continue;
					if (qs.getQueries().get(j).in(tmp)) {
						vis[j] = true;
					}
				}
			}
			locLists.add(llist);
		}
		qs.sortByQidAsc();
		Clock.end();
		int cnt = 0;
		for (int i = 0; i < locLists.size(); i++) {
			cnt += locLists.get(i).size();
		}
		Clock.show("MergeByGreedy in ", " with " + cnt / 2 + " queries left.");
		return locLists;
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

		LBS lbs = new LBS();
		QuerySet qs = QuerySet.generateQuerySet(lbs.graph, 5000, 10, 10);
		lbs.dijikstra(qs);
		lbs.initSa(qs);
		lbs.mergeByGreedy(qs);
		lbs.directMerge(qs);
		lbs.mergeBySelectSort(qs);
		System.out.println("done");
	}
}
