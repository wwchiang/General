import java.util.NoSuchElementException;


public class MyMultiKeyListIterator implements MultiKeyListIterator {
	
	MyListElem next, previous, remove;
	int keyIndex;
	int numKeys;
	public MyMultiKeyListIterator(MyListElem dummy, int keyIndex) {
		remove = null;
		previous = null;
		next = dummy;
		this.keyIndex = keyIndex;
		numKeys = dummy.numKeys;
	}

	@Override
	public boolean hasNext() {
		if (next.getNext(keyIndex) != null) {
			return true;
		}
		return false;
	}

	@Override
	public ListElem next() {
		if (hasNext() == false){
			throw new NoSuchElementException();
		}
		previous = next;
		next = next.getNext(keyIndex);
		remove = next;
		return next;
	}

	@Override
	public boolean hasPrevious() {
		if (next.getPrev(keyIndex) != null){
			return true;
		}
		return false;
	}

	@Override
	public ListElem previous() {
		if (hasPrevious() == false){
			throw new NoSuchElementException();
		}
		previous = next;
		next = next.getPrev(keyIndex);
		remove = previous;
		return previous;
	}

	@Override
	public void remove() {
		if (remove == null){
			throw new IllegalStateException();
		}
		
		if (next == remove){
			next = previous;
		}
		else if (previous == remove){
			previous = next;
		}
		
		for (int i = 0; i < numKeys; i++){
			remove.prev[i].next[i] = remove.next[i];
			if (remove.next[i] != null){
				remove.next[i].prev[i] = remove.prev[i];
			}
			remove.prev[i] = null;
			remove.next[i] = null;
		}
		remove = null;
	}

}
