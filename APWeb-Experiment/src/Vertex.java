import java.util.ArrayList;

/**
 * This class represents the vertices in the graph.
 * 
 * @author myths
 *
 */
public class Vertex extends Location{
	private int vid;
	private int degree;
	private ArrayList<Integer> nearbyEdgeId;

	/**
	 * 
	 * @param longitude
	 *            The longitude of the vertex.
	 * @param latitude
	 *            The latitude of the vertex.
	 * @param vid
	 *            The vid of the vertex.
	 * @param degree
	 *            The degree of the vertex.
	 */
	public Vertex(double longitude, double latitude, int vid, int degree) {
		super(longitude, latitude);
		nearbyEdgeId = new ArrayList<Integer>();
		this.vid = vid;
		this.degree = degree;
	}

	public ArrayList<Integer> getNearbyEdgeId() {
		return nearbyEdgeId;
	}

	public int getVid() {
		return vid;
	}

	public void setVid(int vid) {
		this.vid = vid;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	/**
	 * Add an edge that is connected to the vertex.
	 * 
	 * @param id
	 *            The eid of the edge.
	 */
	public void addEdgeId(int id) {
		nearbyEdgeId.add(id);
	}
}
