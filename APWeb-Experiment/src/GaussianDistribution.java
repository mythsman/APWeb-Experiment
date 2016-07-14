import java.util.Random;

public class GaussianDistribution extends Distribution {

	@Override
	public Location randomLoc() {
		Random rand = new Random();
		Double gaussian = rand.nextGaussian();
		final double rate = 10;
		int id = -1;
		while (id < 0 || id >= graph.getEdges().size()) {
			id = (int) (gaussian * rate) + graph.getEdges().size() / 2;
		}
		Location loc = linearInterpolation(graph.getEdges().get(id), rand.nextDouble());
		loc.setInEdgeId(id);
		return loc;
	}

}
