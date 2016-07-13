/**
 * This class represents the Pois.
 * 
 * @author myths
 *
 */
public class Poi extends Location {

	private int pid;

	/**
	 * 
	 * @param longitude
	 *            The longitude of the poi.
	 * @param latitude
	 *            The latitude of the poi.
	 * @param pid
	 *            The pid of the poi.
	 */
	public Poi(double longitude, double latitude, int pid) {
		super(longitude, latitude);
		this.pid = pid;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public double distanceToEdge(Edge edge) {

		return 0;
	}

	@Override
	public Poi projection(Location loc1, Location loc2) {
		Location loc = super.projection(loc1, loc2);
		longitude = loc.getLongitude();
		latitude = loc.getLatitude();
		return this;

	}

}
