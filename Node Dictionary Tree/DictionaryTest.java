import java.io.File;
import java.util.Scanner;


public class DictionaryTest
{
    static final int NUM_SUGGESTIONS = 4;
    
    public static boolean testWordFile(String filename) throws Exception
    {
        Dictionary d = new Dictionary(filename);
        boolean passed = true;        
        try
        {
            Scanner s = new Scanner(new File(filename));
            while (s.hasNext())
            {
                String nextStr = s.nextLine();
                if (nextStr.length() > 0)
                {
                    if (!d.check(nextStr))
                    {
                        passed = false;
                        System.out.println("Failed to find word: " + nextStr);                        
                    }
                    for (int i = 0; i <= nextStr.length(); i++)
                    {
                        if (!d.checkPrefix(nextStr.substring(0,i)))
                        {
                            passed = false;
                            System.out.println("Failed to find prefix: " + nextStr.substring(0,i) + " of " + nextStr);       
                        }   
                    }
                }
            }
            String badWords[] = {"accer", "fatte", "flox", "forg", "forsoom"};
            for (int i = 0; i < badWords.length; i++)
            {
                System.out.println("Trying " + badWords[i]);
                String result[] = d.suggest(badWords[i], NUM_SUGGESTIONS);
                if (result.length != NUM_SUGGESTIONS)
                {
                    System.out.println("Didn't get correct number of suggestions for " +badWords[i]);
                    System.out.println("  Expected " + NUM_SUGGESTIONS + ", got " + result.length);
                    passed = false;
                }
                for (int j = 0; j < result.length; j++)
                {
                    System.out.println("  " + result[j]);
                    if (!d.check(result[j]))
                    {
                        System.out.println("Suggestion " +result[j] + " not in dictionary.");   
                        passed = false;
                    }
                }
            }
            String goodWords[] = { "cat", "baseball", "original"};
            for (int i = 0; i < goodWords.length; i++)
            {
                String result[] = d.suggest(goodWords[i], NUM_SUGGESTIONS);
                if (result.length != 1)
                {
                    System.out.println("Word " + goodWords[i] + " is in the dictionary -- suggest should return 1 item");
                    System.out.println("   " + result.length + " items returned instead");
                    passed = false;
                }
            }

        }       
        catch (Exception e)
        {
            System.out.println(e);
            passed = false;
        }
        return passed;
    }
    
    public static boolean smallTest()
    {
        Dictionary d = new Dictionary();
        
        boolean passed = true;
        
        String words[] = {"cat", "cart","dog", "apple", "ape", "breakfast", "breakneck", "queasy", "quash", 
                          "quail", "quick", "reason", "rickshaw", "reality"};
        
        String nonWords[] = {"carts", "ap", "break", "qu", "reasons", "buttercup", "under"};
        
        for (int i = 0; i < words.length; i++)
        {
            d.add(words[i]);            
        }
        for (int i = 0; i < nonWords.length; i++)
        {
            if (d.check(nonWords[i]))
            {
                System.out.println("Found word:"+nonWords[i] + ", shoudn't have");
                passed = false;
            }
        }
        
        d.printTree();
        d.print();

        return passed;
        
    }

    public static void main(String args[]) throws Exception
    {
        boolean passedTests;
        
        passedTests = testWordFile("words_ospd.txt");
        passedTests &= smallTest();
        
        if (!passedTests)
        {
            System.out.println("At least one test failed");
        }
        else
        {
            System.out.println("Automated tests passed.");
            System.out.println("Be sure to check output for tree printing & suggest by hand!");

            
        }
        
    }
    
    
}
