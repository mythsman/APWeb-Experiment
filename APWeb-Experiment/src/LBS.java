import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;;

public class LBS {
	private Graph graph;

	public LBS() throws ClassNotFoundException, IOException {

		System.out.println("Generate object file.");
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
	private QuerySet getQuerySet(int num, Distribution dis) {
		dis.setGraph(graph);
		QuerySet querySet = new QuerySet(graph);
		ArrayList<Location> locs = dis.randomList(num);
		Random rand = new Random();
		for (int i = 0; i < num; i++) {
			querySet.add(new Query(locs.get(i), graph.getPois().get(rand.nextInt(graph.getPois().size())), i));
		}
		return querySet;
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		LBS lbs = new LBS();
		for (int i = 200; i <= 2000; i += 200) {
			QuerySet querySet = lbs.getQuerySet(i, new GaussianDistribution());
			querySet.initPath();
			querySet.initSa();
			querySet.testSaNum();
		}
		System.out.println("done");
	}
}
