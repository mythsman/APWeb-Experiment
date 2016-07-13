import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;;

public class LBS {
	private Graph graph;

	public LBS() throws IOException {
		graph = new Graph();
		graph.loadGraph();
	}

	/**
	 * Get a list of queries
	 * 
	 * @param num
	 *            number of the list.
	 * @param dis
	 *            distribution.
	 * @return a list
	 */
	private ArrayList<Query> getQuerylist(int num, Distribution dis) {
		dis.setGraph(graph);
		ArrayList<Query> queries = new ArrayList<Query>();
		ArrayList<Location> locs = dis.randomList(num);
		Random rand = new Random();
		for (int i = 0; i < num; i++) {
			queries.add(new Query(locs.get(i), graph.getPois().get(rand.nextInt(num)), i));
		}
		return queries;
	}

	private void findPath(Query query) {
		Location start = query.getUser();
		Location end = query.getPoi();

		Vertex start1 = null, start2 = null;
		for (int i = 0; i < graph.getEdges().size(); i++) {
			if (start.in(graph.getEdges().get(i).getSvertex(), graph.getEdges().get(i).getEvertex())) {
				start1 = graph.getEdges().get(i).getSvertex();
				start2 = graph.getEdges().get(i).getEvertex();
				break;
			}
		}
		Vertex end1 = null, end2 = null;
		for (int i = 0; i < graph.getEdges().size(); i++) {
			if (end.in(graph.getEdges().get(i).getSvertex(), graph.getEdges().get(i).getEvertex())) {
				end1 = graph.getEdges().get(i).getSvertex();
				end2 = graph.getEdges().get(i).getEvertex();
				break;
			}
		}

		int len = graph.getVertices().size();
		double[] dist = new double[len];
		boolean[] vis = new boolean[len];
		int[] before = new int[len];
		for (int i = 0; i < len; i++) {
			dist[i] = 1e9;
			vis[i] = false;
			before[i] = -1;
		}
		try {
			dist[start1.getVid()] = start1.sphericalDistance(start);
			dist[start2.getVid()] = start2.sphericalDistance(start);
		} catch (Exception e) {
			
		}
		vis[start1.getVid()] = true;
		vis[start2.getVid()] = true;
		for (int i = 0; i < len; i++) {
			for (int eid : start1.getNearbyEdgeId()) {
				int vid = graph.getEdges().get(eid).except(start1).getVid();
				if (vis[vid])
					continue;
				if (dist[vid] > dist[start1.getVid()] + graph.getEdges().get(eid).getLength()) {
					dist[vid] = dist[start1.getVid()] + graph.getEdges().get(eid).getLength();
					before[vid] = start1.getVid();
				}
			}
		}
		for (int i = 0; i < len; i++) {
			for (int eid : start2.getNearbyEdgeId()) {
				int vid = graph.getEdges().get(eid).except(start2).getVid();
				if (vis[vid])
					continue;
				if (dist[vid] > dist[start2.getVid()] + graph.getEdges().get(eid).getLength()) {
					dist[vid] = dist[start2.getVid()] + graph.getEdges().get(eid).getLength();
					before[vid] = start2.getVid();
				}
			}
		}

		for (int cnt = 0; cnt < len; cnt++) {
			double mindis = 1e9;
			int minId = -1;
			for (int i = 0; i < len; i++) {
				if (!vis[i] && dist[i] < mindis) {
					mindis = dist[i];
					minId = i;
				}
			}
			if (mindis == 1e9)
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
		} else {
			endVid = end2.getVid();
		}
		ArrayList<Location> waypoints = new ArrayList<Location>();
		endVid = end1.getVid();
		while (endVid != -1) {
			waypoints.add(graph.getVertices().get(endVid));
			endVid = before[endVid];
		}
		query.setWaypoints(waypoints);
	}

	public static void main(String[] args) throws IOException {
		LBS lbs = new LBS();
		ArrayList<Query> queries = lbs.getQuerylist(100, new NormalDistribution());
		for (int i = 0; i < 100; i++) {
			Query query = queries.get(i);
			lbs.findPath(query);
			System.out.println("=====print path " + i + "======");
			for (Location loc : query.getWaypoints()) {
				System.out.println(loc);
			}
			System.out.println("done");
		}
	}
}
