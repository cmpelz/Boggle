import squint.*;
import javax.swing.*;
import java.util.Random;
import java.awt.*;

/*
 * Class BoggleGrid - Creates and interacts with the grid of buttons
 * 
 * Casey Pelz and Seth Goldstein
 */
public class BoggleGrid extends GUIManager
{
    // Change these values to adjust the size of the program's window
    private final int WINDOW_WIDTH = 400, WINDOW_HEIGHT = 400;

    //defines dimensions of grid of buttons
    public final int WIDTH = 4;
    public final int HEIGHT = 4;

    //creates a new random object
    private Random chooser = new Random();

    // Size of text displayed in puzzle pieces
    private final float FONT_SIZE = 36;

    // Font used for buttons
    private final Font BIGFONT = this.getFont().deriveFont( FONT_SIZE );

    // How many random moves to make when shuffling the puzzle pieces
    private int SHUFFLE_STEPS = 200;

    //creates array to hold all the buttons
    private BoggleButton [] allButtons = new BoggleButton[WIDTH*HEIGHT];

    //letters from which cubes will be made
    private final String [][] cubeSides = new String[][]{
            { "A", "A", "C", "I", "O", "T" },
            { "A", "B", "I", "L", "T", "Y" },
            { "A", "B", "J", "M", "O", "Qu" },
            { "A", "C", "D", "E", "M", "P" },
            { "A", "C", "E", "L", "S", "R" },
            { "A", "D", "E", "N", "V", "Z" },
            { "A", "H", "M", "O", "R", "S" },
            { "B", "F", "I", "O", "R", "X" },
            { "D", "E", "N", "O", "S", "W" },
            { "D", "K", "N", "O", "T", "U" },
            { "E", "E", "F", "H", "I", "Y" },
            { "E", "G", "I", "N", "T", "V" },
            { "E", "G", "K", "L", "U", "Y" },
            { "E", "H", "I", "N", "P", "S" },
            { "E", "L", "P", "S", "T", "U" },
            { "G", "I", "L", "R", "U", "W" }
        };

    //array to hold all randomly determined letters
    private String [] letters = new String [16];

    //array to keep track of which buttons have been pressed
    private BoggleButton[] buttonsPressed = new BoggleButton[16];

    private int counter = 0;

    //BoggleBoard object
    private BoggleBoard gameBoard;

    //button to keep track of the previous button clicked
    private BoggleButton lastButton = null;

    //creates array to hold all words created
    private String [] wordsMade;

    //counts number of words created
    private int wordCounter = 0;

    //constructor for a new BoggleGrid given a BoggleBoard as a parameter
    public BoggleGrid( BoggleBoard gameBoard) {
        //adds grid to contentPane
        contentPane.setLayout( new GridLayout(HEIGHT, WIDTH, 0, 0));

        this.gameBoard = gameBoard;

        //adds buttons to the grid
        int letterPosition = 0;
        letters = getLetters(cubeSides);
        for ( int y = 0; y < HEIGHT; y++ ) {
            for ( int x = 0; x < WIDTH; x++ ) {
                String letter = letters[letterPosition];
                BoggleButton button = new BoggleButton( y, x, letter );
                allButtons[ y*WIDTH + x ] = button;
                contentPane.add( button );
                letterPosition++;
            }
        }
    }

    //sets the text of each button to the randomly found letter
    public void setButtonText( String [] labels ) {
        for ( int p = 0; p < allButtons.length; p++ ) {
            allButtons[p].setIcon( null );

            if ( p < labels.length ) {
                allButtons[p].setText( labels[p] );
            } else {
                allButtons[p].setText( "" );
            }

            allButtons[p].setFont( BIGFONT );
        }
    }

    //gets the letter on a cube at the given position
    public String getLetterName( int row, int column ) {
        return cubeSides[row][column];
    }

    //creates and returns an array of 16 randomly generated letters
    public String [] getLetters( String [][] cubesides ){
        int remaining = 15;
        int rows = 16;
        int i = 0;
        while ( remaining >= 0 ) {
            int randomColumn = chooser.nextInt( 6 );
            int randomRow = chooser.nextInt( rows );

            letters[i] = getLetterName( randomRow, randomColumn );
            cubeSides[randomRow] = cubeSides[remaining];

            remaining--;
            rows--;
            i++;
        }
        return letters;
    }

    //defines what happens when a BoggleButton is pressed
    public void buttonClicked( JButton which ) {
        BoggleButton whichButton = (BoggleButton) which;
        boolean inWord = false;

        //adds the letter of the first button pressed to the array holding the letters of the word being created
        if( counter == 0 ) {
            buttonsPressed[0] = whichButton;
            gameBoard.setWord( whichButton.getText() );
            lastButton = buttonsPressed[0];
            counter++;
            
            //sets the color of the button pressed to red
            lastButton.setForeground( Color.RED );
        } else if( whichButton == lastButton ) {            
            //calls the setWordList method of the BoggleBoard class if a button is pressed twice
            //signifies the end of a word
            gameBoard.setWordList();
            counter = 0;
            
            //resets color of all buttons
            setColor();
        } else {
            //checks if the button has been pressed yet in the creation of the current word
            for( int i= 0; i < counter; i++ ) {
                if( whichButton == buttonsPressed[i] ) {
                    inWord = true;
                }
            }

            //checks if the button pressed is adjacent to the previous button pressed
            if ( whichButton.isAdjacentTo( lastButton ) && inWord == false ) {
                buttonsPressed[counter] = whichButton;
                gameBoard.setWord( whichButton.getText() );
                lastButton = buttonsPressed[counter]; 
                
                //sets the color of the previous button pressed to green and the current button pressed to red
                lastButton.setForeground( Color.RED );
                buttonsPressed[counter - 1].setForeground( Color.GREEN );
                
                counter++;  
            }            
        } 
    }

    //turns the letters of the word being created into a string and returns it
    public String word ( BoggleButton [] letters ) {
        String word = "";
        for( int i = 0; i < letters.length; i++ ) {
            word = word + letters[i].getText();
        }
        return buttonsPressed.toString();
    }

    //disables all the BoggleButtons
    public void clearButtons() {
        for( int i = 0; i < allButtons.length; i++ ) {
            allButtons[i].setEnabled( false );
        }
    }
 
    //resets color of all buttons to black
    public void setColor() {
       for( int i = 0; i < 16; i++ ) {
           allButtons[i].setForeground( Color.BLACK );
       }
    }
}

