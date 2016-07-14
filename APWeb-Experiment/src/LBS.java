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
		QuerySet querySet = lbs.getQuerySet(600, new NormalDistribution());
		long t1 = System.currentTimeMillis();
		querySet.initPath();
		long t2 = System.currentTimeMillis();
		System.out.println(t2 - t1 + "ms");
		querySet.initSa();
		System.out.println("start");
		long t3 = System.currentTimeMillis();
		System.out.println(t3 - t2 + "ms");
		/*
		for (int i = 0; i < querySet.size(); i++) {
			if (querySet.get(i).getSa() > 1)
				System.out.println(querySet.get(i).getSa());
		}
		*/
		System.out.println("end");
	}
}
