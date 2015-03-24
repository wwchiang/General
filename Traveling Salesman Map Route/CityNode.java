/** A class that represents a node of the graph. Contains the name of the city and
 * the location on the "map".
 */
import java.awt.Point;

public class CityNode  {
	private final String city; 
    private Point location;
    
    /** Call this method when you are reading the graph file to create a node
     * @param String cityName
     * @param double x
     * @param double y
     */
	public CityNode(String cityName, double x, double y) {
		
		this.city = cityName;
		int xint = (int) (507*x / 7.0);
		int yint = (int) (289 - 289*y/4.0);
		this.location = new Point(xint, yint);
	}
	
     public Point getLocation() {
    	 return location;
     }
     public String getCity() {
    	 return city;
     }
    
	
}
