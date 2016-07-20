import java.util.Random;

public class GaussianDistribution extends Distribution {

	@Override
	public Location randomLoc() {
		Random rand = new Random();
		Double gaussian = rand.nextGaussian();
		double number = 500;
		int id = -1;
		while (id < 0 || id >= graph.getVertices().size()) {
			id = (int) (gaussian * number / 6.0) + graph.getVertices().size() / 2;
			gaussian = rand.nextGaussian();
		}

		Location loc = new Location(graph.getVertices().get(id).getLongitude(),
				graph.getVertices().get(id).getLatitude());
		loc.setInVertexId(id);
		return loc;
	}
}
