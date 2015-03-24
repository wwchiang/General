
/**
 * Used to calculate Dijkstra's shortest path,
 * storing the current cost and the last node which
 * led to this vertex.
 * @author William
 *
 */
public class Vertex {

	double currentCost;
	int lastNode = -1;

	public Vertex() {
		currentCost = Double.POSITIVE_INFINITY;
	}
}
