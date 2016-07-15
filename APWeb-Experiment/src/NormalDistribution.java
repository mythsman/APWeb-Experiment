import java.io.IOException;
import java.util.Random;

public class NormalDistribution extends Distribution {

	@Override
	public Location randomLoc() {
		Random rand = new Random();
		int id = rand.nextInt(graph.getVertices().size());
		Location loc = new Location(graph.getVertices().get(id).getLongitude(),
				graph.getVertices().get(id).getLatitude());
		loc.setInVertexId(id);
		return loc;
	}

	public static void main(String[] args) throws IOException {
		Graph graph = new Graph();
		graph.loadGraph();
		NormalDistribution dis = new NormalDistribution();
		dis.setGraph(graph);
		System.out.println(dis.randomLoc());
	}
}
