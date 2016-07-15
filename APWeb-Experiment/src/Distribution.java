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
