package core;

import java.util.Arrays;

/**
 * This class contains the core logic behind simple checkers.
 */
public class CheckersLogic {

    /**
     * Enum representing the possible states of a board cell: it can be occupied by player X's piece, player O's piece, or be empty.
     */
    public enum Player { 
        EMPTY, 
        PLAYER_X, 
        PLAYER_O 
    }

    private Player[][] board;
    private Player currentPlayer;
    private boolean gameEnded;

    /**
     * Initializes a new game of checkers, setting up the board and the starting player.
     */
    public CheckersLogic() {
        board = new Player[8][8];
        setupBoard();
        currentPlayer = Player.PLAYER_X;
        gameEnded = false;
    }

    /**
     * Sets up the board with pieces in their initial positions.
     * It's called once when the game is initialized.
     */
    private void setupBoard() {
        // Sets-up empty spaces on the board
        for (Player[] row : board) 
        {
            Arrays.fill(row, Player.EMPTY);
        }

        // Sets-up initial pieces for both players
        for (int i = 0; i < 3; i++) 
        {
            for (int j = 0; j < 8; j++) 
            {
                if ((i + j) % 2 != 0) 
                {
                    board[i][j] = Player.PLAYER_X;
                }
            }
        }

        // Sets-up initial pieces for both players
        for (int i = 5; i < 8; i++) 
        {
            for (int j = 0; j < 8; j++) 
            {
                if ((i + j) % 2 != 0) 
                {
                    board[i][j] = Player.PLAYER_O;
                }
            }
        }
    }

    /**
     * Attempts to move a piece from one position to another.
     * This method checks if the move is legal and updates the board accordingly.
     *
     * @param startX the starting X coordinate of the piece to move
     * @param startY the starting Y coordinate of the piece to move
     * @param endX the ending X coordinate of the piece to move
     * @param endY the ending Y coordinate of the piece to move
     * @return true if the piece was moved successfully, false otherwise
     */
    public boolean movePiece(int startX, int startY, int endX, int endY) {
        boolean isMoveForward;
        // Check if the move is forward or backward
        if (currentPlayer == Player.PLAYER_X) 
        {
            isMoveForward = endX > startX;
        } 
        else 
        {
            isMoveForward = endX < startX;
        }

        // Check if the move is valid
        if (!isMoveForward) 
        {
            return false;
        }
        if (startX < 0 || startX >= 8 || startY < 0 || startY >= 8 || endX < 0 || endX >= 8 || endY < 0 || endY >= 8) 
        {
            return false;
        }

        if (board[startX][startY] != currentPlayer) 
        {
            return false;
        }

        int xDifference = endX - startX;
        int yDifference = endY - startY;

        if (Math.abs(xDifference) != Math.abs(yDifference)) 
        {
            return false;
        }

        boolean singleMove = Math.abs(xDifference) == 1;
        boolean captureMove = Math.abs(xDifference) == 2;

        if (board[endX][endY] != Player.EMPTY) 
        {
            return false;
        }

        if (captureMove) {
            int jumpedX = startX + xDifference / 2;
            int jumpedY = startY + yDifference / 2;
            if (board[jumpedX][jumpedY] != (currentPlayer == Player.PLAYER_X ? Player.PLAYER_O : Player.PLAYER_X)) {
                return false;
            } else {
                board[jumpedX][jumpedY] = Player.EMPTY;
            }
        } else if (!singleMove) {
            return false;
        }
        
        board[endX][endY] = currentPlayer;
        board[startX][startY] = Player.EMPTY;
    
        // Only switch players if no further capture moves are possible.
        if (!captureMove || !canCaptureAgain(endX, endY)) {
            currentPlayer = (currentPlayer == Player.PLAYER_X) ? Player.PLAYER_O : Player.PLAYER_X;
        }
    
        // Check for the end of the game every time a move is made.
        checkGameEnd();
    
        return true; // The move was successful.
    }

    /**
     * Checks if a piece can perform a capturing move again. It's used after a piece makes a capture to see if it can continue capturing.
     *
     * @param x the X coordinate of the piece
     * @param y the Y coordinate of the piece
     * @return true if the piece can capture again, false otherwise.
     */
    private boolean canCaptureAgain(int x, int y) {
        Player opponent = (currentPlayer == Player.PLAYER_X) ? Player.PLAYER_O : Player.PLAYER_X;
        int forwardDirection = (currentPlayer == Player.PLAYER_X) ? 1 : -1;

        // Define the increments to check each diagonal direction
        int[][] directions = {
            {2 * forwardDirection, 2}, {2 * forwardDirection, -2}
        };
    
        // Check each direction
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            int midX = x + dir[0] / 2; 
            int midY = y + dir[1] / 2; 
    
            // Ensure the 'mid' coordinates are for the square between the current position and the destination.
            if (isValidPosition(newX, newY) && 
                board[midX][midY] == opponent && 
                board[newX][newY] == Player.EMPTY) {
                return true;
            }
        }
    
        return false; // No valid captures found
    }    

    /**
     * Checks if the given position is within the bounds of the board.
     *
     * @param row the row number to check
     * @param col the column number to check
     * @return true if the position is within the bounds of the board, false otherwise.
     */
    public boolean isValidPosition(int row, int col) {
        // Check if the position is within the bounds of the board
        return row >= 0 && row < board.length && col >= 0 && col < board[row].length;
    }

    /**
     * Checks if a move is valid based on the game's rules. It checks the bounds, ensures the destination is empty, and the move is diagonal.
     *
     * @param startX the starting X coordinate of the piece
     * @param startY the starting Y coordinate of the piece
     * @param endX the ending X coordinate of the piece
     * @param endY the ending Y coordinate of the piece
     * @return true if the move is valid, false otherwise.
     */
    public boolean isValidMove(int startX, int startY, int endX, int endY) {
        // Check bounds and make sure the end position is empty and the move is diagonal
        return endX >= 0 && endX < 8 && endY >= 0 && endY < 8 &&
               board[endX][endY] == Player.EMPTY &&
               Math.abs(startX - endX) == 1 && Math.abs(startY - endY) == 1;
    }

    /**
     * Checks if the game has reached an end condition. Specifically, it checks if either player has no remaining pieces or no possible moves.
     * It updates the 'gameEnded' field accordingly.
     */
    public void checkGameEnd() {
        int playerXPieces = 0;
        int playerOPieces = 0;
    
        // Count the pieces for each player
        for (Player[] row : board) {
            for (Player player : row) {
                if (player == Player.PLAYER_X) {
                    playerXPieces++;
                } else if (player == Player.PLAYER_O) {
                    playerOPieces++;
                }
            }
        }
    
        // Check win conditions based on the number of pieces each player has
        if (playerXPieces == 0 || (currentPlayer == Player.PLAYER_X && !canAnyPieceMove(Player.PLAYER_X))) {
            gameEnded = true;
            // Handle Player O winning
        } else if (playerOPieces == 0 || (currentPlayer == Player.PLAYER_O && !canAnyPieceMove(Player.PLAYER_O))) {
            gameEnded = true;
            // Handle Player X winning
        }
    
        // If game hasn't ended, the currentPlayer will naturally switch outside this method.
    }

    /**
     * Determines if any piece belonging to the specified player can make a valid move.
     *
     * @param player the player to check for possible moves
     * @return true if at least one valid move exists for the player, false otherwise.
     */
    private boolean canAnyPieceMove(Player player) {
        // Check every square on the board for a piece of the current player
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == player) {
                    int forwardDirection = (player == Player.PLAYER_X) ? 1 : -1; 
    
                    // Check all possible diagonal moves
                    if (isValidMove(i, j, i + forwardDirection, j - 1) || isValidMove(i, j, i + forwardDirection, j + 1)) {
                        return true;
                    }
                    if (isValidJump(i, j, i + 2 * forwardDirection, j - 2) || isValidJump(i, j, i + 2 * forwardDirection, j + 2)) {
                        return true;
                    }
    
                }
            }
        }
        return false; 
    }

    /**
     * Retrieves the current state of the game board. Each cell on the board is represented by a Player enum,
     * indicating whether it's occupied by player X's piece, player O's piece, or empty.
     *
     * @return A 2D array representing the current state of the game board.
     */
    public Player[][] getBoard() {
        // Return a copy of the board to prevent the original from being modified
        return Arrays.stream(board).map(Player[]::clone).toArray(Player[][]::new);
    }

    /**
     * Checks if the game has ended.
     *
     * @return true if the game has ended, false otherwise.
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * Checks if a jump is valid. A jump is considered valid if it's over an opponent's piece and lands in an empty space.
     *
     * @param startX the starting X coordinate of the piece
     * @param startY the starting Y coordinate of the piece
     * @param endX the ending X coordinate of the piece
     * @param endY the ending Y coordinate of the piece
     * @return true if the jump is valid, false otherwise.
     */
    public boolean isValidJump(int startX, int startY, int endX, int endY) {
        // Check bounds and make sure the end position is empty
        if (endX >= 0 && endX < 8 && endY >= 0 && endY < 8 && board[endX][endY] == Player.EMPTY) {
            // Calculate the position of the jumped piece
            int jumpedX = (startX + endX) / 2;
            int jumpedY = (startY + endY) / 2;
            // Check if there is an opponent piece in the jumped position
            if (board[jumpedX][jumpedY] != Player.EMPTY && board[jumpedX][jumpedY] != board[startX][startY]) {
                return true; // Valid jump
            }
        }
        return false; // Not a valid jump
    }

    /**
     * Retrieves the current player.
     *
     * @return the current player.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

}