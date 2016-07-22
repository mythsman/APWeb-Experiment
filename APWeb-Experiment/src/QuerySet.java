import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class QuerySet {

	private ArrayList<Query> queries;
	private int waypointsNum = 10;

	public QuerySet() {
		queries = new ArrayList<Query>();
	}

	public ArrayList<Query> getQueries() {
		return queries;
	}

	public void setQueries(ArrayList<Query> queries) {
		this.queries = queries;
	}

	public static QuerySet generateQuerySet(Graph graph, int size, int bells, int scale) {
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
			qs.queries.addAll(generateQuerySet(graph, sizeofBells[i], scale).getQueries());
		}
		for (int i = 0; i < qs.queries.size(); i++) {
			qs.queries.get(i).setQid(i);
		}
		return qs;
	}

	private static QuerySet generateQuerySet(Graph graph, int size, int scale) {
		QuerySet qs = new QuerySet();
		Random rand = new Random();
		int centerId = rand.nextInt(graph.getVertices().size());
		for (int i = 0; i < size; i++) {
			double gaussian = rand.nextGaussian();
			int startId = centerId + (int) (gaussian * scale);
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

	public Query get(int i) {
		return queries.get(i);
	}

	public int size() {
		return queries.size();
	}

	public int getWaypointsNum() {
		return waypointsNum;
	}

	public void setWaypointsNum(int waypointsNum) {
		this.waypointsNum = waypointsNum;
	}

	/**
	 * Sort by euclidiean length Des;
	 */
	public void sortByLengthDes() {
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

	public void sortBySaDesc() {
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

	public void sortByQidAsc() {
		Collections.sort(queries, new Comparator<Query>() {
			@Override
			public int compare(Query q1, Query q2) {
				if (q1.getQid() > q2.getQid()) {
					return 1;
				} else if (q1.getQid() < q2.getQid()) {
					return -1;
				} else
					return 0;
			}
		});
	}

	public void mergeBySelectSort() {
		sortBySaDesc();
		ArrayList<Query> qs = new ArrayList<Query>();
		ArrayList<Query> rm = new ArrayList<Query>();
		for (Query q : queries) {
			if (q.getSa() == -1) {
				rm.add(q);
			}
		}
		System.out.println(queries.size());
		queries.removeAll(rm);
		System.out.println(queries.size());
		int len = 0;
		while (len < queries.size()) {
			int cnt = 0;
			Query q = new Query(queries.get(len).getUser(), queries.get(len).getPoi());
			len++;
			if (len >= queries.size())
				break;
			cnt++;
			while (cnt < waypointsNum / 2) {
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

	public void mergeByEucliean() {
		sortByLengthDes();
		ArrayList<Query> qs = new ArrayList<Query>();
		int len = 0;
		Random rand = new Random();
		double rate = rand.nextDouble();
		rate = rate * 0.2 + 0.8;
		while (len < queries.size() * rate) {
			int cnt = 0;
			Query q = new Query(queries.get(len).getUser(), queries.get(len).getPoi());
			len++;
			cnt++;
			while (cnt < waypointsNum / 2) {
				q.append(queries.get(len));
				len++;
				cnt++;
				if (len >= queries.size() * rate)
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
				Query q = new Query(queries.get(j).getUser(), queries.get(i).getPoi());
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
