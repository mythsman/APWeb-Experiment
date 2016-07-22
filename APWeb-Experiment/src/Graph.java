import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The Graph.
 * 
 * @author myths
 *
 */
public class Graph implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1974866688573230523L;
	private ArrayList<Vertex> vertices;
	private ArrayList<Edge> edges;
	private ArrayList<Location> pois;

	public Graph() {
		vertices = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		pois = new ArrayList<Location>();
	}

	/**
	 * Load vertices from the file.
	 * 
	 * @param file
	 *            file
	 * @throws IOException
	 *             ioexception
	 */
	private void loadVertices(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		int cnt = 0;
		while ((line = reader.readLine()) != null) {
			String[] items = line.split("\t");
			int vid = Integer.parseInt(items[0]);
			double longitude = Double.parseDouble(items[1]) / 1000000 - 180;
			double latitude = 90 - Double.parseDouble(items[2]) / 1000000;
			int degree = Integer.parseInt(items[3]);
			Vertex vertex = new Vertex(longitude, latitude, vid);
			for (int i = 0; i < degree; i++) {
				line = reader.readLine();
				String[] subItems = line.split("\t");
				vertex.addEdgeId(Integer.parseInt(subItems[0]));
			}
			vertex.setVertexId(cnt++);
			vertices.add(vertex);

		}
		reader.close();
	}

	/**
	 * Load edges from file.
	 * 
	 * @param file
	 *            file
	 * @throws IOException
	 *             ioexception
	 */
	private void loadEdges(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.endsWith("-1"))
				continue;
			String[] items = line.split("\t");
			int svid = Integer.parseInt(items[6]);
			int evid = Integer.parseInt(items[7]);
			Edge edge = new Edge(vertices.get(svid), vertices.get(evid));
			edges.add(edge);
		}
		reader.close();
	}

	private void addPatch() {
		vertices.get(3569).getNearbyEdgeId().add(edges.size());
		vertices.get(3573).getNearbyEdgeId().add(edges.size());
		edges.add(new Edge(vertices.get(3569), vertices.get(3573)));
	}

	/**
	 * Load pois from file.
	 * 
	 * @param source
	 *            source type
	 * @param file
	 *            file
	 * @throws IOException
	 *             ioexception
	 */
	private void loadPois(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String s = null;
		while ((s = reader.readLine()) != null) {
			s = reader.readLine();
			double latitude = Double.parseDouble(s);
			s = reader.readLine();
			double longitude = Double.parseDouble(s);
			reader.readLine();
			Location poi = new Location(longitude, latitude);
			int minId = -1;
			double mindis = 1e9;
			for (int i = 0; i < vertices.size(); i++) {
				double dis = poi.distance(vertices.get(i));
				if (dis < mindis) {
					mindis = dis;
					minId = i;
				}
			}
			poi.setVertexId(minId);
			pois.add(poi);
		}
		reader.close();
	}

	/**
	 * Init the graph with vertices,edges and pois.
	 * 
	 * @throws IOException
	 *             ioexception
	 */
	public void loadGraph() throws IOException {
		long start = System.currentTimeMillis();
		loadVertices(new File("dataset/Minneapolis_vertices.txt"));
		long end = System.currentTimeMillis();
		System.out.println("Loading " + vertices.size() + " vertices costs " + (end - start) + "ms.");
		start = System.currentTimeMillis();
		loadEdges(new File("dataset/Minneapolis_edges.txt"));
		end = System.currentTimeMillis();
		System.out.println("Loading " + edges.size() + " edges costs " + (end - start) + "ms.");
		start = System.currentTimeMillis();
		loadPois(new File("dataset/BarLocations_61.txt"));
		loadPois(new File("dataset/CafeLocations_65.txt"));
		loadPois(new File("dataset/FoodLocations_491.txt"));
		loadPois(new File("dataset/RestaurantLocations_320.txt"));
		loadPois(new File("dataset/StoreLocations_619.txt"));

		int constraint = 1000;
		while (pois.size() > constraint) {
			pois.remove(constraint);
		}
		end = System.currentTimeMillis();
		System.out.println("Loading " + pois.size() + " pois costs " + (end - start) + "ms.");
		addPatch();
	}

	int[] father;

	int find(int id) {
		int now = id;
		while (father[id] != id) {
			id = father[id];
		}
		father[now] = id;
		return id;
	}

	void union(int a1, int a2) {
		if (find(a1) != find(a2)) {
			father[find(a1)] = find(a2);
		}
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public ArrayList<Location> getPois() {
		return pois;
	}

	public static void main(String[] args) throws IOException {
		Graph graph = new Graph();
		graph.loadGraph();

	}
}
