import java.util.Iterator;


public interface MultiKeyListIterator extends Iterator<ListElem>
{
    // Returns true if there is a next element.  If hasNext returns false, then next should
    // throw a NoSuchElement exception
    public boolean hasNext();

    // Returns the next element in the list, and move the cursor position forward
    // Throws a NoSuchElementException if there is no next element
    public  ListElem next();
        
    // Returns true if there is a previous element.  If hasPrevious returns false, then previous should
    // throw a NoSuchElement exception
    public boolean hasPrevious();
    
    // Returns the previous element in the list, and move the cursor position backwards
    // Throws a NoSuchElementException if there is no previous element
    // Alternating calls to next and previous will return the same element
    public ListElem previous();
    
    // Removes the element last returned by next() or previous().  If remove is
    // called before next is called, or of remove is called twice in a row 
    // without an intervening call to next or previous, then an
    // IllegalStateExeception is thrown     
    public void remove();
}
