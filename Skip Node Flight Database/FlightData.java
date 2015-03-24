
public class FlightData {
	
	String flightNumber;
	Integer price;
	
	public FlightData(String flightNumber, Integer price){
		this.flightNumber = flightNumber;
		this.price = price;
	}
	
	public String toString(){
		return " FN [" + flightNumber + "] PRICE [" + price + "]";
	}
}
