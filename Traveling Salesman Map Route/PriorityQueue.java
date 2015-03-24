
public class PriorityQueue {

    private int[] pointers;
    private PriorityNode[] heap;
    private int size;
    private int max;
    
	public PriorityQueue(int numNodes) {
		max = numNodes;
		size = 0;
		pointers = new int[numNodes];
		for(int i = 0; i < numNodes; i++) {
			pointers[i] = -1;
		}
		heap = new PriorityNode[numNodes];
		heap[0] = new PriorityNode(-1, -1);
	}
	
    private int leftchild(int pos) {
	return 2*pos;
    }
    private int rightchild(int pos) {
	return 2*pos + 1;
    }

    private int parent(int pos) {
	return  pos / 2;
    }
    
    private boolean isleaf(int pos) {
	return ((pos > size/2) && (pos <= size));
    }

    private void swap(int pos1, int pos2) {
	PriorityNode tmp;

	int pointertmp = pointers[heap[pos1].nodeid];
	pointers[heap[pos1].nodeid] = pointers[heap[pos2].nodeid];
	pointers[heap[pos2].nodeid] = pointertmp;
	
	tmp = heap[pos1];
	heap[pos1] = heap[pos2];
	heap[pos2] = tmp;
	
    }
    
	public boolean isEmpty() {
		if (size == 0) {
			return true;
		}
		return false;
	}
	public void insert(int nodeID, int priority) {
		if (pointers[nodeID] != -1) {
			reduce_key(nodeID, priority);
		}
		else {
			size++;
			heap[size] = new PriorityNode(nodeID, priority);
			pointers[nodeID] = size;
			int current = size;
			
			
			while(heap[current].priority < heap[parent(current)].priority) {
				swap(current, parent(current));
				current = parent(current);
			}
		}
	}
	
	public int remove_min() {
		swap(1,size);
		size--;
		if (size != 0)
		    pushdown(1);
		pointers[heap[size+1].nodeid] = -1;
		return heap[size+1].nodeid;
	}
	
    private void pushdown(int position) {
	int smallestchild;
	while (!isleaf(position)) {
	    smallestchild = leftchild(position);
	    if ((smallestchild < size) && (heap[smallestchild].priority > heap[smallestchild+1].priority)) {
			smallestchild = smallestchild + 1;	
	    }
	    if (heap[position].priority <= heap[smallestchild].priority) {
	    	return;
	    }
	    swap(position,smallestchild);
	    position = smallestchild;
	}
    }
	
	public void reduce_key(int nodeID, int new_priority) {
		int pos = pointers[nodeID];
    	heap[pos].priority = new_priority;
    	while(parent(pos) != 0) {
    		if(heap[pos].priority < heap[parent(pos)].priority) {
    			swap(pos, parent(pos));
    			pos = parent(pos);
    			continue;
    		}
    		else {
    			break;
    		}
    	}
	}
	
	public void printQueue() {
		System.out.println("Heap: \n\t");
		for (int i = 0; i < size; i++) {
			System.out.print(" id " + heap[i].nodeid + " |pri " + heap[i].priority);
		}
		System.out.println("Pointers: \n\t");
		for(int i = 0; i < pointers.length; i++) {
			System.out.printf(" |%d| ", pointers[i]);
		}
	}
	
	private class PriorityNode {
		
		int nodeid;
		int priority;

		private PriorityNode(int nodeid, int priority) {
			this.nodeid = nodeid;
			this.priority = priority;
		}
	}

}
