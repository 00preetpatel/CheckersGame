package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.util.Optional;

import core.CheckersComputerPlayer;
import core.CheckersLogic;

/**
 * This class provides a graphical user interface for the Checkers game using JavaFX.
 */
public class CheckersGUI extends Application {
    
    private CheckersComputerPlayer computerPlayer;
    private CheckersLogic gameLogic; 
    private GridPane boardGrid; 
    private int selectedX = -1;
    private int selectedY = -1;


    /**
     * Starts the JavaFX application.
     * 
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            gameLogic = new CheckersLogic(); // Initialize game logic
            boardGrid = new GridPane(); // Create the grid for the board
            computerPlayer = new CheckersComputerPlayer(gameLogic); // Initialize computer player
            
            initializeBoard(); // Initialize the game board

            Scene scene = new Scene(boardGrid, 800, 800); // Create a scene with the board grid
            primaryStage.setTitle("Checkers Game");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }

    /**
     * Initializes the game board with pieces.
     */
    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pane square = new Pane();
                square.setPrefSize(100, 100);
                // Change color to light gray for visibility of black pieces
                square.setStyle("-fx-background-color: " + ((i + j) % 2 == 0 ? "white" : "#d3d3d3") + ";");
                boardGrid.add(square, j, i);
    
                square.setOnMouseClicked(event -> handleSquareClick(GridPane.getRowIndex(square), GridPane.getColumnIndex(square)));
            }
        }
        updateUI();
    }


    /**
     * Updates the UI to reflect the current state of the game board.
     */
    private void updateUI() {
        CheckersLogic.Player[][] board = gameLogic.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Pane square = (Pane) getNodeFromGridPane(boardGrid, j, i);
                square.getChildren().clear(); // Clear any existing pieces
                if (board[i][j] != CheckersLogic.Player.EMPTY) {
                    Circle piece = new Circle(40); // Assuming a square size of 100x100 pixels
                    piece.setFill(board[i][j] == CheckersLogic.Player.PLAYER_X ? Color.RED : Color.BLACK);
                    
                    // Bind the center of the circle to the center of the pane
                    piece.centerXProperty().bind(square.widthProperty().divide(2));
                    piece.centerYProperty().bind(square.heightProperty().divide(2));
                    
                    square.getChildren().add(piece);
                }
            }
        }
    }

    /**
     * Returns the node at the specified position in the grid pane.
     * 
     * @param gridPane the grid pane
     * @param col the column index
     * @param row the row index
     * @return the node at the specified position
     */
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {

        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    /**
     * Handles user input when a square on the board is clicked.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     */
    private void handleSquareClick(int x, int y) {
        if (selectedX == -1 && selectedY == -1) {
            // Select the piece
            if (gameLogic.getBoard()[x][y] == gameLogic.getCurrentPlayer()) {
                selectedX = x;
                selectedY = y;
                highlightSquare(x, y, true);
            }
        } else {
            // Attempt to move the piece
            if (gameLogic.movePiece(selectedX, selectedY, x, y)) {
                highlightSquare(selectedX, selectedY, false);
                selectedX = -1;
                selectedY = -1;
                updateUI();
                gameLogic.checkGameEnd(); // Check if the player's move ended the game
                if (gameLogic.isGameEnded()) {
                    endGame(); // Handle the end of the game
                } else {
                    makeComputerMove(); // If the game is not ended, continue with the computer's move
                }
            } else {
                highlightSquare(selectedX, selectedY, false);
                selectedX = -1;
                selectedY = -1;
                // Optionally, add a message to the user about an invalid move
            }
        }
    }

    /**
     * Highlights a square on the board.
     * 
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param highlight true if the square should be highlighted; false otherwise
     */
    private void highlightSquare(int x, int y, boolean highlight) {
        Pane square = (Pane) getNodeFromGridPane(boardGrid, y, x);
        if (highlight) {
            square.setStyle("-fx-border-color: blue; -fx-border-width: 3;");
        } else {
            // Revert to the original color based on square position
            String color = (x + y) % 2 == 0 ? "white" : "#d3d3d3";
            square.setStyle("-fx-background-color: " + color + "; -fx-border-color: transparent;");
        }
    }

    /**
     * Makes the computer player move.
     */
    private void makeComputerMove() {
        String move = computerPlayer.generateMove();
        if (!move.equals("")) {
            updateUI();
            gameLogic.checkGameEnd(); // Check if the computer's move ended the game
            if (gameLogic.isGameEnded()) {
                endGame(); // Handle the end of the game
            }
        }
    }

    /**
     * Handles the end of the game.
     * Displays an alert with the winner and closes the program.
     */
    private void endGame() {
        // Determine the winner
        String winner = gameLogic.getCurrentPlayer() == CheckersLogic.Player.PLAYER_X ? "Player O" : "Player X";
        
        // Create an alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null); // No header
        alert.setContentText(winner + " wins the game!");

        // Add a custom close button
        ButtonType closeButton = new ButtonType("Close");
        alert.getButtonTypes().setAll(closeButton);

        // Show the alert and wait for a response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == closeButton) 
        {
            // Close the program
            System.exit(0);
        }
    }


    /**
     * Launches the JavaFX application.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
