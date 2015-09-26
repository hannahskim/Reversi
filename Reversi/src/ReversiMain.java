// Extra imports required for GUI code
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/** The ReversiMain class - creates the JFrame for Reversi
  * Plays a simple game of Reversi using ReversiBoard class
  * @author Jessica Lei and Hannah Kim
  * @version December 2013
  */
public class ReversiMain extends JFrame implements ActionListener
{
    // Program variables for the Menu items and the game board
    private JMenuItem newOption, exitOption, aboutMenuItem;
    private ReversiBoard gameBoard;

    /** Constructs a new ConnectFourMain frame (sets up the Game)
      */
    public ReversiMain ()
    {
        // Sets up the frame for the game
        super ("Reversi");
        setResizable (false);

        // Load up the icon image (cat)
        setIconImage (Toolkit.getDefaultToolkit ().getImage ("cat.png"));

        // Sets up the Reversi board that plays most of the game
        // and add it to the center of this frame
        gameBoard = new ReversiBoard ();
        getContentPane ().add (gameBoard, BorderLayout.CENTER);

        // Center the frame in the middle (almost) of the screen
        Dimension screen = Toolkit.getDefaultToolkit ().getScreenSize ();
        setLocation ((screen.width - gameBoard.BOARD_SIZE.width) / 2,
                (screen.height - gameBoard.BOARD_SIZE.height) / 2);

        // Adds the menu and menu items to the frame (see below for code)
        // Set up the Game MenuItems
        newOption = new JMenuItem ("New");
        newOption.setAccelerator (
                KeyStroke.getKeyStroke (KeyEvent.VK_N, InputEvent.CTRL_MASK));
        newOption.addActionListener (this);

        exitOption = new JMenuItem ("Exit");
        exitOption.setAccelerator (
                KeyStroke.getKeyStroke (KeyEvent.VK_X, InputEvent.CTRL_MASK));
        exitOption.addActionListener (this);

        // Set up the Help Menu
        JMenu helpMenu = new JMenu ("Help");
        helpMenu.setMnemonic ('H');
        aboutMenuItem = new JMenuItem ("About...", 'A');
        aboutMenuItem.addActionListener (this);
        helpMenu.add (aboutMenuItem);

        // Add each MenuItem to the Game Menu (with a separator)
        JMenu gameMenu = new JMenu ("Game");
        gameMenu.add (newOption);
        gameMenu.addSeparator ();
        gameMenu.add (exitOption);
        JMenuBar mainMenu = new JMenuBar ();
        mainMenu.add (gameMenu);
        mainMenu.add (helpMenu);
        // Set the menu bar for this frame to mainMenu
        setJMenuBar (mainMenu);
    } // Constructor


    /** Responds to a Menu Event.  This method is needed since our
      * REversi frame implements ActionListener
      * @param event the event that triggered this method
      */
    public void actionPerformed (ActionEvent event)
    {
        if (event.getSource () == newOption)   // Selected "New"
        {
            gameBoard.newGame ();
        }
        else if (event.getSource () == exitOption)  // Selected "Exit"
        {
            hide ();
            System.exit (0);
        }
        else if (event.getSource () == aboutMenuItem)  // Selected "About"
        {
            JOptionPane.showMessageDialog (this,
                    "by Jessica Lei and Hannah Kim" +
                    "\n\u00a9 2013", "About Reversi",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }


    /** Starts up the ReversiMain frame
      * @param args An array of Strings (ignored)
      */
    public static void main (String[] args)
    {
        // Starts up the ReversiMain frame
        ReversiMain frame = new ReversiMain ();
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.pack ();
        frame.setVisible (true);
    } // main method
}




