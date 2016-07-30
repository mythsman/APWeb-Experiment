import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class QuerySet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1096428460049959612L;
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
