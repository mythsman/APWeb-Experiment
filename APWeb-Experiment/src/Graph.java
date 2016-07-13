import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * The Graph.
 * 
 * @author myths
 *
 */
public class Graph {
	private ArrayList<Vertex> vertices;
	private ArrayList<Edge> edges;
	private ArrayList<Poi> pois;

	public Graph() {
		vertices = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		pois = new ArrayList<Poi>();
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
		while ((line = reader.readLine()) != null) {
			String[] items = line.split("\t");
			int vid = Integer.parseInt(items[0]);
			double longitude = Double.parseDouble(items[1]) / 1000000 - 180;
			double latitude = 90 - Double.parseDouble(items[2]) / 1000000;
			int degree = Integer.parseInt(items[3]);
			Vertex vertex = new Vertex(longitude, latitude, vid, degree);
			for (int i = 0; i < degree; i++) {
				line = reader.readLine();
				String[] subItems = line.split("\t");
				vertex.addEdgeId(Integer.parseInt(subItems[0]));
			}
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
			int eid = Integer.parseInt(items[0]);
			int svid = Integer.parseInt(items[6]);
			int evid = Integer.parseInt(items[7]);
			Edge edge = new Edge(vertices.get(svid), vertices.get(evid), eid);
			edges.add(edge);
		}
		reader.close();
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
		int cnt = 0;
		while ((s = reader.readLine()) != null) {
			cnt++;
			s = reader.readLine();
			double latitude = Double.parseDouble(s);
			s = reader.readLine();
			double longitude = Double.parseDouble(s);
			reader.readLine();
			Poi poi = new Poi(longitude, latitude, cnt);
			int minId = -1;
			double mindis = 1e9;
			for (int i = 0; i < edges.size(); i++) {
				double dis = poi.distanceToEdge(edges.get(i).getSvertex(), edges.get(i).getEvertex());
				if (dis < mindis) {
					mindis = dis;
					minId = i;
				}
			}
			pois.add(poi.projection(edges.get(minId).getSvertex(), edges.get(minId).getEvertex()));
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
		end = System.currentTimeMillis();
		System.out.println("Loading " + pois.size() + " pois costs " + (end - start) + "ms.");
	}

	public void test() {
		/*
		 * for (Vertex ver : vertices) { double mindis = 1e9; for (Edge edge :
		 * edges) { double dis = ver.distanceToEdge(edge.getSvertex(),
		 * edge.getEvertex()); if (dis < mindis) { mindis = dis; } } if (mindis
		 * > 1) System.out.println(mindis); }
		 * System.out.println("Vertex tests done");
		 */
		for (Poi poi : pois) {
			double mindis = 1e9;
			for (Edge edge : edges) {
				double dis = poi.distanceToEdge(edge.getSvertex(), edge.getEvertex());
				if (dis < mindis) {
					mindis = dis;
				}
			}
			if (mindis > 1)
				System.out.println(mindis);
		}
		System.out.println("Pois tests done");
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public ArrayList<Poi> getPois() {
		return pois;
	}

	public static void main(String[] args) throws IOException {
		Graph graph = new Graph();
		graph.loadGraph();
		graph.test();
	}
}
