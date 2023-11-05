package core;

import java.util.*;

/**
 * This class represents the computer player for the Checkers game.
 * It is responsible for generating a move for the computer player.
 */
public class CheckersComputerPlayer {

    private CheckersLogic gameLogic;
    private Random random;

    /**
     * Constructor for CheckersComputerPlayer.
     * 
     * @param gameLogic The game logic instance.
     */
    public CheckersComputerPlayer(CheckersLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.random = new Random();
    }

    /**
     * Generates and returns a move for the computer player.
     *
     * @return A string representing the move, e.g., "3a-4b".
     */
    public String generateMove() {
        List<String> possibleCaptures = new ArrayList<>();
        List<String> possibleMoves = new ArrayList<>();
        CheckersLogic.Player[][] board = gameLogic.getBoard();

        // Scan for possible moves and captures
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                // Assuming computer is PLAYER_O
                if (board[i][j] == CheckersLogic.Player.PLAYER_O) { 
                    addValidMovesForPiece(possibleMoves, i, j);
                    addValidCapturesForPiece(possibleCaptures, i, j);
                }
            }
        }
        
        // Prioritize captures over normal moves
        if (!possibleCaptures.isEmpty()) 
        {
            // Select a random capture move
            String move = possibleCaptures.get(random.nextInt(possibleCaptures.size()));
            performMove(move); 
            return move;
        } 
        else if (!possibleMoves.isEmpty()) 
        {
            // Select a random normal move
            String move = possibleMoves.get(random.nextInt(possibleMoves.size()));
            performMove(move); 
            return move;
        }

        return "";
    }

    /**
     * Adds all valid moves for a single piece to the provided list.
     *
     * @param possibleMoves The list to add the moves to.
     * @param x The x-coordinate of the piece.
     * @param y The y-coordinate of the piece.
     */
    private void addValidMovesForPiece(List<String> possibleMoves, int x, int y) {
        // Define the possible directions for a move. We should consider that pieces can only move forward diagonally.
        int[][] directions = (gameLogic.getCurrentPlayer() == CheckersLogic.Player.PLAYER_O) ? new int[][]{{-1, -1}, {-1, 1}} : new int[][]{{1, -1}, {1, 1}};

        for (int[] dir : directions) 
        {
            int newX = x + dir[0];
            int newY = y + dir[1];

            // Check if the move is valid and if it's a capturing move, it should be prioritized.
            if (gameLogic.isValidMove(x, y, newX, newY)) 
            {
                char startCol = (char) ('a' + y);
                char endCol = (char) ('a' + newY);
                possibleMoves.add((x + 1) + "" + startCol + "-" + (newX + 1) + endCol);
            }
        }
    }

    /**
     * Adds all valid capturing moves for a single piece to the provided list.
     *
     * @param possibleCaptures The list to add the moves to.
     * @param x The x-coordinate of the piece.
     * @param y The y-coordinate of the piece.
     */
    private void addValidCapturesForPiece(List<String> possibleCaptures, int x, int y) {
        // Define the possible directions for a capturing move.
        int[][] directions = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};

        for (int[] dir : directions) 
        {
            int newX = x + dir[0];
            int newY = y + dir[1];

            // Check if the move is a valid capture
            if (gameLogic.isValidJump(x, y, newX, newY)) 
            {
                char startCol = (char) ('a' + y);
                char endCol = (char) ('a' + newY);
                possibleCaptures.add((x + 1) + "" + startCol + "-" + (newX + 1) + endCol);
            }
        }
    }

    /**
     * Parses the move string and executes the move. 
     * 
     * @param move A string representing the move, e.g., "3a-4b".
     */
    private void performMove(String move) {
        String[] parts = move.split("-");
        int startX = parts[0].charAt(0) - '1';
        int startY = parts[0].charAt(1) - 'a';
        int endX = parts[1].charAt(0) - '1';
        int endY = parts[1].charAt(1) - 'a';
        
        gameLogic.movePiece(startX, startY, endX, endY);
    }
}