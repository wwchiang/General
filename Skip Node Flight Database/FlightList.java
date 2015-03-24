import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;


public class FlightList {
	
	FlightNode LLC, LRC, ULC, URC;
	public FlightList(){
		LLC = new FlightNode("AAA", "AAA", "00/00/00", "00:00");
		ULC = new FlightNode("AAA", "AAA", "00/00/00", "00:00");
		LRC = new FlightNode("ZZZ", "ZZZ", "99/99/99", "99:99");
		URC = new FlightNode("ZZZ", "ZZZ", "99/99/99", "99:99");
		LLC.next = LRC;
		LRC.previous = LLC;
		
		ULC.next = URC;
		URC.previous = ULC;
		
		ULC.down = LLC;
		URC.down = LRC;
	}
	
	public FlightList(String fileName) throws Exception {
		LLC = new FlightNode("AAA", "AAA", "00/00/00", "00:00");
		ULC = new FlightNode("AAA", "AAA", "00/00/00", "00:00");
		LRC = new FlightNode("ZZZ", "ZZZ", "99/99/99", "99:99");
		URC = new FlightNode("ZZZ", "ZZZ", "99/99/99", "99:99");
		LLC.next = LRC;
		LRC.previous = LLC;
		
		ULC.next = URC;
		URC.previous = ULC;
		
		ULC.down = LLC;
		URC.down = LRC;
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line;
		FlightKey newKey;
		FlightData newData;
		while ((line = reader.readLine()) != null){
			String[] input = line.split("\\s");
			newKey = new FlightKey(input[0], input[1], input[2], input[3]);
			newData = new FlightData(input[4], Integer.parseInt(input[5]));
			insert(newKey, newData);
		}
	}
	
	public boolean insert(FlightKey key, FlightData data){
		
		if(key.compareTo(LLC.key) < 0 || key.compareTo(LRC.key) > 0){
			return false;
		}
		
		FlightNode input = new FlightNode(key, data);
		return insert(input, LLC, LRC);
	}
	
	private boolean insert(FlightNode node, FlightNode left, FlightNode right){
		FlightNode head = left;
		while (head != null){
			if (node.key.compareTo(head.key) < 0){
				node.previous = head.previous;
				node.next = head;
				head.previous.next = node;
				head.previous = node;
				break;
			}
			else if (node.key.compareTo(head.key) == 0){
				if (node.data.flightNumber.equalsIgnoreCase(head.data.flightNumber) && node.data.price.equals(head.data.price)){
						return false;
				}
				else if (node.key.compareTo(head.next.key) < 0){
					node.previous = head.previous;
					node.next = head;
					head.previous.next = node;
					head.previous = node;
					break;
				}
				else {
					head = head.next;
				}
			}
			else {
				head = head.next;
			}
		}
		if (createNewRow()){
			FlightNode aboveNode = new FlightNode(node);
			node.up = aboveNode;
			aboveNode.down = node;
			if (left.up != null && right.up != null){
				insert(aboveNode, left.up, right.up);
			}
			else {
				FlightNode aboveLeft = new FlightNode("AAA", "AAA", "00/00/00", "00:00");
				FlightNode aboveRight = new FlightNode("ZZZ", "ZZZ", "99/99/99", "99:99");
				left.up = aboveLeft;
				right.up = aboveRight;
				aboveLeft.down = left;
				aboveRight.down = right;
				aboveLeft.next = aboveRight;
				aboveRight.previous = aboveLeft;
				ULC.down = aboveLeft;
				URC.down = aboveRight;
				insert(aboveNode, aboveLeft, aboveRight);
			}
		}
		return true;
	}
	private boolean createNewRow(){
		double flip = Math.random();
		if (flip < 0.5){
			return true;
		}
		else {
			return false;
		}
	}
	public boolean find(FlightKey key){
		return find(ULC, key);
	}
	
	private boolean find(FlightNode node, FlightKey key){
		FlightNode head = node;
		while (head != null){
			if (head.key.compareTo(key) == 0){
				return true;
			}
			else if (head.next == null){
				return false;
			}
			else if (key.compareTo(head.key) > 0 && key.compareTo(head.next.key) < 0){
				if (head.down != null){
					return find(head.down, key);
				}
				else {
					head = head.next;
				}
			}
			head = head.next;
		}
		return false;
	}
	public ArrayList<FlightNode> successors(FlightKey key){
		FlightNode head = LLC;
		ArrayList<FlightNode> validFlights = new ArrayList<FlightNode>();
		while (head != null){
			if (head.key.origin.equalsIgnoreCase(key.origin)){
				if(head.key.destination.equalsIgnoreCase(key.destination)){
					if(head.key.date.equalsIgnoreCase(key.date)){
						if(head.key.compareTo(key) >= 0){
							validFlights.add(head);	
						}
					}
				}
			}
			head = head.next;
		}
		return validFlights;
	}
	
	public ArrayList<FlightNode> predecessors(FlightKey key){
		FlightNode head = LLC;
		ArrayList<FlightNode> validFlights = new ArrayList<FlightNode>();
		while (head != null){
			if (head.key.origin.equalsIgnoreCase(key.origin)){
				if(head.key.destination.equalsIgnoreCase(key.destination)){
					if(head.key.date.equalsIgnoreCase(key.date)){
						if(head.key.compareTo(key) <= 0){
							validFlights.add(head);	
						}
					}
				}
			}
			head = head.next;
		}
		return validFlights;
	}
	
	private String getHour(FlightKey key, int hour, int timeDiff){
		String convertedHour = "";
		Integer intHour = hour % 24 + timeDiff;
		if (intHour < 0){
			intHour = 0;
		}
		if (intHour < 10){
			if (intHour == 0){
				convertedHour = "00" + convertedHour;
			}
			else {
				convertedHour = "0" + intHour.toString() + convertedHour;
			}
		}
		else {
			convertedHour = intHour.toString() + convertedHour;
		}
		return convertedHour;
	}
	public ArrayList<FlightNode> findFlights(FlightKey key, int timeDifference){
		Integer hour = Integer.parseInt(key.time.substring(0, 2));
		String low = getHour(key, hour, timeDifference*-1);
		String high = getHour(key, hour, timeDifference);
		FlightNode head = LLC;
		ArrayList<FlightNode> validFlights = new ArrayList<FlightNode>();
		while (head != null){
			if (head.key.origin.equalsIgnoreCase(key.origin)){
				if(head.key.destination.equalsIgnoreCase(key.destination)){
					if(head.key.date.equalsIgnoreCase(key.date)){
						String keyhour = head.key.time.substring(0, 2);
						if(keyhour.compareToIgnoreCase(low) >= 0 && keyhour.compareToIgnoreCase(high) <= 0){
							validFlights.add(head);	
						}
					}
				}
			}
			head = head.next;
		}
		return validFlights;
	}

	public void print(){
		FlightNode up = LLC;
		print(up);
	}
	
	private void print(FlightNode node){
		if (node.up == null){
			return;
		}
		else {
			print(node.up);
		}
		FlightNode head = node;
		while (head != null){
			if (head.key.origin.equalsIgnoreCase("AAA") || head.key.origin.equalsIgnoreCase("ZZZ")){
				head = head.next;
				continue;
			}
			System.out.print(head.key.toString() + "|" + head.data.flightNumber + "-" + head.data.price + "|\t");
			head = head.next;
		}
		System.out.println();
		return;
	}
	
	// Extra Credit Methods
	// Remove
	public void remove(FlightKey key){
		remove(ULC.down, key);
	}
	
	private void remove(FlightNode node, FlightKey key){
		FlightNode head = node;
		while (head != null){
			if (head.key.compareTo(key) == 0){
				head.previous.next = head.next;
				head.next.previous = head.previous;
				if (head.down != null){
					remove(head.down, key);
				}
				head = null;
			}
			else if (head.next == null){
				return;
			}
			else if (key.compareTo(head.key) > 0 && key.compareTo(head.next.key) < 0){
				if (head.down != null){
					remove(head.down, key);
				}
				else {
					head = head.next;
				}
			}
			head = head.next;
		}
	}
	
	// Suggest
	public FlightKey suggestFlights(FlightKey key){
		FlightNode head = LLC;
		while (head != null){
			if(head.key.compareTo(key) == 0){
				return head.key;
			}
			head = head.next;
		}
		head = LLC;
		while (head != null){
			if(head.key.compareTo(key) < 0 && head.next.key.compareTo(key) > 0){
				if(!head.key.origin.equalsIgnoreCase("AAA")){
					return head.key;
				}
				else if (!head.next.key.origin.equalsIgnoreCase("ZZZ")){
					return head.next.key;
				}
			}
		}
		return head.key;
	}
}
