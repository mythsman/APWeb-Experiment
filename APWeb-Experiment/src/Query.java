import java.util.ArrayList;

public class Query {
	private int qid;
	private double length;
	private int sa;
	private Location user;
	private Poi poi;
	private ArrayList<Location> waypoints;

	private double dist;

	public Query(Location user, Poi poi, int qid) {
		this.user = user;
		this.poi = poi;
		this.qid = qid;
		sa = 1;
		waypoints = new ArrayList<Location>();
		this.length = user.distanceToLoc(poi);
	}

	public Query(Location user, Poi poi) {
		this(user, poi, -1);
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

	public int getQid() {
		return qid;
	}

	public double getLength() {
		return length;
	}

	public Location getUser() {
		return user;
	}

	public void append(Query q) {
		waypoints.add(poi);
		waypoints.add(q.getUser());
		poi = q.getPoi();
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
		ArrayList<Location> list = new ArrayList<Location>();
		list.add(query.getUser());
		list.addAll(query.getWaypoints());
		list.add(query.getPoi());
		boolean userIn = false, poiIn = false;
		for (Location loc : list) {
			if (loc.distanceToLoc(user) < 1) {
				userIn = true;
			}
			if (loc.distanceToLoc(poi) < 1) {
				poiIn = true;
			}
		}
		return userIn && poiIn;
	}

}
