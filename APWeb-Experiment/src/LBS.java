import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;;

public class LBS {
	private Graph graph;

	public LBS() throws ClassNotFoundException, IOException {
		File file = null;
		try {
			file = new File("dataset/GraphObject.bin");
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			System.out.println("Load object file.");
			graph = (Graph) in.readObject();
			in.close();

		} catch (FileNotFoundException e) {
			System.out.println("Generate object file.");
			graph = new Graph();
			graph.loadGraph();
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(graph);
			out.flush();
			out.close();
		}
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
		for (int val = 200; val <= 2000; val += 200) {
			QuerySet querySet = lbs.getQuerySet(val, new GaussianDistribution());
			querySet.initPath();
			querySet.initSa();
			querySet.testSaNum();
		}
	}
}
