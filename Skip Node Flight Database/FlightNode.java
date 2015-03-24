import java.util.ArrayList;


public class FlightNode {
	
	FlightKey key;
	FlightData data;
	FlightNode next, previous, down, up = null;
	
	public FlightNode(FlightKey key, FlightData data){
		this.key = key;
		this.data = data;
	}
	
	public FlightNode(FlightNode presentNode){
		this.key = presentNode.key;
		this.data = presentNode.data;
	}
	public FlightNode(String origin, String destination, String date, String time){
		this.key = new FlightKey(origin, destination, date, time);
	}

	public FlightKey getKey() {
		return this.key;
	}
	
}
