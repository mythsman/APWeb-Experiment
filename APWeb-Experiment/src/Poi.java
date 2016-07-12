/**
 * This class represents the Pois.
 * 
 * @author myths
 *
 */
public class Poi extends Location {

	public static final int BarLocations = 0;
	public static final int CafeLocations = 1;
	public static final int FoodLocations = 2;
	public static final int RestaurantLocations = 3;
	public static final int StoreLocations = 4;

	private int source;
	private int pid;

	/**
	 * 
	 * @param longitude
	 *            The longitude of the poi.
	 * @param latitude
	 *            The latitude of the poi.
	 * @param source
	 *            The source of the poi.
	 * @param pid
	 *            The pid of the poi.
	 */
	public Poi(double longitude, double latitude, int source, int pid) {
		super(longitude, latitude);
		this.source = source;
		this.pid = pid;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
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

}
