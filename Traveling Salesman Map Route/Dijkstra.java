import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class Dijkstra {
	
	private Vertex[] vertexes;
    private int sourceVertex = -1;    
    private MapGraph graph;
    private CityTable table;
    
    Dijkstra(String filename) {
    	table = new CityTable();
        loadGraph(filename);
    }

    public MapGraph getGraph() {
    	return graph;
    }
    
    /** Compute all the shortest paths from the source vertex to all the other vertices
     * in the graph;
     * This function is called from GUIApp, when the user clicks on the city.
     */
    public void computePaths(CityNode vSource) {
		vertexes = new Vertex[graph.numNodes()];
		for (int i = 0; i < vertexes.length; i++) {
			vertexes[i] = new Vertex();
		}
    	sourceVertex = table.get(vSource.getCity());
   		vertexes[sourceVertex].currentCost = 0;
   		
		Edge temp = graph.getEdge(sourceVertex);
		PriorityQueue queue = new PriorityQueue(graph.numNodes());
		while(temp != null) {
			vertexes[temp.id].currentCost = temp.distance;
			vertexes[temp.id].lastNode = sourceVertex;
			queue.insert(temp.id, temp.distance);
			temp = temp.next;
		}
		
		while(!queue.isEmpty()) {
			int currentNode = queue.remove_min();
			temp = graph.getEdge(currentNode);
			while(temp != null) {
				double cost = vertexes[currentNode].currentCost + temp.distance;
				if(cost < vertexes[temp.id].currentCost) {
					vertexes[temp.id].currentCost = cost;
					vertexes[temp.id].lastNode = currentNode;
					queue.insert(temp.id, temp.distance);
				}	
				temp = temp.next;
			}
		}
	} 
		 
	/** Returns the shortest path between the source vertex and this vertex.
	 *  Returns the array of node id-s on the path
	 */ 
    public ArrayList<Integer> shortestPath(CityNode vTarget) {
    	int target = table.get(vTarget.getCity());
    	ArrayList<Integer> path = new ArrayList<Integer>();
    	path.add(target);
    	Vertex temp = vertexes[target];
    	while (temp.lastNode != -1) {
    		path.add(0, temp.lastNode);
    		temp = vertexes[temp.lastNode];
    	}
    	return path;
	 }
		
   /**
    * Loads graph info from the text file into MapGraph graph
    * @param filename
    */
   public void loadGraph(String filename) {
	   try {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;
		boolean readNodes = false;
		boolean readArcs = false;
		
		while ((line = reader.readLine()) != null) {
			if(line.equalsIgnoreCase("NODES")) {
				readNodes = true;
				graph = new MapGraph( Integer.parseInt(reader.readLine()) );
				continue;
			}
			if(line.equalsIgnoreCase("ARCS")) {
				readArcs = true;
				readNodes = false;
				continue;
			}
			if (readNodes) {
				String[] input = line.split("\\s");
				CityNode node = new CityNode(input[0], Double.parseDouble(input[1]), Double.parseDouble(input[2]));
				table.addCity(node);
				graph.addNode(node);
				continue;
			}
			if(readArcs) { 
				String[] input = line.split("\\s");
				int CityA = table.get(input[0]);
				int CityB = table.get(input[1]);
				
				Edge location_A = new Edge(CityA, Integer.parseInt(input[2]));
				Edge location_B = new Edge(CityB, Integer.parseInt(input[2]));
				graph.addEdge(CityB, location_A);
				graph.addEdge(CityA, location_B);
				continue;
			}
		}
		
		reader.close();
	   } catch (Exception e) {
		e.printStackTrace();
	}
	   
   }
		
   public static void main(String [] args){
			
		// Create an instance of the Dijkstra class
		// The parameter is the name of the file
		Dijkstra dijkstra = new Dijkstra(args[0]);
		GUIApp app = new GUIApp(dijkstra);	
	}
}
