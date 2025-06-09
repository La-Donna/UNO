package GAMEPLAY;

/**
 * The main entry point for the UNO game application.
 * This class is responsible for displaying the initial welcome message
 * and then initiating the game by creating and running a `Run` instance.
 */
public class Main {
    public static void main(String[] args) {
        displayWelcomeMessage(); // Show the welcome message first
        Run unoGame = new Run(); // Create an instance of the game runner
        unoGame.setupGame();     // Set up the game
        unoGame.startGameLoop(); // Start the main game loop
    }

    private static void displayWelcomeMessage() {
        System.out.println("------------------------------------");
        System.out.println("         Welcome to UNO v1.0!         ");
        System.out.println("------------------------------------");
        System.out.println();
    }
}