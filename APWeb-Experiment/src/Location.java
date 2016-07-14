import java.io.Serializable;

/**
 * This class is the father of all kinds of points.
 * 
 * @author myths
 *
 */
public class Location implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final double EARTH_RADIUS = 6378160;
	private int inEdgeId;
	protected double longitude, latitude;

	/**
	 * 
	 * @param longitude
	 *            The longitude of the location , positive ones represent the
	 *            east,negative ones represent the west.(degree measure)
	 * @param latitude
	 *            The latitude of the location, positive ones represent the
	 *            north,negative ones represent the south.(degree measure)
	 */
	public Location(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public double radians(double angle) {
		return angle * Math.PI / 180;
	}

	public int getInEdgeId() {
		return inEdgeId;
	}

	public void setInEdgeId(int inEdgeId) {
		this.inEdgeId = inEdgeId;
	}

	/**
	 * Calculate the spherical distance between two locations.
	 * 
	 * @param loc
	 *            Another location
	 * @return The spherical distance between the two locations(meters).
	 */
	public double sphericalDistance(Location loc) {
		double x = radians(this.longitude), y = radians(this.latitude);
		double a = radians(loc.longitude), b = radians(loc.latitude);
		if (isZero(EARTH_RADIUS * (x - a)) && isZero(EARTH_RADIUS * (y - b))) {
			return 0;
		}
		return EARTH_RADIUS * Math.acos(Math.cos(b) * Math.cos(y) * Math.cos(a - x) + Math.sin(b) * Math.sin(y));
	}

	private boolean isZero(double d) {
		return d * d < 1;
	}

	/**
	 * Calculate the distance between the vertex and the edge.
	 * 
	 * @param edge
	 *            The concerned edge.
	 * @return The distance from the vertex to the edge.
	 */
	public double distanceToEdge(Location v1, Location v2) {
		double d1 = this.sphericalDistance(v1);
		double d2 = this.sphericalDistance(v2);
		if (isZero(d1) || isZero(d2)) {
			return 0;
		}
		double d = v1.sphericalDistance(v2);
		double cos1 = (d1 * d1 + d * d - d2 * d2) / (2.0 * d1 * d);
		double cos2 = (d2 * d2 + d * d - d1 * d1) / (2.0 * d2 * d);
		if (cos1 < 0) {
			return d1;
		} else if (cos2 < 0) {
			return d2;
		} else {
			double p = (d + d1 + d2) / 2.0;
			if ((p - d) * (p - d1) * (p - d2) < 0) {
				return 0;
			} else {
				return Math.sqrt(p * (p - d) * (p - d1) * (p - d2)) * 2.0 / d;
			}
		}

	}

	public Location projection(Location loc1, Location loc2) {
		if (in(loc1, loc2)) {
			return this;
		} else {
			double d1 = this.sphericalDistance(loc1);
			double d2 = this.sphericalDistance(loc2);
			double d = loc1.sphericalDistance(loc2);
			double cos1 = d1 * d1 + d * d - d2 * d2;
			double cos2 = d2 * d2 + d * d - d1 * d1;
			if (cos1 < 0) {
				return loc1;
			} else if (cos2 < 0) {
				return loc2;
			} else {
				double x = getLongitude(), y = getLatitude();
				double x1 = loc1.getLongitude(), y1 = loc1.getLatitude();
				double x2 = loc2.getLongitude(), y2 = loc2.getLatitude();
				double rate = 1 - ((y - y1) * (y2 - y1) + (x - x1) * (x2 - x1))
						/ ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
				return new Location((1 - rate) * x1 + rate * x2, (1 - rate) * y1 + rate * y2);
			}
		}
	}

	public boolean in(Location loc1, Location loc2) {
		return isZero(distanceToEdge(loc1, loc2));
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public boolean equals(Location loc) {
		return (longitude - loc.longitude) * (longitude - loc.longitude) < 1e-9
				&& (latitude - loc.latitude) * (latitude - loc.latitude) < 1e-9;
	}

	@Override
	public String toString() {
		return "(" + longitude + "," + latitude + ")";
	}

	public static void main(String[] args) {
		Location loc = new Location(-93.277618, 44.915874);
		Location loc1 = new Location(-93.269224, 44.960969);
		Location loc2 = new Location(-93.269224, 44.960165);
		System.out.println(loc.distanceToEdge(loc1, loc2));
	}

}
