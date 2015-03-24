
public class FlightKey implements Comparable<FlightKey> {
	
	String origin, destination, date, time;

	public String getOrigin(){
		return this.origin.toString();
	}
	
	public String getDest(){
		return this.destination.toString();
	}
	
	public String getDate(){
		return this.date.toString();
	}
	
	public String getTime(){
		return this.time.toString();
	}
	
	
	
	public FlightKey(String origin, String destination, String date, String time){
		this.origin = origin;
		this.destination = destination;
		this.date = date;
		this.time = time;
	}
	
	public String toString(){
		return "[" + origin + "][" + destination + "][" + date + "][" + time + "]";
	}

	@Override
	public int compareTo(FlightKey o) {
		if (this.origin.compareToIgnoreCase(o.origin) < 0){
			return -1;
		}
		else if (this.origin.compareToIgnoreCase(o.origin) > 0){
			return 1;
		}
		else {
			if (this.destination.compareToIgnoreCase(o.destination) < 0){
				return -1;
			}
			else if (this.destination.compareToIgnoreCase(o.destination) > 0){
				return 1;
			}
			else {
				if (this.date.compareTo(o.date) < 0){
					return -1;
				}
				else if (this.date.compareTo(o.date) > 0){
					return 1;
				}
				else {
					return this.time.compareTo(o.time);
				}
			}
		}
	}
}
