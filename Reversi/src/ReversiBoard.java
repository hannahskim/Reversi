import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * The "Reversi" class. Handles the board play for Reversi
 * 
 * @author Jessica Lei Hannah Kim
 * @version December 2013
 */

public class ReversiBoard extends JPanel implements MouseListener
{
	// Program constants (declared at the top, these can be used by any method)
	private final int HINTS = -2;
	private final int CAT = -1;
	private final int DOG = 1;
	private final int EMPTY = 0;

	private final int SQUARE_SIZE = 70;
	private final int CHANGING_SPEED = 85;
	private final int NO_OF_ROWS = 8;
	private final int NO_OF_COLUMNS = 8;
	private final boolean ANIMATION_ON = true;

	// Images of pieces, title screen, and an instruction page
	private final String IMAGE_FILENAME_PLAYER1 = "cat.png";
	private final String IMAGE_FILENAME_PLAYER2 = "dog.png";
	private final String IMAGE_FILENAME_FLIP5 = "cat2.png";
	private final String IMAGE_FILENAME_FLIP4 = "cat3.png";
	private final String IMAGE_FILENAME_FLIP3 = "cat4.png";
	private final String IMAGE_FILENAME_FLIP2 = "dog5.png";
	private final String IMAGE_FILENAME_FLIP1 = "dog6.png";
	private final String IMAGE_FILENAME_TITLE = "titleScreenEx.png";
	private final String IMAGE_FILENAME_INSTRUCTION = "HowToPlayPage1.png";
	public final Dimension BOARD_SIZE = new Dimension(NO_OF_COLUMNS
			* SQUARE_SIZE + 200, (NO_OF_ROWS + 1) * SQUARE_SIZE + 25);

	// Program variables (declared at the top, these can be
	// used or changed by any method)
	private int[][] board;
	private boolean[][] flipped;
	private int currentPlayer;
	private int currentColumn;
	private Image firstImage, secondImage, background, howToPlay;
	private Image[] flipImages;
	private int flipSequence;
	private boolean gameOver;
	private int noOfHint = 0;
	private boolean startingPage;
	private boolean humanVsHuman;
	private boolean humanVsCom;
	private boolean howToPlayPage;

	// Make variables to count moves
	private int noOfMove = 0;
	private int noOfDog = 2;
	private int noOfCat = 2;
	private int bestRow = 0;
	private int bestCol = 0;
	private int mostFlip = 0;
	private int[] rowStr = { 0, 1, -1, 0, 1, -1, 1, -1 };
	private int[] colStr = { 1, 0, 0, -1, 1, 1, -1, -1 };

	/**
	 * Constructs a new ReversiBoard object
	 */
	public ReversiBoard()
	{
		// Sets up the board area, loads in piece images and starts a new game
		setPreferredSize(BOARD_SIZE);

		setBackground(Color.WHITE);
		// Add mouse listeners and Key Listeners to the game board
		addMouseListener(this);
		setFocusable(true);
		requestFocusInWindow();
		addMouseMotionListener(new MouseMotionHandler());

		// Load up the images for the pieces, background, etc
		flipImages = new Image[5];
		flipImages[0] = new ImageIcon(IMAGE_FILENAME_FLIP1).getImage();
		flipImages[1] = new ImageIcon(IMAGE_FILENAME_FLIP2).getImage();
		flipImages[2] = new ImageIcon(IMAGE_FILENAME_FLIP3).getImage();
		flipImages[3] = new ImageIcon(IMAGE_FILENAME_FLIP4).getImage();
		flipImages[4] = new ImageIcon(IMAGE_FILENAME_FLIP5).getImage();
		firstImage = new ImageIcon(IMAGE_FILENAME_PLAYER1).getImage();
		secondImage = new ImageIcon(IMAGE_FILENAME_PLAYER2).getImage();
		background = new ImageIcon(IMAGE_FILENAME_TITLE).getImage();
		howToPlay = new ImageIcon(IMAGE_FILENAME_INSTRUCTION).getImage();

		// Sets up the board array and starts a new game
		board = new int[NO_OF_ROWS + 2][NO_OF_COLUMNS + 2];
		flipped = new boolean[NO_OF_ROWS + 2][NO_OF_COLUMNS + 2];
		newGame();
	}

	/**
	 * Starts a new game
	 */
	public void newGame()
	{
		// Let the dog(black) piece always start first
		currentPlayer = DOG;
		clearBoard();
		gameOver = false;
		currentColumn = NO_OF_COLUMNS / 2 + 1;
		// Set pieces on the middle of the board to start a new game
		board[4][4] = CAT;
		board[4][5] = DOG;
		board[5][5] = CAT;
		board[5][4] = DOG;
		repaint();
		noOfMove = 4;
		noOfDog = 2;
		noOfCat = 2;
		startingPage = true;
		humanVsHuman = false;
		humanVsCom = false;
		hints();
	}

	/**
	 * clears the board
	 */
	private void clearBoard()
	{
		// Make each square empty
		for (int row = 0; row <= NO_OF_ROWS; row++)
		{
			for (int column = 0; column <= NO_OF_COLUMNS; column++)
				board[row][column] = EMPTY;
		}
	}

	/**
	 * Check if the move user made is valid
	 * 
	 * @param currentRow the row user clicked
	 * @param currentCol the column user clicked
	 * @return true if the move user made is valid false if the move user made
	 *         is not a valid move
	 */
	private boolean isValidMove(int currentRow, int currentCol)
	{
		// The move is not a valid move if the square is occupied by any player
		// The move is a valid move only when the user clicked on
		// one of the hints
		if (board[currentRow][currentCol] == HINTS && !startingPage)
		{
			return true;
		}
		return false;
	}

	/**
	 * Show all the possible move user can make. The move is a
	 * valid move only if there is opposite pieces around it in 8 directions and
	 * current player pieces next to the opposite piece in the same direction.
	 * Call the "check" method to find all the possible move and show the hints.
	 */
	private void hints()
	{
		for (int row = 1; row <= NO_OF_ROWS; row++)
		{
			for (int column = 1; column <= NO_OF_COLUMNS; column++)
			{
				if (board[row][column] == currentPlayer)
				{
					if (board[row - 1][column] == currentPlayer * -1
							|| board[row][column - 1] == currentPlayer * -1
							|| board[row - 1][column - 1] == currentPlayer * -1
							|| board[row][column + 1] == currentPlayer * -1
							|| board[row + 1][column + 1] == currentPlayer * -1
							|| board[row + 1][column - 1] == currentPlayer * -1
							|| board[row - 1][column + 1] == currentPlayer * -1
							|| board[row + 1][column] == currentPlayer * -1)
					{
						check(row, column);
					}
				}

			}
		}
	}

	/**
	 * In all 8 directions, keep checking if the next piece is a
	 * opposite piece. If the next piece is not an opposite piece but its an
	 * empty piece, the piece is a potential move. And mark the empty piece as
	 * hint. Also,in the process of finding the potential move, use strategy by
	 * making a method "computer move" to find the best move computer and player
	 * can make.
	 * 
	 * @param currentRow the row to check
	 * @param currentCol the column to check
	 */
	private void check(int currentRow, int currentCol)
	{
		// Search in all 8 directions
		int pos = 0;
		while (pos < rowStr.length)
		{
			int rowDir = rowStr[pos];
			int colDir = colStr[pos];
			int row = currentRow + rowDir;
			int col = currentCol + colDir;
			int count = 0;
			// The direction might has potential move if the first piece next to
			// the piece to check in the direction
			// is an opposite piece
			if (board[row][col] == -1 * currentPlayer)
			{
				// keep checking if the next piece is a opposite piece
				while (row <= 8 && row > 0 && col <= 8 && col > 0
						&& board[row][col] == -1 * currentPlayer)
				{
					row += rowDir;
					col += colDir;
					count++;
				}
				// If the next piece is not an opposite piece but its an empty
				// piece,
				// the piece is a potential move
				if (row <= 8 && row > 0 && col <= 8 && col > 0
						&& board[row][col] == EMPTY)
				{
					// Mark the empty piece as the HINT
					board[row][col] = HINTS;
					noOfHint++;
					// Try to find the best move using strategy
					count = computerMove(count, row, col);
					if (count > mostFlip)
					{
						mostFlip = count;
						bestRow = row;
						bestCol = col;
					}

				}
				repaint();
			}
			pos++;
		}

	}

	/**
	 * Find the best move using strategy
	 * 
	 * @param count the number of pieces can be flip
	 * @param row the row of the hint
	 * @param col the column of the hint
	 * @return the value of the piece using strategy
	 */
	private int computerMove(int count, int row, int col)
	{
		int flipMost = count;

		if (row == 1 || col == 8 || row == 8 || col == 1)
		{
			// Give the bonus 5 points for 4 corners
			if (row == 1 && col == 1)
			{
				flipMost += 5;
			}
			if (row == 1 && col == 8)
			{
				flipMost += 5;
			}
			if (row == 8 && col == 1)
			{
				flipMost += 5;
			}
			if (row == 8 && col == 8)
			{
				flipMost += 5;
			}
			// Give the bonus 2 points for the pieces at the 4 slides
			else
			{
				flipMost += 2;
			}
		}
		return flipMost;
	}

	/**
	 * Flip all the opposite pieces between the move user made
	 * and the current pieces
	 * 
	 * @param currentRow the row of the move user made
	 * @param currentCol the column of the move user made
	 */
	private void flip(int currentRow, int currentCol)
	{
		int pos = 0;
		// Search in all 8 directions
		while (pos < rowStr.length)
		{
			int rowDir = rowStr[pos];
			int colDir = colStr[pos];
			int row = currentRow + rowDir;
			int col = currentCol + colDir;
			int count = 0;

			// The direction might has pieces to flip if the piece next to the
			// piece to check in the direction
			// is an opposite piece

			while (row <= 8 && row > 0 && col <= 8 && col > 0
					&& board[row][col] == -1 * currentPlayer)
			{
				row += rowDir;
				col += colDir;
				// The potential number of pieces can flip
				count++;
			}
			// If the next piece is not an opposite piece but its the current
			// piece,
			// flip all the opposite pieces between the current piece and the
			// move user made
			if (row <= 8 && row > 0 && col <= 8 && col > 0
					&& board[row][col] == currentPlayer)
			{
				row -= rowDir;
				col -= colDir;
				while (board[row][col] == -1 * currentPlayer && count >= 0)
				{
					board[row][col] = currentPlayer;
					flipped[row][col] = true;
					row -= rowDir;
					col -= colDir;
					count--;
				}

			}
			repaint();
			pos++;
		}

	}

	/**
	 * Check if the game is over
	 * 
	 * @return true if the game is over false if the game is not over
	 */
	private boolean checkGameOver()
	{
		// When one of the players doesn't have any pieces
		// The game is over
		if (noOfCat == 0 || noOfDog == 0)
		{
			return true;
		}
		// When the board is filled
		// The game is over
		if (noOfMove >= 64)
		{
			return true;
		}
		if (gameOver)
		{
			return true;
		}
		return false;
	}

	/**
	 * The messages to show to the player after the game is over
	 */
	private void over()
	{
		gameOver = true;
		repaint(0);
		if (noOfCat > noOfDog)
		{
			JOptionPane.showMessageDialog(this, "CAT Wins!!!", "GAME OVER",
					JOptionPane.INFORMATION_MESSAGE);
		}
		else if (noOfDog > noOfCat)
		{
			JOptionPane.showMessageDialog(this, "DOG Wins!!!", "GAME OVER",
					JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			JOptionPane.showMessageDialog(this, "Tie Game", "GAME OVER",
					JOptionPane.INFORMATION_MESSAGE);
		}
		repaint(0);
		if (JOptionPane.showConfirmDialog(this, "Do you want to Play Again?",
				"Game Over", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		{
			newGame();
		}
		else
		{
			hide();
			System.exit(0);
		}
		return;
	}

	/**
	 * Makes a move on the board (if possible)
	 * 
	 * @param row the row to move in
	 * @param selectedColumn the selected column to move in
	 */
	private void makeMove(int row, int selectedColumn)
	{
		// If the clicked area is a valid area, place the piece on the area
		if (isValidMove(row, selectedColumn) && checkGameOver() == false)
		{
			repaint();

			flip(row, selectedColumn);
			// The animation to flip the pieces
			if (ANIMATION_ON)
				animatePiece();
			repaint();
			// Change the piece user clicked to the piece of currentPlayer
			board[row][selectedColumn] = currentPlayer;
			// Erase all the hints from the board and make the hints pieces back
			// to empty
			changeBack();
			if (checkGameOver())
			{
				repaint();
				over();
			}

			else
			// Switch to the other player
			{
				if (humanVsCom && checkGameOver() == false)
				{
					// call the AI
					computerPlayer();

				}
				if (checkGameOver() == false)
				{
					// Make sure that the human player is always dog
					if (humanVsCom && currentPlayer == DOG)
					{
						currentPlayer *= -1;
					}
					currentPlayer *= -1;
					repaint();
					hints();
					// The number of switches player had been made
					int noOfSwitch = 0;
					// Switch player when there is no more possible move for one
					// player
					if (noOfHint == 0)
					{
						while (noOfHint == 0 && checkGameOver() == false
								&& noOfMove < 64 && noOfMove > 0)
						{

							noOfSwitch++;
							if (humanVsCom && checkGameOver() == false)
							{
								computerPlayer();

							}
							currentPlayer *= -1;
							hints();
							// When the player had been switched for 5 times and
							// both players still has no possible move, the game
							// is over
							if (noOfSwitch > 5)
							{
								gameOver = true;
								checkGameOver();
							}
						}
					}
					noOfSwitch = 0;
					if (checkGameOver() && gameOver == false)
					{
						repaint();
						over();
					}
					noOfHint = 0;
				}
			}

			// Start piece in center
			currentColumn = NO_OF_COLUMNS / 2 + 1;
			repaint();
		} // Change the player to cat first

	}

	/**
	 * The computer player move
	 */
	private void computerPlayer()
	{
		currentPlayer *= -1;
		bestRow = 0;
		bestCol = 0;
		mostFlip = 0;
		// Find the best possible move for the computer player
		hints();
		int change = 0;
		// Switch player when there is no more possible move for one player
		if (noOfHint == 0 & checkGameOver() == false)
		{
			while (noOfHint == 0 && checkGameOver() == false)
			{
				change++;
				currentPlayer *= -1;
				hints();
				if (change > 5)
				{
					gameOver = true;
					checkGameOver();
				}
			}
		}
		change = 0;
		// Make sure that the computer player is always cat
		if (noOfHint > 0 && currentPlayer == CAT)
		{
			noOfHint = 0;
			board[bestRow][bestCol] = currentPlayer;
			// Make the value of all the hint pieces back to empty
			changeBack();
			// Flips the pieces
			flip(bestRow, bestCol);
			if (ANIMATION_ON)
				animatePiece();
			repaint();
			mostFlip = 0;
			repaint();
		}
		// Check if the game is over
		if (checkGameOver())
		{
			repaint();
			over();
		}
	}

	/**
	 * Make the value of all the hints on the board back to
	 * empty
	 */
	private void changeBack()
	{
		for (int row = 1; row <= NO_OF_ROWS; row++)
		{
			for (int column = 1; column <= NO_OF_COLUMNS; column++)
			{
				if (board[row][column] == HINTS)
				{
					board[row][column] = EMPTY;
				}
			}
		}
	}

	// }

	/**
	 * Animates flip pieces
	 */
	private void animatePiece()
	{

		// Flip the images in order (Cat to Dog)
		if (currentPlayer == CAT)
		{
			for (flipSequence = 0; flipSequence < flipImages.length; flipSequence++)
			{
				// Update the drawing area
				paintImmediately(0, 0, getWidth(), getHeight());

				delay(CHANGING_SPEED);
			}
		}
		// Flip the images in reverse order (Dog to Cat)
		if (currentPlayer == DOG)
		{
			for (flipSequence = flipImages.length - 1; flipSequence > 0; flipSequence--)
			{
				// Update the drawing area
				paintImmediately(0, 0, getWidth(), getHeight());

				delay(CHANGING_SPEED);
			}
		}

		// Change the value of all the pieces on the flipped board back to false
		for (int row = 1; row <= NO_OF_ROWS; row++)
		{
			for (int column = 1; column <= NO_OF_COLUMNS; column++)
			{

				flipped[row][column] = false;

			}
		}

	}

	/**
	 * Delays the given number of milliseconds
	 * 
	 * @param milliSec The number of milliseconds to delay
	 */
	private void delay(int milliSec)
	{
		try
		{
			Thread.sleep(milliSec);
		}

		catch (InterruptedException e)
		{
		}
	}

	/**
	 * Repaint the board's drawing panel
	 * 
	 * @param g The Graphics context
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Draws the starting page
		if (startingPage)
		{
			g.drawImage(background, 0, 0, this);
			// Draws the instruction on the board
			if (howToPlayPage)
				g.drawImage(howToPlay, 130, 100, this);
		}

		else if (humanVsHuman || humanVsCom)
		{
			g.setFont(new Font("TimesRoman", Font.BOLD, 50));
			g.drawString("Reversi", 30, 50);

			// Counts the number of dogs and cats on the board
			int c = 0;
			int d = 0;
			for (int i = 0; i < board.length; i++)
			{
				for (int ind = 0; ind < board[i].length; ind++)
				{
					if (board[i][ind] == DOG)
						d++;
					else if (board[i][ind] == CAT)
						c++;
				}
			}
			noOfCat = c;
			noOfDog = d;

			g.setFont(new Font("Bauhaus 93", Font.PLAIN, 20));
			g.drawString("number of cats: " + noOfCat, 580, 100);
			g.drawString("number of dogs: " + noOfDog, 580, 150);

			// Redraw the board with current pieces
			for (int row = 1; row <= NO_OF_ROWS; row++)
			{
				for (int column = 1; column <= NO_OF_COLUMNS; column++)
				{
					// Find the x and y positions for each row and column
					int xPos = (column - 1) * SQUARE_SIZE;
					int yPos = row * SQUARE_SIZE;

					// Draw the squares
					g.setColor(Color.PINK);
					g.drawRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);

					// Draw go to menu button
					g.drawRect(585, 530, 150, 50);
					g.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
					g.drawString("Double click for menu", 590, 550);

					// Draws each piece, depending on the value in board
					if (board[row][column] == CAT)
						g.drawImage(firstImage, xPos, yPos, this);
					else if (board[row][column] == DOG)
						g.drawImage(secondImage, xPos, yPos, this);
					// Draws hints
					else if (board[row][column] == HINTS)
					{
						g.fillOval(xPos + 4, yPos + 4, SQUARE_SIZE - 7,
								SQUARE_SIZE - 7);
					}
					// Draws image fliping
					if (flipped[row][column])
					{
						g.drawImage(flipImages[flipSequence], xPos, yPos, this);
					}
				}
			}

			// Set the current piece on the right
			if (!gameOver)
			{
				if (currentPlayer == CAT)
					g.drawImage(firstImage, 625, 200, this);
				else
					g.drawImage(secondImage, 625, 200, this);
			}

		}

	} // End of paint component method

	// Mouse events you can listen for since this JPanel is a MouseListener

	// *
	/**
	 * Monitors mouse movement over the game panel and responds
	 */
	private class MouseMotionHandler extends MouseMotionAdapter
	{
		/**
		 * Responds to mouse-movement inputs
		 * 
		 * @param event The event created by the mouse movement
		 */
		public void mouseMoved(MouseEvent event)
		{
			Point pos = event.getPoint();

			// At the title screen
			if (startingPage)
			{
				// If the mouse is on the menus, set the cursor to the hand
				if (!howToPlayPage)
				{
					if (pos.x >= 50 && pos.x < 365 && pos.y >= 370
							&& pos.y < 460 || pos.x >= 405 && pos.x < 720
							&& pos.y >= 370 && pos.y < 460 || pos.x >= 235
							&& pos.x < 550 && pos.y >= 505 && pos.y < 595)
						setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));
					// If not, set it to default cursor
					else
						setCursor(Cursor.getDefaultCursor());
				}

				if (howToPlayPage)
				{
					// If the mouse is on the Exit button on how to play page
					// set the cursor to the hand
					if (pos.x > 285 && pos.x < 445 && pos.y > 460
							&& pos.y < 500)
						setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));
					else
						// If not, set it to default cursor
						setCursor(Cursor.getDefaultCursor());
				}

			}
			else if (humanVsHuman || humanVsCom)
			{
				// Go to menu bar
				if (pos.x >= 585 && pos.x < 735 && pos.y >= 530 && pos.y < 580)
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

				// If not, set it to default cursor
				else
					setCursor(Cursor.getDefaultCursor());
			}
			repaint();
		}
	}

	/**
	 * Responds to a mousePressed event
	 * 
	 * @param eventinformation about the mouse pressed event
	 */
	public void mousePressed(MouseEvent event)
	{
		Point pos = event.getPoint();

		int selectedColumn = event.getX() / SQUARE_SIZE + 1;
		int selectedRow = event.getY() / SQUARE_SIZE;
		// Call makeMove method only if the user clicked on the one of the hints
		if (board[selectedRow][selectedColumn] == HINTS && !startingPage
				&& pos.x < 562)
		{
			makeMove(selectedRow, selectedColumn);
		}
		// The user clicked on the pieces that are not hints on the board
		else if (board[selectedRow][selectedColumn] != HINTS && !startingPage
				&& pos.x < 562)
		{
			JOptionPane.showMessageDialog(this, "The move is not a valid move",
					"Warning", JOptionPane.INFORMATION_MESSAGE);
		}

		if (startingPage)
		{
			if (!howToPlayPage)
			{
				// If the mouse is on the human verses human game button,
				// change it to human verses human game mode
				if (pos.x >= 50 && pos.x < 365 && pos.y >= 370 && pos.y < 460)
				{
					newGame();
					startingPage = false;
					humanVsHuman = true;
					setCursor(Cursor.getDefaultCursor());
				}
				// If the mouse is on the human verses computer game button,
				// change it to human verses computer game mode
				else if (pos.x >= 405 && pos.x < 720 && pos.y >= 370
						&& pos.y < 460)
				{
					newGame();
					startingPage = false;
					humanVsCom = true;
				}
			}
			if (pos.x >= 235 && pos.x < 550 && pos.y >= 505 && pos.y < 595)
			{
				howToPlayPage = true;
			}
			else if (pos.x > 285 && pos.x < 445 && pos.y > 460 && pos.y < 500
					&& howToPlayPage)
			{
				howToPlayPage = false;
			}
		}
	}

	// Extra methods needed since this game board is a MouseListener

	public void mouseReleased(MouseEvent event)
	{
	}

	public void mouseClicked(MouseEvent event)
	{
		Point pos = event.getPoint();
		// If the "double click for menu" button is being double clicked,
		// change the background to the menu page
		if (humanVsHuman || humanVsCom)
		{
			if (pos.x >= 585 && pos.x < 735 && pos.y >= 530 && pos.y < 580)
			{
				if (event.getClickCount() == 2)
				{
					humanVsHuman = false;
					humanVsCom = false;
					startingPage = true;
				}
			}
		}
	}

	public void mouseEntered(MouseEvent event)
	{
	}

	public void mouseExited(MouseEvent event)
	{
	}
}
