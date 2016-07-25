import java.io.Serializable;
import java.util.ArrayList;

public class Query implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9083546139459335909L;
	private double length;
	private int sa;
	private Location user;
	private Location poi;
	private ArrayList<Vertex> waypoints;
	private int qid;
	private long requestTime;
	private double dist;

	public int getQid() {
		return qid;
	}

	public void setQid(int qid) {
		this.qid = qid;
	}

	public long getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}

	public Query(Location user, Location poi) {
		this.user = user;
		this.poi = poi;
		sa = 1;
		waypoints = new ArrayList<Vertex>();
		this.length = user.distance(poi);
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

	public int getSa() {
		return sa;
	}

	public void setSa(int sa) {
		this.sa = sa;
	}

	public double getLength() {
		return length;
	}

	public Location getUser() {
		return user;
	}

	public void append(Query q) {
		// waypoints.add(poi);
		// waypoints.add(q.getUser());
	}

	public Location getPoi() {
		return poi;
	}

	public ArrayList<Vertex> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(ArrayList<Vertex> waypoints) {
		this.waypoints = waypoints;
	}

	@Override
	public String toString() {
		String s = user + "->";
		for (Location loc : waypoints) {
			s += loc + "->";
		}
		s += poi;
		return s;
	}

	public boolean in(Query query) {
		ArrayList<Vertex> list = query.getWaypoints();
		boolean userIn = false, poiIn = false;
		for (Location loc : list) {
			if (loc.distance(getWaypoints().get(0)) < 1) {
				userIn = true;
			}
			if (loc.distance(getWaypoints().get(getWaypoints().size() - 1)) < 1) {
				poiIn = true;
			}
		}
		return userIn && poiIn;
	}

}
