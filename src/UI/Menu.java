package UI;

import CARDS.Card; // Required for displaying card information in the hand
import PLAYERS.Player; // Required for showing the player's hand

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * The Menu class handles all user interface interactions for the UNO game.
 * It's responsible for displaying prompts, messages, and receiving user input.
 */
public class Menu {
    private Scanner scanner;

    /**
     * Constructor for the Menu class.
     * @param scanner The Scanner object used for reading user input.
     */
    public Menu(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Prompts the user to press Enter to continue.
     */
    public void pressEnterToContinue() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine(); // Consumes the leftover newline character if any, then waits for user input
    }

    /**
     * Gets the number of human players for the game.
     * Ensures the input is within the valid range (1 to maxPlayers).
     * @param maxPlayers The maximum number of players allowed in the game.
     * @return The number of human players chosen by the user.
     */
    public int getGameSetupInput(int maxPlayers) {
        int numHumanPlayers = -1;
        while (numHumanPlayers < 0 || numHumanPlayers > maxPlayers) {
            System.out.print("Enter the number of human players (0-" + maxPlayers + "): ");
            try {
                numHumanPlayers = Integer.parseInt(scanner.nextLine());
                if (numHumanPlayers < 0 || numHumanPlayers > maxPlayers) {
                    System.out.println("Invalid number. Please enter a number between 0 and " + maxPlayers + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return numHumanPlayers;
    }

    /**
     * Prompts the user to enter a player's name.
     * @param prompt The message to display to the user.
     * @return The entered player name.
     */
    public String getPlayerNameInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim(); // Read the name and trim whitespace
    }

    /**
     * Displays a player's hand and prompts them to choose a card to play or to draw a card.
     * @param hand The list of cards in the player's hand.
     * @return The 1-based index of the chosen card, or 0 if the player wants to draw.
     */
    public int getCardChoice(List<Card> hand) {
        System.out.println("\nYour Hand:");
        if (hand.isEmpty()) {
            System.out.println("Your hand is empty! (This shouldn't happen unless you've won the round ‼️)");
            return -1; // Indicate an issue or that player has no cards
        }
        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i + 1) + ". " + hand.get(i));
        }
        System.out.println("0. Draw a card");

        int choice = -1;
        while (choice < 0 || choice > hand.size()) {
            System.out.print("Enter the number of the card you want to play, or 0 to draw: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < 0 || choice > hand.size()) {
                    System.out.println("Invalid choice. Please enter a number between 0 and " + hand.size() + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return choice;
    }

    /**
     * Prompts the user to choose a color (for Wild or Wild Draw Four cards).
     * @return The chosen Card.Color.
     */
    public Card.Color promptForColorChoice() {
        Card.Color chosenColor = null;
        System.out.println("Choose a color:");
        System.out.println("1. RED 🔴");
        System.out.println("2. YELLOW 🟡");
        System.out.println("3. GREEN 🟢");
        System.out.println("4. BLUE 🔵");

        while (chosenColor == null) {
            System.out.print("Enter the number for your chosen color: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1: chosenColor = Card.Color.RED; break;
                    case 2: chosenColor = Card.Color.YELLOW; break;
                    case 3: chosenColor = Card.Color.GREEN; break;
                    case 4: chosenColor = Card.Color.BLUE; break;
                    default: System.out.println("Invalid choice. Please enter a number between 1 and 4."); break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return chosenColor;
    }

    /**
     * Gets a yes/no input from the user.
     * @param prompt The question to ask the user.
     * @return true if the user enters 'y' or 'Y', false if 'n' or 'N'.
     */
    public boolean getYesNoInput(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt + " (y/n): ");
            input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                return true;
            } else if (input.equals("n")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'y' for Yes or 'n' for No.");
            }
        }
    }

    // Method to display a generic message
    public void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * Reads a line of text input from the console.
     * This provides a public way to get string input without exposing the internal Scanner.
     * @return The line of text entered by the user.
     */
    public String readLine() {
        return scanner.nextLine();
    }



    /**
     * Shows Menu and creates a loop until a valid choice has been
     * made by human player
     */
    public int getPlayerActionChoice(List<Card> hand, String playerName) {
        // Loop until a valid choice is made (card to play, draw, or special action)
        while (true) {
            System.out.println("\n--- " + playerName + "'s Options ---");
            System.out.println("Your Hand:");
            for (int i = 0; i < hand.size(); i++) {
                System.out.println((i + 1) + ". " + hand.get(i).toString());
            }
            System.out.println("--------------------------");
            System.out.println("0. Draw a card");
            System.out.println("S. Say 'UNO!' (if you have one card left)"); // Example: 'S' for Say
            System.out.println("H. Help (show rules/commands)"); // Example: 'H' for Help
            // ... potentially other options like "view discard pile" or "view scores"
            System.out.print("Enter your choice (number, S, H): ");

            String input = scanner.nextLine().trim().toUpperCase(); // Read as String, convert to uppercase

            // Try to parse as a number (card choice or draw)
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 0 && choice <= hand.size()) {
                    return choice; // Valid card choice or draw
                } else {
                    System.out.println("Invalid card number. Please try again.");
                }
            } catch (NumberFormatException e) {
                // Not a number, check for special commands
                switch (input) {
                    case "S":
                        return -2; // Special code for "Say UNO!"
                    case "H":
                        return -3; // Special code for "Help"
                    default:
                        System.out.println("Invalid input. Please enter a card number, 'S' for UNO, or 'H' for Help.");
                }
            }
        }
    }

}
