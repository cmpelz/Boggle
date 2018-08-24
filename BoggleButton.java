import javax.swing.JButton;

/*
 * Class BoggleButton - write a description of your class here
 * 
 * Casey Pelz and Seth Goldstein
 */
public class BoggleButton extends JButton
{
    //instance variables to hold aspects of buttons
    private int row;
    private int column;
    private String letter;
   
    private int [] word;
        
    private BoggleGrid buttons;    
   
    //contructs button based on parameters
    public BoggleButton( int row, int column, String letter) {
      this.row = row;
      this.column = column;
      this.letter = letter;
    }
    
    // Return the row number of this button
    public int getRow() {
        return row;
    }
    
    // Return the column number of this button
    public int getColumn() {
        return column;
    }
        
    // Return true if other button is adjacent to this button horizontally, vertically, or diagonally
    public boolean isAdjacentTo( BoggleButton other ) {
        return other != this &&
               Math.abs( row - other.getRow() ) <= 1 &&  
               Math.abs( column - other.getColumn() ) <= 1;    
    }
}
