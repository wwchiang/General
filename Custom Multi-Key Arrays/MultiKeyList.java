public interface MultiKeyList
{
    // Adds an object to the list.  If the length of keys is not the same
    // as the number of keys in the list, throw an IllegalArgumentException
    public  void add(Comparable keys[], Object data);

    // Get an iterator to iterate over a particular key.  If keyIndex is not
    // within the range of allowed keys, throw an IllegalArgumentException    
    public  MultiKeyListIterator iterator(int keyIndex);
    
    // Get an interface to the element at a particular index of the list.  If keyIndex is not
    // within the range of allowed keys, throw an IllegalArgumentException
    public  ListElem get(int index, int keyIndex); 
    
    // Remove the ith element in the list using the given key index.
    public  void removeIndex(int index, int keyIndex);  
    
    // Remove the element matching *all* keys       
    public  void remove(Comparable keys[]);
    
    // Remove the element matching the key at the given index 
    public void remove(Comparable key, int keyIndex);
}
