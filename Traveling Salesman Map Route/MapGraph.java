/** A class representing a graph. Stores an array of nodes and adjacency list.
 * 
 */
import java.awt.Point;

public class MapGraph {
	
	private CityNode[] nodes;
	private int idCounter;
	// for each vertex store a linked list of edges; 
	private Edge[] adjacencyList; 
	
	private int numNodes = 0;
	private int numEdges = 0;

	public final int EPS_DIST = 5;
	
	
	MapGraph(int numNodes) {
		idCounter = 0;
		this.numNodes = numNodes;
		nodes = new CityNode[numNodes];
		adjacencyList = new Edge[numNodes];
	}
	
	/**
	 * Returns a node with index i
	 */
	CityNode getNode(int i) {
		return nodes[i];
	}
	
	/** Returns the head of the linked list of edges for a vertex with id = i
	 */
	Edge getEdge(int i) {
		return adjacencyList[i];
	}
	/** Adds a node to the graph
	 * @param node
	 */
	public void addNode(CityNode node) {
		nodes[idCounter] = node;
		idCounter++;
	}
	
	public void printMap() {
		for(int i = 0; i < numNodes; i++) {
			System.out.print(nodes[i].getCity() + "[" + i + "]");
			Edge head = adjacencyList[i];
			while(head != null) {
				System.out.print(" -> " + head.id + "|" + head.distance);
				head = head.next;
			}
			System.out.println();
		}
	}
	
	public int numNodes() {
		return numNodes;
	}
	
	/** Adds the edge to the linked list for this vertexId
	 * @param vertexId
	 * @param edge
	 */
	public void addEdge(int nodeId, Edge edge) {
		
		if (adjacencyList[nodeId] == null) {
			adjacencyList[nodeId] = edge;
		}
		else {
			Edge head = adjacencyList[nodeId];
			adjacencyList[nodeId] = edge;
			edge.next = head;
		}
		numEdges++;
	}
	
	/** Given the location of the click, return the node of the graph 
	 * at this location. 
	 */
	public CityNode getVertex(Point loc) {
		for (CityNode v: nodes) {
			Point p = v.getLocation();
			if ((Math.abs(loc.x - p.x) < EPS_DIST) && (Math.abs(loc.y - p.y) < EPS_DIST))
				return v;
		}
		return null;
	}
	

	
	/**
	 *  Returns the array of all edges for drawing: each element in the array 
	 *  corresponds  to one edge and is the array of two Point objects 
	 *  (corresponding to the locations of the two nodes connected by this edge).
	 */
	public Point[][] getEdges() {
		Point[][] edges = new Point[numEdges][2];
		int counter = 0;
		for(int i = 0; i < numNodes; i++) {
			//Point[] edge = new Point[2];
			Point one = new Point(nodes[i].getLocation());
			Edge head = adjacencyList[i];
			while(head != null) {
				Point two = new Point(nodes[head.id].getLocation());
				edges[counter][0] = one;
				edges[counter][1] = two;
				head = head.next;
				counter++;
			}
		}
		return edges;
		
	}
	
	/** Returns the array of nodes as points. 
	 * Used by MapGraph to draw little circles at the location of the nodes
	 */
	public Point[] getNodeLocations() {
		Point[] locations = new Point[numNodes];
		for(int i = 0; i < numNodes; i++) {
			locations[i] = new Point(nodes[i].getLocation());
		}
		return locations;
	}
	
	/**
	 * Returns the array of cities corresponding to the vertices of this graph 
	 * in the array
	 * @return
	 */
	public String[] getCities() {
		String[] labels = new String[numNodes];
		for(int i = 0; i < numNodes; i++) {
			labels[i] = nodes[i].getCity();
		}
		return labels;
	}		
	
} // class MapGraph