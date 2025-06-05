package CARDS;

import java.util.Scanner;

/**
 * Represents action cards (non-number cards) in UNO
 * Handles special functions for SKIP, REVERSE, DRAW_TWO, WILD, and WILD_DRAW_FOUR cards
 */
public class Action_Cards extends Card {

    public Action_Cards(Color color, Type type) {
        super(color, type);
        // Ensure this is actually an action card
        if (type == Type.NUMBER) {
            throw new IllegalArgumentException("Action_Cards cannot be of type NUMBER");
        }
    }

    /**
     * Executes the special function of this action card
     * @param playerCount Number of players in the game (needed for REVERSE logic)
     * @param scanner Scanner for user input (needed for wild card color choice)
     * @return ActionResult containing the effects of playing this card
     */
    public ActionResult executeSpecialFunction(int playerCount, Scanner scanner) {
        switch (this.getType()) {
            case SKIP:
                return executeSkip();

            case REVERSE:
                return executeReverse(playerCount);

            case DRAW_TWO:
                return executeDrawTwo();

            case WILD:
                return executeWild(scanner);

            case WILD_DRAW_FOUR:
                return executeWildDrawFour(scanner);

            default:
                return new ActionResult(); // No special effects
        }
    }

    /**
     * SKIP card: Next player is skipped
     */
    private ActionResult executeSkip() {
        System.out.println("SKIP card played! Next player is skipped.");
        ActionResult result = new ActionResult();
        result.skipNextPlayer = true;
        return result;
    }

    /**
     * REVERSE card: Changes direction of play
     */
    private ActionResult executeReverse(int playerCount) {
        System.out.println("REVERSE card played! Direction of play changed.");
        ActionResult result = new ActionResult();
        result.reverseDirection = true;

        // Special case: In a 2-player game, REVERSE acts like SKIP
        if (playerCount == 2) {
            System.out.println("In 2-player game, REVERSE acts as SKIP!");
            result.skipNextPlayer = true;
        }

        return result;
    }

    /**
     * DRAW TWO card: Next player draws 2 cards and loses their turn
     */
    private ActionResult executeDrawTwo() {
        System.out.println("DRAW TWO card played! Next player draws 2 cards and loses their turn.");
        ActionResult result = new ActionResult();
        result.cardsToDrawByNextPlayer = 2;
        result.skipNextPlayer = true;
        return result;
    }

    /**
     * WILD card: Player chooses the next color
     */
    private ActionResult executeWild(Scanner scanner) {
        System.out.println("WILD card played! Choose the next color:");
        ActionResult result = new ActionResult();
        result.newColor = chooseColor(scanner);
        return result;
    }

    /**
     * WILD DRAW FOUR card: Player chooses color, next player draws 4 cards
     */
    private ActionResult executeWildDrawFour(Scanner scanner) {
        System.out.println("WILD DRAW FOUR card played!");
        ActionResult result = new ActionResult();
        result.cardsToDrawByNextPlayer = 4;
        result.skipNextPlayer = true;

        // The Player who played the card chooses the color
        System.out.println("Choose the next color:");
        result.newColor = chooseColor(scanner);

        return result;
    }

    /**
     * Helper method for color selection
     */
    private Color chooseColor(Scanner scanner) {
        System.out.println("1. RED");
        System.out.println("2. BLUE");
        System.out.println("3. GREEN");
        System.out.println("4. YELLOW");
        System.out.print("Enter your choice (1-4): ");

        int choice;
        do {
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= 4) {
                    break;
                }
            } catch (NumberFormatException e) {
                // Invalid input, continue loop
            }
            System.out.print("Invalid choice. Please enter 1-4: ");
        } while (true);

        Color selectedColor;
        switch (choice) {
            case 1: selectedColor = Color.RED; break;
            case 2: selectedColor = Color.BLUE; break;
            case 3: selectedColor = Color.GREEN; break;
            case 4: selectedColor = Color.YELLOW; break;
            default: selectedColor = Color.RED; // Should never happen
        }

        System.out.println("Color changed to " + selectedColor);
        return selectedColor;
    }

    /**
     * Simple class to hold the results of playing an action card
     */
    public static class ActionResult {
        public boolean skipNextPlayer = false;
        public boolean reverseDirection = false;
        public int cardsToDrawByNextPlayer = 0;
        public Color newColor = null;
//Constructor
        public ActionResult() {}

        @Override
        public String toString() {
            return "ActionResult{" +
                    "skipNextPlayer=" + skipNextPlayer +
                    ", reverseDirection=" + reverseDirection +
                    ", cardsToDrawByNextPlayer=" + cardsToDrawByNextPlayer +
                    ", newColor=" + newColor +
                    '}';
        }
    }
}