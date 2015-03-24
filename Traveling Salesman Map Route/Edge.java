import java.awt.Color;

/** Edge class represents a single node in the linked list of edges for a vertex.
 * 
 */

public class Edge {
	
	int id;
	int distance;
	Edge next;
	Color color;
	
	public Edge(int id, int distance) {
		this.id = id;
		this.distance = distance;
		next = null;
	}
	
	
 }