/** GUIApp - a class that deals with the UI.
 *  Creates the window with the MapPanel and displays the map of the US.
 *  Takes the instance of class Dijkstra as a parameter, and has a reference to the graph. 
 *  The user can click on two cities and the program should run Dijkstra's 
 *  algorithm to compute the shortest path between the two cities. 
 *  The edges of the shortest path will be shown in blue.

 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class GUIApp extends JFrame {
	private MapPanel panel;
	
	GUIApp(Dijkstra dijkstra) {		
		// Creating a window
		JFrame frame = new JFrame("USA Map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		// Creating a panel with the image of the map, and with buttons
		panel = new MapPanel(dijkstra);
		// Adding the panel to the window
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);       
	}
	
	class MapPanel extends JPanel {
		 	private Dijkstra dijkstra;
		    private MapGraph graph;
		    
		 // the shortest path to display: array list of node id-s
		    ArrayList<Integer> path= null; 
		    
		    private BufferedImage image;
		    private boolean oneClicked = false;
		    private JButton buttonQuit;
		    private JButton buttonDijkstra;
		    
			// radius of the circle representing the node
			public final static int RAD = 3; 
		   
			private Color[] nodeColors;
		    private Color[] edgeColors;
		    private Point[] nodeLocations;
		    private String[] nodeLabels;
		    		    
		public MapPanel(Dijkstra dijkstra) {
			
			this.dijkstra = dijkstra;
			this.graph = dijkstra.getGraph(); 
			
			// initialize the array of node locations and array of node colors
			// used to draw nodes
			if (graph != null) {
				nodeLocations = graph.getNodeLocations();
				nodeColors = new Color[graph.numNodes()];
				nodeLabels = graph.getCities();
			}
			this.setLayout(new BorderLayout());
			this.setPreferredSize(new Dimension(590, 290));
			this.setBackground(Color.lightGray);
			this.addMouseListener(new MyListener());

			// buttons
			buttonDijkstra = new JButton("Dijkstra");
			buttonQuit = new JButton("Quit");
			buttonDijkstra.addActionListener(new ButtonListener());
			buttonQuit.addActionListener(new ButtonListener());
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
			buttonPanel.add(buttonDijkstra);
			buttonPanel.add(buttonQuit);
			this.add(buttonPanel, BorderLayout.EAST);
			
			try { // load the image of the map of the USA             
		          image = ImageIO.read(new File("USA.bmp"));	          
		       } catch (IOException ex) {
		            System.out.println("Could not load the image. ");
		       }
		       repaint();
		}
		
		/** The method actionPerformed of this class will be called when the user
		 * clicks on a button.
		 */
		class ButtonListener implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == buttonQuit) {
					System.exit(0);
				}
				else {
					oneClicked = false;
					// all nodes and edges should be gray
					if (graph != null) {
						path = null;
						resetColors();
						repaint();
					}
				}		
			}
	  } // innner class ButtonListener
		
	/**
	 * The method is responsible for drawing everything on the panel.
	 * Do not call it explicitly. Instead, call repaint() when something 
	 * changes and needs to be repainted.
	 */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null); 
        if (graph != null) {
        	drawGraph(g);
        }
    }
    
    /** Draws a little circle at the given location of the node; Uses the given color;
     * "city" parameter is used to draw a label next to the circle.
     */
    public void drawNode(Graphics g, Point location, Color col, String city) {
   		g.setColor(col);
   		g.fillOval(location.x - RAD, location.y - RAD, 2*RAD, 2*RAD);
   		g.setColor(Color.black);
   		g.setFont(new Font("SANS_SERIF", Font.PLAIN, 11));
 		g.drawString(city, location.x + 2, location.y - 2);
   	}
    
    public void drawGraph(Graphics g) {
    	if (nodeLocations == null || nodeColors == null || nodeLabels == null) {
    		System.out.println("Can't display nodes - the array of node locations has not been initialized");
    		return;
    	}
    	assert(nodeLocations.length == nodeColors.length);
    	for (int k=0; k<nodeLocations.length; k++) {
    			Point p = nodeLocations[k];
    			Color col = nodeColors[k];
    			String label = nodeLabels[k];
    			drawNode(g, p, col, label);
    	}
    	drawEdges(g);
    	drawPath(g, Color.blue);
    	
    }
    
    /** Draws the shortest path in a given color 
     * @param g
     * @param col
     */
    public void drawPath(Graphics g, Color col) {
    	if (path == null) {
    		System.out.println("No path to draw.");
    		return;
    	}
    	Integer vPrev = path.get(0);
    	nodeColors[vPrev] = col;
    	for (int k=1; k < path.size(); k++) {
    		Integer vCurr = path.get(k);
    		// change the color of this vertex 
    		nodeColors[vCurr] = col;
    		g.setColor(col);
    		// draw an edge from prev to curr
    		Point p1 = nodeLocations[vPrev];
    		Point p2 = nodeLocations[vCurr];
       		g.fillOval(p1.x - RAD, p1.y - RAD, 2*RAD, 2*RAD);
       		g.fillOval(p2.x - RAD, p2.y - RAD, 2*RAD, 2*RAD);
    		g.drawLine(p1.x, p1.y, p2.x, p2.y);
    		vPrev = vCurr;
    	}	
	}
	
    
    /** Changes the color of the node located at point p, to col
     * @param Point p
     * @param Color col
     */
    public void changeColor(Point p, Color col) {
    	for (int k=0; k<nodeLocations.length; k++) {
			Point curr = nodeLocations[k];
			if (p.x == curr.x && p.y == curr.y) {
				nodeColors[k] = col;
				return;
			}
    	}
    }
    
    /** Changes the color of all nodes to gray
     */
    public void resetColors() {
    	for (int k = 0; k < nodeColors.length; k++)
			nodeColors[k] = Color.gray;
    }
    
    /** Draws edges. Gets the edge information from the graph in a 2D array: 
     *  for each edge, the array stores an array of two Point objects 
     *  (corresponding to the beginning and end vertices of the edge). 
     *  Note that this representation is different from what is stored in 
     *  the adjacency list of the graph.
     * @param  Graphics g
     */
    public void drawEdges(Graphics g) {
    	Point[][] edges = graph.getEdges();
    	assert(edges.length == edgeColors.length);
		g.setColor(Color.gray);
    	for (int i =0; i < edges.length; i++) {
    		Point[] edge = edges[i];
    		assert(edge.length == 2); // should contain two vertices
    		Point p1 = edge[0];
    		Point p2 = edge[1];
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
    	}
    }
   
	class MyListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			
				Point center = e.getPoint();
				CityNode v = graph.getVertex(center);
				if (v == null) {
					System.out.println("You did not click on any node");
					return;
				}
				changeColor(v.getLocation(), Color.red);
				//v.setColor(Color.red);
				
				repaint();
			
				if (!oneClicked) {
					System.out.println("First node clicked: " + v.getCity());
					System.out.println("Run Dijkstra's computePaths method for this source vertex");
					
					dijkstra.computePaths(v);
					oneClicked = true;
				}
				else { //it's the second click
					System.out.println("Second  node clicked: " + v.getCity());
					System.out.println("Call Dijkstra's shortestPath() method to compute the shortest path between selected cities");
					path = dijkstra.shortestPath(v);
					
					repaint();
					oneClicked = false;
				
				} // if oneClicked is true
			
		}

		public void mouseEntered(MouseEvent e) { }			
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseDragged(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
		
	}
    
	}
	
}
