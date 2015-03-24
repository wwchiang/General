
/**
 * This class is essentially an open hash table used to find the 
 * ID of each city.
 * @author William
 *
 */
public class CityTable {
	
	private final int tableSize = 17;
	private CityTableEntry[] entries;
	private int idCounter;
	
	public CityTable() {
		idCounter = 0;
		entries = new CityTableEntry[tableSize];
		for (int i = 0; i < tableSize; i++) {
			entries[i] = new CityTableEntry();
		}
	}
	
	public void addCity(CityNode entry) {
		int index = entry.getCity().charAt(0) % tableSize;
		if (entries[index].next == null) {
			entries[index].setNext(new CityTableEntry(idCounter, entry.getCity(), entry));
			idCounter++;
		}
		else {
			CityTableEntry head = entries[index].next;
			CityTableEntry current = new CityTableEntry(idCounter, entry.getCity(), entry);
			entries[index].setNext(current);
			current.setNext(head);
			idCounter++;
		}
	}
	
	public int get(String cityName) {
		int index = cityName.charAt(0) % tableSize;
		CityTableEntry head = entries[index].next;
		while(head != null) {
			if (head.city.equalsIgnoreCase(cityName)) {
				return head.id;
			}
			head = head.next;
		}
		return -1;
	}
	
	public void printTable() {
		for(int i = 0; i < tableSize; i++) {
			CityTableEntry head = entries[i].next;
			while (head != null) {
				System.out.print("[" + head.city + "-" + head.id + "] -> ");
				head = head.next;
			}
			System.out.println();
		}
	}
	private class CityTableEntry {
		
		int id;
		String city;
		private CityTableEntry next;
		CityNode entryNode;
		
		public CityTableEntry() {
			next = null;
		}
		public CityTableEntry(int id, String city, CityNode entryNode) {
			this.id = id;
			this.city = city;
			this.entryNode = entryNode;
			next = null;
		}
		
		public void setNext(CityTableEntry next) {
			this.next = next;
		}
		
		public CityTableEntry getNext() {
			return this.next;
		}
		
	}
}
