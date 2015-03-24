
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FlightListTester {
	// skip list
	private FlightList list;
	
	// test cases
	private ArrayList<FlightKey> flightsToTest;
	private int[] timeDifference = new int[5];
	private ArrayList<FlightNode[]> expectedResults;

	FlightListTester(String filename) {
		try {
			list = new FlightList(filename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//list.readFlights(filename);	
		flightsToTest = new ArrayList<FlightKey>();
		expectedResults = new ArrayList<FlightNode[]>();
	}
	
	/** Tests the find() method of class FlightList
	 * 
	 */
    public boolean testFind(String filename) {
	
	   boolean passed = true;    
	   BufferedReader br = null;
	   try  {
			br = new BufferedReader(new FileReader(filename));
			String s;
			while ((s = br.readLine()) != null) {
				String[] arr = s.split(" ");
				assert(arr.length == 6);
				FlightKey key = new FlightKey(arr[0], arr[1], arr[2], arr[3]);
				//System.out.println(key.toString());
	            // make sure that a flight with this key is in the skip list:
	            //System.out.println(list.find(key)); 
				if (!list.find(key)) {
	                   passed = false;
	                   System.out.println("Failed to find the following flight: " + s);                        
	             }      
	        }
	   } 
       catch (IOException e)
       {
           System.out.println("IOException occured while reading from the file: " + e);
           passed = false;
       }
       if (passed)
    	   System.out.println("testFind = success.");
       return passed;
    }
			


    /** Tests whether findFligths method returns the correct result for a few selected flights
     * 
     */
    public boolean testFindFlights() {
    	
    	for (int i =0 ; i < flightsToTest.size(); i++) {
    		FlightKey key = flightsToTest.get(i);
    		ArrayList<FlightNode> results = list.findFlights(key, timeDifference[i]);
    	
    		FlightNode[] expectedResult = expectedResults.get(i);
    		if (expectedResult.length != results.size()) {
    			System.out.println("In testFindFlights: the number of flights returned for " + key + " is not what was expected");
    			System.out.println("Expected size versus actual size: " + expectedResult.length + " vs " + results.size());
    			System.out.println("The expected output of findFlights method on this flight  with " + " time difference of " + timeDifference[i] + " hours is: ");
    			for (int l=0; l < expectedResult.length; l++)
    				System.out.println(expectedResult[l].getKey() + " ");
    			return false; // failed the test
    		}
    		for (int k = 0; k < expectedResult.length; k++) {
    			FlightNode node1 =  expectedResult[k];
    			FlightNode node2 = results.get(k);
    			FlightKey ndkey1 = node1.getKey();
    			FlightKey ndkey2 = node2.getKey();
    			boolean bol = (((ndkey1.getOrigin()).equals(ndkey2.getOrigin())) && ((ndkey1.getDest()).equals(ndkey2.getDest()))
    				&& ((ndkey1.getDate()).equals(ndkey2.getDate())) && ((ndkey1.getTime()).equals(ndkey2.getTime())));
    			{
    				if (!bol) {
    				System.out.println("At least one of the flights returned by findFlights is incorrect: " + ndkey1);
    				return false;
    				}
    			}
    		}		
    	}
    	System.out.println("testFindFlights = success.");
    	return true;
    }
    
    
    public void prepareTestData() {
    	flightsToTest.add(new FlightKey("FRA", "JFK", "01/03/2014", "05:50"));
    	flightsToTest.add(new FlightKey("FRA", "JFK", "01/03/2014", "16:00"));
    	flightsToTest.add(new FlightKey("FRA", "JFK", "01/03/2014", "12:00"));
    	flightsToTest.add(new FlightKey("FRA", "SFO", "01/03/2014", "07:10"));
    	flightsToTest.add(new FlightKey("SFO", "ORD", "01/03/2014", "05:00"));
    	timeDifference[0] = 2;
    	timeDifference[1] = 7;
    	timeDifference[2] = 5;
    	timeDifference[3] = 2;
    	timeDifference[4] = 18;
    
    	FlightKey resKey1 = new FlightKey("FRA", "JFK",  "01/03/2014", "05:50");
    	FlightData data1 = new FlightData("LH113", 400);
    	FlightKey resKey2 = new FlightKey("FRA", "JFK",  "01/03/2014", "07:00");
    	FlightData data2 = new FlightData("LH123", 400);
    	FlightNode[] expectedList1 = {new FlightNode(resKey1, data1), new FlightNode(resKey2, data2)};
    
    	resKey1 = new FlightKey("FRA", "JFK",  "01/03/2014", "16:00");
    	data1 = new FlightData("LH143", 500);
    	resKey2 = new FlightKey("FRA", "JFK",  "01/03/2014", "17:00");
    	data2 = new FlightData("AA123", 400);
    	FlightKey resKey3 = new FlightKey("FRA", "JFK",  "01/03/2014", "22:00");
    	FlightData data3 = new FlightData("DL324", 400);
    	FlightNode[] expectedList2 = {new FlightNode(resKey1, data1), new FlightNode(resKey2, data2), new FlightNode(resKey3, data3)};
    	
    	resKey1 = new FlightKey("FRA", "JFK",  "01/03/2014", "07:00");
    	data1 = new FlightData("LH123", 400);
    	resKey2 = new FlightKey("FRA", "JFK",  "01/03/2014", "16:00");
    	data2 = new FlightData("LH143", 500);
    	resKey3 = new FlightKey("FRA", "JFK",  "01/03/2014", "17:00");
    	data3 = new FlightData("AA123", 400);
    	FlightNode[] expectedList3 = {new FlightNode(resKey1, data1), new FlightNode(resKey2, data2), new FlightNode(resKey3, data3)};
    	
    	resKey1 = new FlightKey("FRA", "SFO",  "01/03/2014", "07:10");
    	data1 = new FlightData("DL113", 400);
    	resKey2 = new FlightKey("FRA", "SFO",  "01/03/2014", "09:10");
    	data2 = new FlightData("DL891", 500);
    	FlightNode[] expectedList4 = {new FlightNode(resKey1, data1), new FlightNode(resKey2, data2)};
    	
    	resKey1 = new FlightKey("SFO", "ORD",  "01/03/2014", "05:00");
    	data1 = new FlightData("DL394", 500);
    	resKey2 = new FlightKey("SFO", "ORD",  "01/03/2014", "23:10");
    	data2 = new FlightData("AA234", 400);
    	FlightNode[] expectedList5 = {new FlightNode(resKey1, data1), new FlightNode(resKey2, data2)};
    	
    	expectedResults.add(expectedList1);
    	expectedResults.add(expectedList2);
    	expectedResults.add(expectedList3);
    	expectedResults.add(expectedList4);
    	expectedResults.add(expectedList5); 	
    }
    
  public static void main(String args[]) {
	boolean passedTests1, passedTests2;
	FlightListTester tester = new FlightListTester("flights.txt");
	passedTests1 = tester.testFind("flights.txt");
	tester.prepareTestData();
	passedTests2 = tester.testFindFlights();

	if (! (passedTests1&&passedTests2) ) {
	      System.out.println("At least one test failed");
	}
	else {
	        System.out.println("Automated tests passed.");
	        System.out.println("Be sure to check your output by hand!");    
	      }
	        
  }   	    

}
