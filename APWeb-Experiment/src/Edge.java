import java.io.Serializable;
import java.util.Random;

/**
 * This class represents the edges in the graph.
 * 
 * @author myths
 *
 */
public class Edge implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8206488135922879477L;
	private double length;
	private Vertex svertex, evertex;

	/**
	 * 
	 * @param svertex
	 *            The start vertex.
	 * @param evertex
	 *            The end vertex.
	 * @param eid
	 *            The edge's eid.
	 */
	public Edge(Vertex svertex, Vertex evertex) {
		this.svertex = svertex;
		this.evertex = evertex;
		this.length = svertex.distance(evertex);
	}

	public Location getRandomLocation() {
		double rand = new Random().nextDouble();
		double longitude = rand * svertex.getLongitude() + (1 - rand) * evertex.getLongitude();
		double latitude = rand * svertex.getLatitude() + (1 - rand) * evertex.getLatitude();
		return new Location(longitude, latitude);
	}

	public double getLength() {
		return length;
	}

	public Vertex getSvertex() {
		return svertex;
	}

	public Vertex getEvertex() {
		return evertex;
	}

	public Vertex except(Vertex v) {
		if (v.equals(evertex)) {
			return svertex;
		} else {
			return evertex;
		}
	}

}
