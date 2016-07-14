import java.util.ArrayList;

/**
 * This class represents the vertices in the graph.
 * 
 * @author myths
 *
 */
public class Vertex extends Location{
	private int vid;
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
	public Vertex(double longitude, double latitude, int vid) {
		super(longitude, latitude);
		nearbyEdgeId = new ArrayList<Integer>();
		this.vid = vid;

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
