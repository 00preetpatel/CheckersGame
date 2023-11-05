import java.util.Scanner;
import ui.CheckersGUI;
import ui.CheckersTextConsole;

/**
 * This is the main class for the Checkers game.
 * It is responsible for starting the game, choosing the UI, and competitor.
 * 
 * @author Preet Patel
 */
public class RunGame {
    
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Starts the game, and handles the game loop and user interactions.
     * 
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        String uiType = chooseUI();
        boolean playAgainstComputer = chooseOpponent();

        if ("GUI".equalsIgnoreCase(uiType)) {
            CheckersGUI.launch(CheckersGUI.class, args);
        } else {
            CheckersTextConsole consoleGame = new CheckersTextConsole(playAgainstComputer);
            consoleGame.startGame(); // Start the console game
        }
    }
    
    /**
     * Prompts the user to choose a UI type.
     * 
     * @return The user's choice of UI type
     */
    private static String chooseUI() {
        String uiType;
        do 
        {
            System.out.println("Choose UI: 'GUI' for graphical interface or 'Console' for text-based interface.");
            uiType = scanner.nextLine().trim();
            if (!"GUI".equalsIgnoreCase(uiType) && !"Console".equalsIgnoreCase(uiType)) 
            {
                System.out.println("Invalid UI type. Please enter 'GUI' or 'Console'.");
            }
        } 
        while (!"GUI".equalsIgnoreCase(uiType) && !"Console".equalsIgnoreCase(uiType));
        return uiType;
    }

    /**
     * Prompts the user to choose an opponent.
     * 
     * @return True if the user chooses to play against the computer; false otherwise
     */
    private static boolean chooseOpponent() {
        System.out.println("Enter 'P' if you want to play against another player; enter 'C' to play against computer.");
        String gameMode;
        do {
            gameMode = scanner.nextLine().trim().toUpperCase();
            if (!gameMode.equals("P") && !gameMode.equals("C")) 
            {
                System.out.println("Invalid selection. Please enter 'P' or 'C'.");
            }
        } 
        while (!gameMode.equals("P") && !gameMode.equals("C"));
        return gameMode.equals("C");
    }
}
