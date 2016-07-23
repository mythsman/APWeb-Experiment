import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

	public QuerySet generateQuerySet(int size, int bells, int scale) {
		Clock.getClock("GenerateQuerySet").start();
		QuerySet qs = new QuerySet();
		int[] sizeofBells = new int[bells];
		double k = 0;
		for (int i = 1; i <= bells; i++) {
			k += 1.0 / i;
		}
		int remain = size;
		for (int i = 0; i < bells; i++) {
			sizeofBells[i] = (int) (size * 1.0 / k / (1 + i));
			remain -= sizeofBells[i];
		}
		sizeofBells[bells - 1] += remain;
		for (int i = 0; i < bells; i++) {
			qs.getQueries().addAll(generateQuerySet(sizeofBells[i], scale).getQueries());
		}
		for (int i = 0; i < qs.getQueries().size(); i++) {
			qs.getQueries().get(i).setQid(i);
		}
		Clock.getClock("GenerateQuerySet").end();
		Clock.getClock("GenerateQuerySet").show("Generate QuerySet in ");
		return qs;
	}

	// private QuerySet generateQuerySet(int size, int scale) {
	// QuerySet qs = new QuerySet();
	// Random rand = new Random();
	// int centerId = rand.nextInt(graph.getVertices().size());
	// for (int i = 0; i < size; i++) {
	// double gaussian = rand.nextGaussian();
	// int startId = centerId + (int) (gaussian * scale);
	// while (startId < 0) {
	// startId += graph.getVertices().size();
	// }
	// while (startId >= graph.getVertices().size()) {
	// startId -= graph.getVertices().size();
	// }
	// int endId = rand.nextInt(graph.getPois().size());
	// ArrayList<Integer> edgeIds =
	// graph.getVertices().get(startId).getNearbyEdgeId();
	// Location user =
	// graph.getEdges().get(edgeIds.get(rand.nextInt(edgeIds.size()))).getRandomLocation();
	// Query q = new Query(user, graph.getPois().get(endId));
	// q.getWaypoints().add(graph.getVertices().get(startId));
	// q.getWaypoints().add(graph.getVertices().get(graph.getPois().get(endId).getVertexId()));
	// qs.getQueries().add(q);
	// }
	// return qs;
	// }
	private QuerySet generateQuerySet(int size, int scale) {
		QuerySet qs = new QuerySet();
		Random rand = new Random();
		int centerId = rand.nextInt(graph.getVertices().size());
		List<SortItem> list = new ArrayList<SortItem>();
		for (int i = 0; i < graph.getVertices().size(); i++) {
			SortItem item = new SortItem(i, graph.getVertices().get(i).distance(graph.getVertices().get(centerId)));
			list.add(item);
		}
		Collections.sort(list);
		for (int i = 0; i < size; i++) {
			double gaussian = rand.nextGaussian();
			if (gaussian < 0)
				gaussian = -gaussian;

			int startId = centerId + list.get((int) (gaussian * scale)).getVid();
			while (startId < 0) {
				startId += graph.getVertices().size();
			}
			while (startId >= graph.getVertices().size()) {
				startId -= graph.getVertices().size();
			}
			int endId = rand.nextInt(graph.getPois().size());
			ArrayList<Integer> edgeIds = graph.getVertices().get(startId).getNearbyEdgeId();
			Location user = graph.getEdges().get(edgeIds.get(rand.nextInt(edgeIds.size()))).getRandomLocation();
			Query q = new Query(user, graph.getPois().get(endId));
			q.getWaypoints().add(graph.getVertices().get(startId));
			q.getWaypoints().add(graph.getVertices().get(graph.getPois().get(endId).getVertexId()));
			qs.getQueries().add(q);
		}
		return qs;
	}

	public void dijikstra(QuerySet qs) {
		Clock.getClock("dijikstra").start();
		for (Query q : qs.getQueries()) {
			dijikstra(q);
		}
		Clock.getClock("dijikstra").end();
		Clock.getClock("dijikstra").show("Dijikstra in ");
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
		Clock.getClock("initSa").start();
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
		Clock.getClock("initSa").end();
		int cnt = 0;
		for (Query q : qs.getQueries()) {
			if (q.getSa() == -1) {
				cnt++;
			}
		}
		Clock.getClock("initSa").show("InitSa in ", ", with " + (qs.size() - cnt) + "/" + qs.size() + " left.");
	}

	public boolean nextPermutation(ArrayList<Integer> arr) {
		boolean mod = false;
		for (int i = 0; i < arr.size() - 1; i++) {
			if (arr.get(i) > arr.get(i + 1)) {
				int swp = arr.get(i);
				arr.set(i, arr.get(i + 1));
				arr.set(i + 1, swp);
				mod = true;
			}
		}
		return mod;
	}

	public ArrayList<LocationList> mergeBySelectSort(QuerySet qs) {
		Clock.getClock("mergeBySelectSort").start();
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

			Clock.getClock("mergeBySelectSort").end();
			Clock.getClock("inDijikstra2").start();
			Query[][] queries = new Query[llist.size() / 2][llist.size() / 2];
			for (int i = 0; i < llist.size() / 2; i++) {
				for (int j = 0; j < llist.size() / 2; j++) {
					Vertex start = llist.get(2 * i + 1);
					Vertex end = llist.get(2 * j);
					Query tmp = new Query(start, end);
					tmp.getWaypoints().add(start);
					tmp.getWaypoints().add(end);
					dijikstra(tmp);
					queries[i][j] = tmp;
				}
			}
			Clock.getClock("inDijikstra2").end();
			Clock.getClock("mergeBySelectSort").start();
			ArrayList<Integer> arr = new ArrayList<Integer>();
			ArrayList<Integer> res = new ArrayList<Integer>();
			for (int i = 0; i < llist.size() / 2; i++) {
				arr.add(i);
				res.add(i);
			}
			int maxi = 0;
			do {
				int cc = 0;
				for (int i = 0; i < llist.size() / 2 - 1; i++) {
					Query tmp = queries[arr.get(i)][arr.get(i + 1)];
					for (int j = 0; j < qs.getQueries().size(); j++) {
						if (vis[j])
							continue;
						if (qs.getQueries().get(j).in(tmp)) {
							cc++;
						}
					}
				}
				if (cc > maxi) {
					for (int i = 0; i < llist.size() / 2; i++) {
						res.set(i, arr.get(i));
					}
				}
			} while (nextPermutation(arr));
			LocationList newList = new LocationList();
			for (int i = 0; i < llist.size() / 2 - 1; i++) {
				newList.add(llist.get(2 * res.get(i)));
				newList.add(llist.get(2 * res.get(i) + 1));
			}
			llist = newList;

			locLists.add(llist);
		}
		Clock.getClock("mergeBySelectSort").end();
		int cnt = 0;
		for (int i = 0; i < locLists.size(); i++) {
			cnt += locLists.get(i).size();
		}
		Clock.getClock("mergeBySelectSort").show("mergeBySelectSort in ",
				" with " + cnt / 2 + "/" + qs.size() + " queries left.");
		Clock.getClock("inDijikstra2").show("inDijikstra2 in ");
		return locLists;
	}

	public ArrayList<LocationList> mergeByGreedy(QuerySet qs) {
		Clock.getClock("directMerge").start();
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
		Clock.getClock("directMerge").end();
		cnt = 0;
		for (int i = 0; i < res.size(); i++) {
			cnt += res.get(i).size();
		}
		Clock.getClock("directMerge").show("DirectMerge in ", " with " + cnt / 2 + "/" + qs.size() + " queries left.");
		return res;
	}

	public void request(QuerySet qs, ArrayList<LocationList> list, int par) throws InterruptedException {
		Clock.getClock("Request").start();
		Request[] req = new Request[list.size()];
		for (int i = 0; i < list.size(); i++) {
			req[i] = new Request(list.get(i));
			req[i].start();
			if ((i + 1) % par == 0) {
				for (int j = i - par + 1; j <= i; j++) {
					req[j].join();
				}
			}
		}
		for (int i = 0; i < list.size(); i++) {
			req[i].join();
		}

		int cnt = 0;
		for (int i = 0; i < qs.size(); i++) {
			Vertex start = qs.getQueries().get(i).getWaypoints().get(0);
			Vertex end = qs.getQueries().get(i).getWaypoints().get(qs.getQueries().get(i).getWaypoints().size() - 1);
			boolean startIn = false, endIn = false;
			for (LocationList llist : list) {
				for (int j = 0; j < llist.size() - 1; j++) {
					double d = llist.get(j).distance(llist.get(j + 1));
					double d1 = llist.get(j).distance(start);
					double d2 = llist.get(j + 1).distance(start);
					if (d1 + d2 - d <= 10) {
						startIn = true;
					}
					d1 = llist.get(j).distance(end);
					d2 = llist.get(j + 1).distance(end);
					if (d1 + d2 - d <= 20) {
						endIn = true;
					}
				}
			}
			if (startIn && endIn)
				cnt++;
		}
		System.out.println("Target " + cnt + "/" + qs.size());
		Clock.getClock("Request").end();
		Clock.getClock("Request").show("Request totally in ");
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		LBS lbs = new LBS();
		QuerySet qs = lbs.generateQuerySet(2000, 10, 10);
		lbs.dijikstra(qs);
		lbs.initSa(qs);
		lbs.setMaxPointNum(10);
		ArrayList<LocationList> list;
		list = lbs.mergeBySelectSort(qs);
		lbs.request(qs, list, 300);
		list = lbs.mergeByGreedy(qs);
		lbs.request(qs, list, 300);
		System.out.println("done");
	}
}
