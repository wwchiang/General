import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;


class TestData
{
    public TestData(int numKeys)
    {
        keys = new Comparable[numKeys];
    }
    Comparable keys[];
    Object data;        
}



public class TestMultiKeyList
{
    
    
    private static final int DATA_SIZE = 100;
    private static final int NUM_KEYS = 10;
    
    
    public static boolean testGet(TestData data[], MultiKeyList L, int keyIndex)
    {
        sortList(data, keyIndex);
        try 
        {
        boolean pass = true;
        for (int i = 0; i < data.length; i++)
        {
            if (! equal(data[i], L.get(i,  keyIndex)))
            {
                System.out.println("Error in get at index " + i + " for key " + keyIndex);
                pass = false;
            }            
        }
        return pass;
        } catch (Exception e)
        {
            System.out.println("Exception in get: " + e.toString());
            return false;

        }
    }
    
    public static boolean testIteratorBackward(TestData data[], MultiKeyList L, int keyIndex)
    {
        try
        {
            sortList(data, keyIndex);
            MultiKeyListIterator it = L.iterator(keyIndex);
            while (it.hasNext())
            {
                it.next();
            }
            int arrayIndex = data.length - 1;
            while (it.hasPrevious())
            {
                ListElem prev = it.previous();
                if (!equal(data[arrayIndex], prev))
                {
                    System.out.println("Error in testIteratorBackward (key index " + keyIndex + ")  Mismatch at List index " + arrayIndex);                    
                    return false;
                }
                arrayIndex--;
            }
            if (arrayIndex != -1)
            {
                System.out.println("Error in testIteratorBackward  (key index " + keyIndex + "):  iterator did not cover entire list");
                return false;
            }
        }
        catch (Exception e)
        {
            System.out.println("Error in testIteratorBackward  (key index " + keyIndex + "): " + e.toString());
            return false;
        }
        return true;
    }
    

    public static TestData[] buildDeletedList(TestData data[], int indicesToDelete[])
    {
        TestData data2[] = new TestData[data.length - indicesToDelete.length];

        int nextIndex = 0;        
        for (int i = 0; i < data.length; i++)
        {
            boolean skip = false;
            for (int j = 0; j < indicesToDelete.length; j++)
            {
                skip = skip || (i == indicesToDelete[j]);
            }
            if (!skip)
            {
                data2[nextIndex++] = data[i];
            }
        }
        return data2;


    }
    
    
    public static boolean testDeleteIndex(TestData data[], MultiKeyList L, int keyIndex)
    {
        sortList(data, keyIndex);

        final int INDICES_TO_SKIP = 4;
        int indiciesToSkip[] = new int[INDICES_TO_SKIP];
        for (int i = 0; i < INDICES_TO_SKIP - 1; i++)
        {
            indiciesToSkip[i] = i * (data.length / (INDICES_TO_SKIP + 3));
        }
        indiciesToSkip[INDICES_TO_SKIP-1] = data.length-1;
        
        TestData data2[] = buildDeletedList(data, indiciesToSkip);
        
        for (int i = 0; i < INDICES_TO_SKIP; i++)
        {
            L.removeIndex(indiciesToSkip[i] - i, keyIndex);   
        }
        boolean result = true;
        for (int i = 0; i < data2[0].keys.length; i++)
        {
            result = result && testIteratorForward(data2, L, i);
        }
        for (int j = 0; j < INDICES_TO_SKIP; j++)
        {
            L.add(data[indiciesToSkip[j]].keys, data[indiciesToSkip[j]].data);
        }
        return result;

    }
    
    public static boolean testDeleteKey(TestData data[], MultiKeyList L, int keyIndex)
    {
        sortList(data, keyIndex);
        
        final int INDICES_TO_SKIP = 4;
        int indiciesToSkip[] = new int[INDICES_TO_SKIP];
        for (int i = 0; i < INDICES_TO_SKIP - 1; i++)
        {
            indiciesToSkip[i] = i * (data.length / (INDICES_TO_SKIP + 3));
        }
        indiciesToSkip[INDICES_TO_SKIP-1] = data.length-1;
        
        TestData data2[] = buildDeletedList(data, indiciesToSkip);
        
        for (int i = 0; i < INDICES_TO_SKIP; i++)
        {
            L.remove(data[indiciesToSkip[i]].keys[keyIndex],keyIndex);
        }
        boolean result = true;
        for (int i = 0; i < data2[0].keys.length; i++)
        {
            result = result && testIteratorForward(data2, L, i);
        }
        for (int j = 0; j < INDICES_TO_SKIP; j++)
        {
            L.add(data[indiciesToSkip[j]].keys, data[indiciesToSkip[j]].data);
        }
        return result;

    }
    
    
    public static boolean testDeleteIterator(TestData data[], MultiKeyList L, int keyIndex)
    {
        sortList(data, keyIndex);

        final int INDICES_TO_SKIP = 4;
        int indiciesToSkip[] = new int[INDICES_TO_SKIP];
        for (int i = 0; i < INDICES_TO_SKIP - 1; i++)
        {
            indiciesToSkip[i] = i * (data.length / (INDICES_TO_SKIP + 3));
        }
        indiciesToSkip[INDICES_TO_SKIP-1] = data.length-1;
        
        TestData data2[] = buildDeletedList(data, indiciesToSkip);
                        
        int currIndex = 0;
        
        MultiKeyListIterator it = L.iterator(keyIndex);
        while (it.hasNext())
        {
            it.next();
            for (int j = 0; j < INDICES_TO_SKIP; j++)
            {
                if (indiciesToSkip[j] == currIndex)
                {
                    it.remove();
                }
            }
            currIndex++;
        }
        
        boolean result = true;
        for (int i = 0; i < data2[0].keys.length; i++)
        {
            result = result && testIteratorForward(data2, L, i);
        }
        for (int j = 0; j < INDICES_TO_SKIP; j++)
        {
            L.add(data[indiciesToSkip[j]].keys, data[indiciesToSkip[j]].data);
        }
        return result;
    }
    
    
    public static boolean testIteratorForward(TestData data[], MultiKeyList L, int keyIndex)
    {
        try
        {
            sortList(data, keyIndex);
            MultiKeyListIterator it = L.iterator(keyIndex);
            int arrayIndex = 0;
            while (it.hasNext())
            {
                ListElem next = it.next();
                if (!equal(data[arrayIndex], next))
                {
                    System.out.println("Error in testIteratorForward (key index " + keyIndex + ")  Mismatch at List index " + arrayIndex);                    
                    return false;
                }
                arrayIndex++;
            }
            if (arrayIndex != data.length)
            {
                System.out.println("Error in testIteratorForward  (key index " + keyIndex + "):  iterator did not cover entire list (" + arrayIndex + "," + data.length + ")");
                return false;
            }
        }
        catch (Exception e)
        {
            System.out.println("Error in testIteratorForward  (key index " + keyIndex + "): " + e.toString());
            return false;
        }
        return true;
    }
    
    
    public static void sortList(TestData data[], int keyIndex)
    {
        for (int i = 1; i < data.length; i++)
        {
            int insertIndex = i;
            TestData insertElem = data[i];
            while (insertIndex > 0 && data[insertIndex-1].keys[keyIndex].compareTo(insertElem.keys[keyIndex]) > 0)
            {
                data[insertIndex] = data[insertIndex - 1];
                insertIndex--;                
            }
            data[insertIndex] = insertElem;   
        }       
    }
    
    
    public static boolean forwardBacwardIterators(MultiKeyList L, int keyIndex)
    {
        MultiKeyListIterator it = L.iterator(keyIndex);
        while (it.hasNext())
        {
            ListElem l1 = it.next();
            ListElem l2 = it.previous();
            if (!equal(l1, l2))
            {
                System.out.println("Moving iterator forward then backward should return the same element ...");
                return false;
            }
            it.next();
        }
        return true;
    }
    
    private static boolean equal(ListElem l1, ListElem l2)
    {
        if (!l1.data().equals(l2.data()))
        {
            return false;
        }
        for (int i = 0; i < l1.numKeys(); i++)
        {
            if (!(l1.key(i).equals(l2.key(i))))
            {
                return false;
            }
        }
        return true;
    }

    public static boolean equal(TestData data, ListElem elem)
    {
        if (data.keys.length != elem.numKeys())
            return false;
        for (int i = 0; i < elem.numKeys(); i++)
        {
            if (data.keys[i].compareTo(elem.key(i)) != 0)
            {
                System.out.println("Failure at key " + i + ":" + data.keys[i] + "," + elem.key(i));
                return false;
            }
        }
        if (! data.data.equals(elem.data()))
        System.out.println("Failure at data " + data.data + "," + elem.data());
        return data.data.equals(elem.data());        
    }
    
    
    public static void main(String[] args)
    {
        MultiKeyList  L = new MyMultiKeyList(NUM_KEYS);
        TestData testData[] = createData(DATA_SIZE, NUM_KEYS);
        for (int i = 0; i < DATA_SIZE; i++)
        {
            L.add(testData[i].keys, testData[i].data);
        }
        
        boolean globalTest = true;
        boolean localTest = true;
        System.out.println("Testing Forward Iterator:");
        for (int keyIndex = 0; keyIndex < NUM_KEYS; keyIndex++)
        {
            boolean nextTest = testIteratorForward(testData, L, keyIndex);
            localTest = localTest && nextTest;
        }
        if (localTest)
        {
            System.out.println(" --  Forward Iterator OK");
        }
        else
        {
            System.out.println(" --  Forward Iterator FAIL"); 
        }
        globalTest = globalTest && localTest;
        localTest = true;
        System.out.println("Testing Backward Iterator");
        for (int keyIndex = 0; keyIndex < NUM_KEYS; keyIndex++)
        {
            localTest = localTest && testIteratorBackward(testData, L, keyIndex);
        }
        if (localTest)
        {
            System.out.println(" --  Backward Iterator OK");
            
        }
        else
        {
            System.out.println(" --  Backward Iterator FAIL"); 
        }
        globalTest = globalTest && localTest;
        localTest = true;
        
        System.out.println("Testing get");
        for (int keyIndex = 0; keyIndex < NUM_KEYS; keyIndex++)
        {
            localTest = localTest && testGet(testData, L, keyIndex);
        }
        if (localTest)
        {
            System.out.println(" --  get OK");
            
        }
        else
        {
            System.out.println(" --  get FAIL"); 
        }
        globalTest = globalTest && localTest;
        localTest = true;
   
        System.out.println("Testing delete (iterator)");
        for (int keyIndex = 0; keyIndex < NUM_KEYS; keyIndex++)
        {
            localTest = localTest && testDeleteIterator(testData, L, keyIndex);
        }
        if (localTest)
        {
            System.out.println(" --  delete (iterator) OK");
            
        }
        else
        {
            System.out.println(" --  delete (iterator) FAIL"); 
        }
        globalTest = globalTest && localTest;
        localTest = true;
        
        System.out.println("Testing delete (index)");
        for (int keyIndex = 0; keyIndex < NUM_KEYS; keyIndex++)
        {
            localTest = localTest && testDeleteIndex(testData, L, keyIndex);
        }
        if (localTest)
        {
            System.out.println(" --  delete (index) OK");
            
        }
        else
        {
            System.out.println(" --  delete (index) FAIL"); 
        }
        globalTest = globalTest && localTest;
        localTest = true;
        
        System.out.println("Testing delete (key)");
        for (int keyIndex = 0; keyIndex < NUM_KEYS; keyIndex++)
        {
            localTest = testDeleteKey(testData, L, keyIndex) && localTest;
        }
        if (localTest)
        {
            System.out.println(" --  delete (key) OK");
            
        }
        else
        {
            System.out.println(" --  delete (key) FAIL"); 
        }
        globalTest = globalTest && localTest;
        localTest = true;
        
        System.out.println("Testing forward / backward iterators");
        for (int keyIndex = 0; keyIndex < NUM_KEYS; keyIndex++)
        {
            localTest = forwardBacwardIterators(L, keyIndex) && localTest;
        }
        if (localTest)
        {
            System.out.println(" --  forward / backward iterators OK");
            
        }
        else
        {
            System.out.println(" --  forward / backward iterators FAIL"); 
        }
        globalTest = globalTest && localTest;
        localTest = true;
        
        
        
        
        System.out.println("Testing Exceptions");

        
        localTest = testExceptions();

        if (localTest)
        {
            System.out.println(" --  Exceptions OK");
            
        }
        else
        {
            System.out.println(" --  Exceptions FAIL"); 
        }
        
        globalTest = globalTest && localTest;

        if (globalTest)
        {
            System.out.println("All Passed!");
        }
        else
        {
            System.out.println("Some tests failed ...");
        }

    }



    private static boolean testExceptions()
    {
        MultiKeyList L = new MyMultiKeyList(3);
        boolean success = true;
             
        Comparable [] keys = new Comparable[2];
        for (int i = 0; i < 2; i++)
        {
            keys[i] = new Integer(1);
        }
        try
        {
            L.add(keys, new Integer(1));
            System.out.println("Should have thrown an exception!");      
            success = false;
        }
        catch (IllegalArgumentException e)
        {
            
        }
        catch (Exception e)
        {
            System.out.println("Incorrect exception thrown: " + e);
            success = false;
            
        }

        L = new MyMultiKeyList(NUM_KEYS);
        
        final int SMALL_LIST = 10;
        TestData testData[] = createData(SMALL_LIST, NUM_KEYS);
        for (int i = 0; i < SMALL_LIST; i++)
        {
            L.add(testData[i].keys, testData[i].data);
        }
        MultiKeyListIterator it = L.iterator(0);
        
        try
        {
            for (int i = 0; i < SMALL_LIST + 1; i++)
               it.next();
            System.out.println("Should have thrown an exception for going past the list");      
            success = false;
        }
        catch (NoSuchElementException e)
        {
            
        }
        catch (Exception e)
        {
            System.out.println("Incorrect exception thrown: " + e);
            success = false;
            
        }
        
        try
        {
          L.get(SMALL_LIST, 0);
          System.out.println("Should have thrown an exception for getting an illegal index");      
          L.get(SMALL_LIST+1, 0);
          success = false;

        }
        catch (IllegalArgumentException e)
        {
            
        }
        catch (Exception e)
        {
            System.out.println("Incorrect exception thrown: " + e);
            success = false;
            
        }
        try
        {
          MultiKeyListIterator it2 = L.iterator(NUM_KEYS);
          
          System.out.println("Should have thrown an exception for illegal index");   
          success = false;

        }
        catch (IllegalArgumentException e)
        {
            
        }
        catch (Exception e)
        {
            System.out.println("Incorrect exception thrown: " + e);
            success = false;   
        }
        
        try
        {
          MultiKeyListIterator it2 = L.iterator(0);
          
          it2.remove();
          System.out.println("Should have thrown an exception for removing before moving forward");   
          success = false;
        }
        catch (IllegalStateException e)
        {
            
        }
        catch (Exception e)
        {
            System.out.println("Incorrect exception thrown for removing before moving forward " + e);
            success = false;   
        }     
        
        try
        {
          MultiKeyListIterator it2 = L.iterator(0);
          
          it2.next();
          it2.remove();
          it2.remove();
          System.out.println("Should have thrown an exception for removing twice in a row");   
          success = false;
        }
        catch (IllegalStateException e)
        {
            
        }
        catch (Exception e)
        {
            System.out.println("Incorrect exception thrown for removing twice in a row " + e);
            success = false;   
        }     
        
        return success;
    }

    private static TestData [] createData(int dataSize, int numKeys)
    {
        Random r = new Random(32L); // Specific seed for repeatability 
        
        TestData newData[] = new TestData[dataSize];
        
        for (int i = 0; i < dataSize; i++)
        {
            newData[i] = new TestData(numKeys);
            for (int j = 0; j < numKeys; j++)
            {
                newData[i].keys[j] = new Integer(r.nextInt());                
            }
            newData[i].data = new Integer(i);
        }
        return newData;
    }

}
