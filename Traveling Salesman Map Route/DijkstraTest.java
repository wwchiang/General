import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DijkstraTest {

	/** Loads the graph from the input file. 
	 * Runs Dijktra's algorithm on every origin node and for each destination node,
	 * compares the returned path with the correct path in the resultsFile.
	 * @param inputFile
	 * @param resultsFile
	 * @return
	 */
	public static boolean testDijkstra(String inputFile, String resultsFile) {
		
		Dijkstra dijkstra = new Dijkstra(inputFile);
		MapGraph gr = dijkstra.getGraph();
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(resultsFile));
			String s;
			for (int i = 0 ; i < gr.numNodes(); i++) {
				CityNode originNode = gr.getNode(i);
				String originCity = originNode.getCity();
				s = br.readLine(); 
				if (s == null) {
					System.out.println("Error reading from the resultsFile");
					return false;
				}
				assert(s.equals(originCity));
				// Run Dijkstra's algorithm to compute the paths from originCity
				// to all other nodes
				dijkstra.computePaths(originNode);
				
				// Go along all the destinations
				for (int j = 0; j < gr.numNodes(); j++) {
					if (j != i) { 
						CityNode destNode = gr.getNode(j);
						String destCity = destNode.getCity();
						// get the path between originCity and destCity
						ArrayList<Integer> path = dijkstra.shortestPath(destNode);
						String pathString = br.readLine();
						if (pathString == null) {
							System.out.println("Error reading from the resultsFile");
							return false;
						}
						
						String[] expectedPath = pathString.split(" ");
						if (path.size() != expectedPath.length) {
							
							System.out.println("Your path's size = " + path.size());
							System.out.println("Expected the path of size: " + expectedPath.length);
							DijkstraTest.printInfo(originCity, destCity, path, expectedPath, gr);
							return false;
						}
						assert(expectedPath[expectedPath.length-1].equals(destCity)); 
						// comparing the cities on the returned path and the expected path
						for (int k = 0; k < path.size(); k++) {
							Integer num = path.get(k);
							CityNode nodeOnPath = gr.getNode(num);
							String cityString = nodeOnPath.getCity();
							if (!cityString.equals(expectedPath[k])) {
								DijkstraTest.printInfo(originCity, destCity, path, expectedPath, gr);
								return false;
							}
						} // for loop on nodes on the path
					} // if i!=j
				} // for destinations
			} // for origins
		}
		catch(IOException e) {
			e.getMessage();
		}
		return true;

	}
	
	/** Helper function, used for printing the error messages when the test function
	 * finds the mismatch between the expected result and the returned result.
	 * @param originCity
	 * @param destCity
	 * @param returnedPath
	 * @param expectedPath
	 * @param MapGraph gr
	 */
	public static void printInfo(String originCity, String destCity, ArrayList<Integer> returnedPath, String[] expectedPath, MapGraph gr) {

		System.out.println("MISMATCH FOUND: For cities " + originCity +  " -> " + destCity);
		System.out.print("The expected path is: ");
		for (int l = 0; l < expectedPath.length; l++) {
			System.out.print(expectedPath[l] + " ");
		}
		System.out.print("\nYour program returned the following path: ");
		for (int l = 0; l < returnedPath.size(); l++) {
			String city = gr.getNode(returnedPath.get(l)).getCity();
			System.out.print(city + " ");
		}
		
	}

	public static void main(String[] args) {
	
		boolean test = DijkstraTest.testDijkstra(args[0], "results");
		if (test)
				System.out.println("You passed all the tests.");
		
	}
}