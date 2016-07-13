import java.util.ArrayList;

public class Query {
	private int qid;
	private double length;
	private int sa;
	private Location user;
	private Poi poi;
	private ArrayList<Location> waypoints;

	public Query(Location user, Poi poi, int qid) {
		this.user = user;
		this.poi = poi;
		this.qid = qid;
		waypoints = new ArrayList<Location>();
		this.length = user.sphericalDistance(poi);
	}

	public void calPath(Graph graph) {

	}

	public int getSa() {
		return sa;
	}

	public void setSa(int sa) {
		this.sa = sa;
	}

	public int getQid() {
		return qid;
	}

	public double getLength() {
		return length;
	}

	public Location getUser() {
		return user;
	}

	public Poi getPoi() {
		return poi;
	}

	public ArrayList<Location> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(ArrayList<Location> waypoints) {
		this.waypoints = waypoints;
	}

}
