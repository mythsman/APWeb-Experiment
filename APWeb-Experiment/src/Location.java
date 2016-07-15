import java.io.Serializable;

/**
 * This class is the father of all kinds of points.
 * 
 * @author myths
 *
 */
public class Location implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final double LONGITUDE_RADIUS = 6378160;
	private static final double LATITUDE_RADIUS = 4510040;
	private int inVertexId;
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

	public int getInVertexId() {
		return inVertexId;
	}

	public void setInVertexId(int inVertexId) {
		this.inVertexId = inVertexId;
	}

	/**
	 * Calculate the spherical distance between two locations.
	 * 
	 * @param loc
	 *            Another location
	 * @return The spherical distance between the two locations(meters).
	 */
	public double distanceToLoc(Location loc) {
		double x = radians(this.longitude), y = radians(this.latitude);
		double a = radians(loc.longitude), b = radians(loc.latitude);
		double dLon = (x - a) * LONGITUDE_RADIUS;
		double dLat = (y - b) * LATITUDE_RADIUS;
		return Math.sqrt(dLat * dLat + dLon * dLon);
	}

	public Location projection(Location loc1, Location loc2) {
		if (in(loc1, loc2)) {
			return this;
		} else {
			double d1 = this.distanceToLoc(loc1);
			double d2 = this.distanceToLoc(loc2);
			double d = loc1.distanceToLoc(loc2);
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

		double eps = (latitude - loc1.latitude) * (longitude - loc2.latitude)
				- (longitude - loc1.latitude) * (latitude - loc2.latitude);
		return (latitude - loc1.latitude) * (latitude - loc2.latitude) <= 0
				&& (longitude - loc1.longitude) * (longitude - loc2.longitude) <= 0 && eps * eps < 1e-18;

		// return isZero(distanceToEdge(loc2, loc1));
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
		System.out.println(loc.distanceToLoc(loc1));
	}

}
