import java.util.ArrayList;

public class LocationList {
	private ArrayList<Vertex> list;

	private long responseTime;

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public LocationList() {
		list = new ArrayList<Vertex>();
	}

	public ArrayList<Vertex> getList() {
		return list;
	}

	public void setList(ArrayList<Vertex> list) {
		this.list = list;
	}

	public void add(Vertex loc) {
		list.add(loc);
	}

	public Vertex get(int index) {
		return list.get(index);
	}

	public int size() {
		return list.size();
	}
}