import java.io.Serializable;

/**
 * This class represents the edges in the graph.
 * 
 * @author myths
 *
 */
public class Edge implements Comparable<Object>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private int eid;
	private double length;
	private Vertex svertex, evertex;

	/**
	 * 
	 * @param svertex
	 *            The start vertex.
	 * @param evertex
	 *            The end vertex.
	 * @param eid
	 *            The edge's eid.
	 */
	public Edge(Vertex svertex, Vertex evertex, int eid) {
		this.svertex = svertex;
		this.evertex = evertex;
		this.eid = eid;
		this.length = svertex.sphericalDistance(evertex);
	}

	public int getEid() {
		return eid;
	}

	public double getLength() {
		return length;
	}

	public Vertex getSvertex() {
		return svertex;
	}

	public Vertex getEvertex() {
		return evertex;
	}

	public Vertex except(Vertex v) {
		if (v.equals(evertex)) {
			return svertex;
		} else {
			return evertex;
		}
	}

	@Override
	public int compareTo(Object o) {
		Edge edge = (Edge) o;
		if (this.eid < edge.eid)
			return 1;
		else if (this.eid < edge.eid)
			return -1;
		else
			return 0;
	}

}
