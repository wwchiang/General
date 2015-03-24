
public class MyListElem implements ListElem {
	int numKeys;
	Comparable[] keys;
	MyListElem[] next;
	MyListElem[] prev;
	Object data;
	
	public MyListElem(Comparable[] inputKeys, Object data, int numKeys){
		this.numKeys = numKeys;
		keys = new Comparable[numKeys];
		next = new MyListElem[numKeys];
		prev = new MyListElem[numKeys];
		this.data = data;
		for (int i = 0; i < numKeys; i++){
			keys[i] = inputKeys[i]; 
			prev[i] = null;
			next[i] = null;
		}
		
	}
	
	public MyListElem getNext(int index){
		return next[index];
	}
	
	public MyListElem getPrev(int index){
		return prev[index];
	}
	
	public void setNext(int index, MyListElem a){
		next[index] = a;
	}
	
	public void setPrevious(int index, MyListElem a){
		prev[index] = a;
	}
	@Override
	public int numKeys() {
		return numKeys;
	}

	@Override
	public Comparable key(int index) {
		return keys[index];
	}

	@Override
	public Object data() {
		return data;
	}

}
