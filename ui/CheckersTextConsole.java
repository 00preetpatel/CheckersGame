package ui;

import core.CheckersComputerPlayer;
import core.CheckersLogic;

import java.util.Scanner;

/**
 * CheckersTextConsole class represents the user interface for the Checkers game, 
 * managing all user interactions on the console.
 */
public class CheckersTextConsole {

    private CheckersLogic gameLogic;
    private Scanner scanner;
    private boolean playAgainstComputer;
    

    /**
     * Constructor for CheckersTextConsole.
     * Initializes the game logic and scanner instances.
     * 
     * @param playAgainstComputer True if the user chooses to play against the computer; false otherwise.
     */
    public CheckersTextConsole(boolean playAgainstComputer) {
        this.playAgainstComputer = playAgainstComputer;
        this.gameLogic = new CheckersLogic();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the game, and handles the game loop and user interactions.
     */
    public void startGame() {
        System.out.println("Welcome to Checkers!");
        if (playAgainstComputer) {
            System.out.println("You(Player X) are playing against the computer(Player 0).");
        } else {
            System.out.println("You are playing against another player.");
        }

        // Initialize the computer player
        CheckersComputerPlayer computerPlayer = null;
        if (playAgainstComputer) {
            computerPlayer = new CheckersComputerPlayer(gameLogic);
        }

        printBoard();

        // Game loop
        while (!gameLogic.isGameEnded()) 
        { 
            String currentPlayer = gameLogic.getCurrentPlayer() == CheckersLogic.Player.PLAYER_X ? "X" : "O"; 
            System.out.println("Player " + currentPlayer + "'s turn");

            System.out.println("Enter your move (e.g., '3a-4b'):");
            String move = scanner.nextLine();

            // Input validation and parsing
            if (!move.matches("[1-8][a-h]-[1-8][a-h]")) {
                System.out.println("Invalid input. Please use the format: '3a-4b'.");
                continue;
            }
            
            // Convert the input to coordinates
            String[] parts = move.split("-");
            int startX = Character.getNumericValue(parts[0].charAt(0)) - 1;
            int startY = parts[0].charAt(1) - 'a';
            int endX = Character.getNumericValue(parts[1].charAt(0)) - 1;
            int endY = parts[1].charAt(1) - 'a';
            
            // Execute the move
            if (gameLogic.movePiece(startX, startY, endX, endY)) 
            {
                System.out.println("Move successful!");
            } 
            else 
            {
                System.out.println("Invalid move. Try again.");
                continue; // Skip the rest of the loop to prompt the player again
            }
            
            if (playAgainstComputer && gameLogic.getCurrentPlayer() == CheckersLogic.Player.PLAYER_O) {
                String computerMove = computerPlayer.generateMove();
            
                // Print out the move to show what the computer did
                System.out.println("Computer's move: " + computerMove);
            
                // Parse the computer's move
                String[] compMoveParts = computerMove.split("-");
                int compStartX = Character.getNumericValue(compMoveParts[0].charAt(0)) - 1;
                int compStartY = compMoveParts[0].charAt(1) - 'a';
                int compEndX = Character.getNumericValue(compMoveParts[1].charAt(0)) - 1;
                int compEndY = compMoveParts[1].charAt(1) - 'a';
            
                // Execute the computer's move
                boolean moveSuccessful = gameLogic.movePiece(compStartX, compStartY, compEndX, compEndY);
                
                // Print out the computer's move to show what the computer did
                if (moveSuccessful) {
                    System.out.println("Computer moved from " + compMoveParts[0] + " to " + compMoveParts[1]);
                } 
                else 
                {
                    System.out.println("No more forward moves. Computer's turn is over.");
                }
            }

            printBoard();
        }
        // Announce the winner
            String winner = gameLogic.getCurrentPlayer() == CheckersLogic.Player.PLAYER_X ? "O" : "X"; 
            System.out.println("Game over! Player " + winner + " wins!");
    }

    /**
     * Prints the current state of the board to the console.
     */
    private void printBoard() { 
        CheckersLogic.Player[][] board = gameLogic.getBoard(); 

        // Print the top coordinate legend
        System.out.print("  ");
        for (char c = 'a'; c <= 'h'; c++) 
        {
            System.out.print(" " + c);
        }
        System.out.println();
        
        // Print the top boundary of the board
        for (int i = 0; i < board.length; i++) {
            // Print the side coordinate legend
            System.out.print((i + 1) + " ");
            
            // Print the content of each cell
            for (int j = 0; j < board[i].length; j++) {
                char symbol = ' '; // Default symbol for empty cells
                if (board[i][j] == CheckersLogic.Player.PLAYER_X) {
                    symbol = 'X';
                } else if (board[i][j] == CheckersLogic.Player.PLAYER_O) {
                    symbol = 'O';
                }
    
                System.out.print("|" + (symbol == ' ' ? '_' : symbol)); // Print cell content, using underscore for empty cells
            }
            System.out.println("|"); // New line and right boundary of the board
        }
    }
}
