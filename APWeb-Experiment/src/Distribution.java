import java.util.ArrayList;

/**
 * The father class of all distributions.
 * 
 * @author myths
 *
 */
public abstract class Distribution {
	protected Graph graph;

	public Distribution() {

	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	/**
	 * Find a location in an edge.
	 * 
	 * @param edge
	 *            an edge
	 * @param rate
	 *            [0,1]
	 * @return a location
	 */
	protected Location linearInterpolation(Edge edge, double rate) {
		Vertex v1 = edge.getSvertex();
		Vertex v2 = edge.getEvertex();
		Location res = new Location((1 - rate) * v1.getLongitude() + rate * v2.getLongitude(),
				(1 - rate) * v1.getLatitude() + rate * v2.getLatitude());
		return res;
	}

	/**
	 * Get a random location.
	 * 
	 * @return A location in the graph.
	 */
	public abstract Location randomLoc();

	/**
	 * Get a random list of locations with the length of 'num'.
	 * 
	 * @param num
	 *            The length of the list.
	 * @return The list.
	 */
	public ArrayList<Location> randomList(int num) {
		ArrayList<Location> list = new ArrayList<Location>();
		for (int i = 0; i < num; i++) {
			list.add(randomLoc());
		}
		return list;
	}
}
