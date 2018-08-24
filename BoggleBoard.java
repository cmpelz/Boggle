import squint.*;
import javax.swing.*;
import java.util.Random;
import java.awt.*;
import java.io.File;

/*
 * Class BoggleBoard - controls the entire game
 * 
 * casey Pelz and Seth Goldstein
 */
public class BoggleBoard extends GUIManager
{
    // Defines the final variables for the class
    private final int WINDOW_WIDTH = 1000, WINDOW_HEIGHT = 700;
    private final int GAME_DURATION = 180;
    private final String SERVER = "rath.cs.williams.edu";
    private final int PORT = 13415;

    //creates all JButtons
    private JButton start = new JButton ("Start New Game");
    private JButton partner = new JButton ("Find Partner");
    private JButton concede = new JButton ("Concede");

    //Creates all JLabels
    private JLabel time = new JLabel ("");
    private JLabel current = new JLabel ("");
    private JLabel score = new JLabel ("Score: ");
    private JLabel myScore = new JLabel ("0");
    private JLabel vs = new JLabel ("");
    private JLabel opponentScore = new JLabel ("");
    private JLabel nameLabel = new JLabel ("Your Name: ");
    private JLabel groupLabel = new JLabel ("Partner Group: ");
    private JLabel opponentLabel = new JLabel ("");

    //creates all JTextFields
    private JTextField yourName = new JTextField (10);
    private JTextField partnerGroup = new JTextField (10);

    //creates JPanels to groupd window elements
    private JPanel name = new JPanel();
    private JPanel group = new JPanel();
    private JPanel top = new JPanel();
    private JPanel bottom = new JPanel();
    private JPanel bottomButtons = new JPanel();
    private JPanel bottomText = new JPanel();

    //Creates JTextArea
    private JTextArea wordList = new JTextArea( 10, 20);

    //Creates all elements corresponding to the timer 
    //including a JProgressBar
    private String gameClock = "Time remaining: ";
    private JLabel gameClockLabel = new JLabel(gameClock);
    private int timeRemaining = GAME_DURATION;
    private final int TICK_DURATION = 1;
    private JProgressBar progress;
    private PaceMaker timer;

    //creates a new BoggleGrid object 
    private BoggleGrid buttons;

    //creates a new Lexicon object
    private Lexicon lexicon = new Lexicon();

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

    //creates a new NetConnection object
    private NetConnection toServer;

    //creates 3 new RecursiveLists
    //master list, my list, and the opponent's list
    private RecursiveList list = new RecursiveList();
    private RecursiveList myList = new RecursiveList();
    private RecursiveList theirList = new RecursiveList();

    //creates an array to keep track of letters received
    private String [] lettersReceived;

    //creates variables to track scores
    private int ownScore;
    private int partnerScore;

    //creates variables to track opponent's name and word
    private String opponentWord;
    private String opponentName;

    //Creates a new BoggleBoard object
    public BoggleBoard() {
        // Create window to hold all the components
        this.createWindow( WINDOW_WIDTH, WINDOW_HEIGHT );
        contentPane.setLayout( new BorderLayout() );

        //adds BoggleGrid to window
        buttons = new BoggleGrid( this );
        contentPane.add( buttons, BorderLayout.CENTER);

        //sets up progress bar
        progress = new JProgressBar( 0, GAME_DURATION );
        progress.setValue( 0 );
        progress.setStringPainted( true );
        progress.setString( GAME_DURATION + " seconds" );

        //adds elements to the contentPane
        contentPane.add( time, BorderLayout.NORTH  );

        contentPane.add( new JScrollPane( wordList ), BorderLayout.EAST );

        name.add( nameLabel ); 
        name.add( yourName ); 

        group.add( groupLabel );
        group.add( partnerGroup ); 

        bottomText.add( opponentLabel, BorderLayout.NORTH );
        bottomText.add( group, BorderLayout.SOUTH );
        bottomText.add( name, BorderLayout.NORTH );

        top.add( current );
        top.add( gameClockLabel );
        top.add( progress );
        top.add( score );
        top.add( myScore );
        top.add( vs );
        top.add( opponentScore );

        bottomButtons.add( start );
        bottomButtons.add( partner );
        bottomButtons.add( concede );

        bottom.add( bottomButtons, BorderLayout.WEST );
        bottom.add( bottomText, BorderLayout.EAST );

        contentPane.add( bottom, BorderLayout.SOUTH ); 

        contentPane.add( top, BorderLayout.NORTH );  

        //disables the JTextArea
        wordList.setEditable( false ); 
    }

    //defines what happens when each button is pressed
    public void buttonClicked( JButton which ) {
        //starts game if the start button is pressed
        if( which == start ) {
            this.startGame();
        } else if( which == partner ) { //defines what happens if the partner button is pressed
            //creates NetConnection and sends your name and group name if entered
            if( !yourName.getText().equals( "" ) && partnerGroup.getText().equals( "" ) ) {
                toServer = new NetConnection( SERVER, PORT );    
                toServer.out.println( "PLAY " + yourName.getText() );
                toServer.addMessageListener( this );
            } else if( !yourName.getText().equals( "" ) && !partnerGroup.getText().equals( "" ) ) {
                toServer = new NetConnection( SERVER, PORT );    
                toServer.out.println( "PLAY " + yourName.getText() + " " + partnerGroup.getText() );
                toServer.addMessageListener( this );
            }
        } else if( which == concede ) {
            //ends the game if the concede button is pressed
            endGame();
        }
    }

    //defines how to start game
    public void startGame() {        
        //disables any previously running timers
        if( timer != null ) {
            timer.stop();
        }
        //creates a new timer
        timer = new PaceMaker(TICK_DURATION, this);
        timeRemaining = GAME_DURATION;
        
        //sets the text of the cubes, JTextArea, and score
        buttons.setButtonText( buttons.getLetters( cubeSides ) );
        wordList.setText( "" );
        myScore.setText( "0" );
        ownScore = 0;
    }

    // Decrement the game timer and JProgressBar
    public void tick() {
        timeRemaining--;
        progress.setValue( timeRemaining );
        progress.setString( timeRemaining + " seconds" );
        
        //ends game if time runs out
        if( timeRemaining == 0 ) {
            endGame();
        }
    }

    //displays current word
    public void setWord( String newLetter ) {
        current.setText( current.getText() + newLetter );
    }

    //displays all words made in the JTextArea
    public void setWordList( ) {
        if( !isValidWord() ) {
            //clears the current word if it's not valid
            current.setText("");
        } else if( toServer == null && isValidWord() ) { //defines what happens if the word's valid in solitaire          
            //adds word to RecursiveList and displays it in the JTextArea
            this.addMyWord( current.getText() );
            wordList.setText( myList.toString() );
            
            //updates and displays score
            ownScore = computeScore( ownScore, current.getText() );
            myScore.setText( "" + ownScore );
            
            //resets JLabel displaying current word
            current.setText("");
        } else if ( toServer != null && isValidWord() ) { //defines what happens if the word is valid in partner play
            if( myList.contains( current.getText() ) ) {
                //clears current word if it's already in my list
                current.setText("");
            } else {
                if( !list.contains( current.getText() ) ) { //defines what happens if the word isn't in the master list yet
                    //adds word to my list and the master list
                    this.addWord( current.getText() );
                    this.addMyWord( current.getText() );
                    
                    //sends current word to opponent
                    toServer.out.println( "WORD " + current.getText() );
                    
                    //computes and displays score
                    ownScore = computeScore( ownScore, current.getText() );
                    myScore.setText( "" + ownScore );
                    
                    //displays word in the JTextArea
                    wordList.setText( "---MY WORDS---" + "\n" + myList.toString() );
                    //wordList.setText( myList.toString() );
                    
                    //clears the current word
                    current.setText("");
                } else {
                    //adds word to my list
                    this.addMyWord( current.getText() );
                    
                    //sends current word to opponent
                    toServer.out.println( "WORD " + current.getText() );
                    
                    //decrements opponent's score due to duplicate word
                    partnerScore = updateScore( partnerScore, current.getText() );
                    opponentScore.setText( "" + partnerScore );
                    
                    //displays word in the JTextArea
                    wordList.setText( "---MY WORDS---" + "\n" + myList.toString() );
                    
                    //clears the current word
                    current.setText("");
                }
            }
        }
    }
    
    //adds word to the master list recursively
    public void addWord( String word ) {
        if( lexicon.contains( word ) ) {
            list = new RecursiveList( word, list );
        }
    }
    
    //adds word to my list recurively
    public void addMyWord( String word ) {
        if( lexicon.contains( word ) ) {
            myList = new RecursiveList( word, myList );
        }
    }
    
    //adds word to their list recurively
    public void addTheirWord( String word ) {
       if( lexicon.contains( word ) ) {
           theirList = new RecursiveList( word, theirList );
        }
    }

    //checks if the word created is valid
    public boolean isValidWord( ) { 
        if( current.getText().length() < 3 ) {
            return false;
        } 

        if( myList.contains( current.getText() ) ) {
            return false;
        }

        return lexicon.contains( current.getText() );      
    }

    //ends game
    public void endGame() {        
        //stops timer and disables buttons
        timer.stop();
        buttons.clearButtons();
        
        //closes connection if in partner play
        if( toServer != null ) {
            toServer.close();
        }
    }
    
    //defines what happens when you receive a message from opponent
    public void dataAvailable() {
        //takes in line from server
        String line = toServer.in.nextLine();

        //defines what happens if the message received is a "START" message
        if( line.contains( "START " ) ) {
            //starts timer
            timer = new PaceMaker(TICK_DURATION, this);
            timeRemaining = GAME_DURATION;
            
            //extracts opponent's name
            opponentName = line.substring( 5 );
            
            //fills array with letters received from server
            lettersReceived = new String [16];
            for( int i = 0; i < 16; i++ ) {                
                line = toServer.in.nextLine();
                lettersReceived[i] = line;
            }
            //sets text of buttons to display letters received from server
            buttons.setButtonText( lettersReceived );
            
            //displays opponent's name and sets up score
            opponentLabel.setText( "Playing: " + opponentName ); 
            vs.setText( "vs." );
            opponentScore.setText( "0" );
        } else { //defines what happens if message received is "WORD" message
            //extracts opponents word 
            opponentWord = line.substring( 5 );
            
            if( lexicon.contains( opponentWord ) ) { //checks if word received is in the Lexicon
                if( !list.contains( opponentWord ) ) { //defines what happens if the word isn't in the master list yet
                    //adds word to the master list
                    this.addWord( opponentWord );
                    this.addTheirWord( opponentWord );
                    
                    //updates and displays opponent's score
                    partnerScore = computeScore( partnerScore, opponentWord );
                    opponentScore.setText( "" + partnerScore );
                } else {
                    //decrements my score if opponent's word is a word I already made
                    ownScore = updateScore( ownScore, opponentWord );
                    myScore.setText( "" + ownScore );
                }
            }
        }
    }
 
    //ends game when the connection gets closed
    public void connectionClosed() {
        timer.stop();
        buttons.clearButtons();
        
        //displays opponent's words when the game ends
        wordList.append( "---THEIR WORDS---" + "\n" + theirList.toString() );
    }

    //computes any increases to scores
    public int computeScore ( int score, String word ) {
        int theScore;
        if( word.length() == 3 || word.length() == 4 ) {
            theScore = score + 1;
        } else if( word.length() == 5 ) {
            theScore = score + 2;
        } else if( word.length() == 6 ) {
            theScore = score + 3;
        } else if( word.length() == 7 ) {
            theScore = score + 5;
        } else {
            theScore = score + 11;
        } 
        return theScore;
    }

    //computes any decreases to scores
    public int updateScore ( int score, String word ) {
        int theScore;
        if( word.length() == 3 || word.length() == 4 ) {
            theScore = score - 1;
        } else if( word.length() == 5 ) {
            theScore = score - 2;
        } else if( word.length() == 6 ) {
            theScore = score - 3;
        } else if( word.length() == 7 ) {
            theScore = score - 5;
        } else {
            theScore = score - 11;
        } 
        return theScore;
    }
}

