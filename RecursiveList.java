
/**
 * Contains methods to manipulate lists of words
 * 
 * Casey Pelz and Seth Goldstein
 * 
 */
public class RecursiveList
{
    //Instance variables for the list of words, first word, and whether the list is empty
    private String firstWord;       
    private RecursiveList restOfWords;
    private boolean empty = false; 

    /**
     * Constructor for objects of class RecursiveClass
     */
    public RecursiveList()
    {
        empty = true;
    }
    
    //constructor that creates the recursive list
    public RecursiveList( String newWord, RecursiveList existingWords ) {
        firstWord = newWord;
        restOfWords = existingWords;
    }
    
    //turns recursive list into a string
     public String toString() {
        if ( empty ) {
            return "";
        } else {
            return firstWord + "\n" + restOfWords.toString();
        }
    }
    
    // determines whether the collection contains a given entry
    public boolean contains( String word ) {
        if ( empty ) {
            return false;
        } else if ( firstWord.equals( word ) ) {
            return true;
        } else {
            return restOfWords.contains( word );
        }
    } 
}
