/**
 * This class is the father of all kinds of points.
 * 
 * @author myths
 *
 */
public class Location {
	public static final double EARTH_RADIUS = 6378160;
	private double longitude, latitude;

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
		if (isZero(x - a) && isZero(y - b)) {
			return 0;
		}
		return EARTH_RADIUS * Math.acos(Math.cos(b) * Math.cos(y) * Math.cos(a - x) + Math.sin(b) * Math.sin(y));
	}

	private boolean isZero(double d) {
		return d * d < 1e-6;
	}

	/**
	 * Calculate the distance between the vertex and the edge.
	 * 
	 * @param edge
	 *            The concerned edge.
	 * @return The distance from the vertex to the edge.
	 */
	public double distanceToEdge(Edge edge) {
		Vertex v1 = edge.getSvertex();
		Vertex v2 = edge.getEvertex();
		double d1 = this.sphericalDistance(v1);
		double d2 = this.sphericalDistance(v2);
		if (isZero(d1) || isZero(d2)) {
			return 0;
		}
		double d = edge.getLength();
		double cos1 = (d1 * d1 + d * d - d2 * d2) / (2.0 * d1 * d);
		double cos2 = (d2 * d2 + d * d - d1 * d1) / (2.0 * d2 * d);
		if (cos1 < 0) {
			return d1;
		} else if (cos2 < 0) {
			return d2;
		} else {
			double p = (d + d1 + d2) / 2.0;
			double dis = Math.sqrt(p * (p - d) * (p - d1) * (p - d2)) * 2.0 / d;
			return dis;
		}

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

	public static void main(String[] args) {

	}

}
