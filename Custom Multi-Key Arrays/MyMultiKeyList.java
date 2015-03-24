
/**
 * Programming Assignment 1 - Sorted Lists
 * 
 * @author William Chiang
 *
 */
public class MyMultiKeyList implements MultiKeyList {
	int numKeys, numOfElements;
	MyListElem dummy;
	public MyMultiKeyList(int numKeys) {
		this.numKeys = numKeys;
		numOfElements = 0;
		Comparable[] dummyList = new Comparable[numKeys];
		for (int i = 0; i < numKeys; i++){
			dummyList[i] = null;
		}
		dummy = new MyListElem(dummyList, null, numKeys);
	}

	@Override
	public void add(Comparable[] keys, Object data) {
		if (keys.length != numKeys){
			throw new IllegalArgumentException();
		}
		MyListElem newElem = new MyListElem(keys, data, numKeys);
		for (int i = 0; i < numKeys; i++){
			if (dummy.next[i] == null){
				dummy.next[i] = newElem;
				newElem.prev[i] = dummy;
			}
			else {
				MultiKeyListIterator addIterator = iterator(i);
				MyListElem currentElem;
				while (addIterator.hasNext()){
					currentElem = (MyListElem) addIterator.next();
					if (newElem.key(i).compareTo(currentElem.key(i)) < 0){
						newElem.next[i] = currentElem;
						newElem.prev[i] = currentElem.prev[i];
						currentElem.prev[i].next[i] = newElem;
						currentElem.prev[i] = newElem;
						break;
						
					}
					else {
						if (currentElem.next[i] == null){
							currentElem.next[i] = newElem;
							newElem.prev[i] = currentElem;	
							break;
						}
					}
				}	
			}
		}
		numOfElements++;
		
	}

	@Override
	public MultiKeyListIterator iterator(int keyIndex) {
		if (keyIndex < 0 || keyIndex > numKeys - 1){
			throw new IllegalArgumentException();
		}
		MultiKeyListIterator iterator = new MyMultiKeyListIterator(dummy, keyIndex);
		
		return iterator;
	}

	@Override
	public ListElem get(int index, int keyIndex) {
		if (index > numOfElements - 1 || keyIndex > numKeys - 1){
			throw new IllegalArgumentException();
		}
		MultiKeyListIterator getIterator = iterator(keyIndex);
		MyListElem currentElem = null;
		for (int i = 0; i < index+1; i++){
			currentElem = (MyListElem) getIterator.next();
		}
		return currentElem;
	}

	@Override
	public void removeIndex(int index, int keyIndex) {
		MultiKeyListIterator removeIterator = iterator(keyIndex);
		for (int i = 0; i < index+1; i++){
			removeIterator.next();
		}
		removeIterator.remove();
	}

	@Override
	public void remove(Comparable[] keys) {
		MultiKeyListIterator removeIterator = iterator(0);
		ListElem removeElem = null;
		boolean doRemove = true;
		while (removeIterator.hasNext()){
			removeElem = removeIterator.next();
			for (int i = 0; i < numKeys; i++){
				if (removeElem.key(i) != keys[i]){
					doRemove = false;
				}
			}
			if (doRemove){
				removeIterator.remove();
				break;
			}
		}
	}

	@Override
	public void remove(Comparable key, int keyIndex) {
		
		MultiKeyListIterator removeIterator = iterator(keyIndex);
		ListElem removeElem = null;
		boolean doRemove = false;
		while (removeIterator.hasNext()){
			removeElem = removeIterator.next();
			if (removeElem.key(keyIndex).compareTo(key) == 0){
				doRemove = true;
			}
			if (doRemove){
				removeIterator.remove();
				break;
			}
		}		
	}

}
