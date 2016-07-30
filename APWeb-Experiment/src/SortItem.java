public class SortItem implements Comparable<SortItem> {
	private int vid;
	private double dist;

	public SortItem(int vid, double dist) {
		this.vid = vid;
		this.dist = dist;
	}

	public int getVid() {
		return vid;
	}

	@Override
	public int compareTo(SortItem item) {
		if (dist < item.dist)
			return -1;
		else if (dist > item.dist)
			return 1;
		else
			return 0;
	}
}