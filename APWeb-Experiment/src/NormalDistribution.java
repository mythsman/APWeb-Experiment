import java.io.IOException;
import java.util.Random;

public class NormalDistribution extends Distribution {


	@Override
	public Location randomLoc() {
		Random rand = new Random();
		int id = rand.nextInt(graph.getEdges().size());
		return linearInterpolation(graph.getEdges().get(id), rand.nextDouble());
	}

	public static void main(String[] args) throws IOException {
		Graph graph = new Graph();
		graph.loadGraph();
		NormalDistribution dis = new NormalDistribution();
		dis.setGraph(graph);
		System.out.println(dis.randomLoc());
	}
}
