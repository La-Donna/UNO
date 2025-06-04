package CARDS;

import PLAYERS.Player;

import java.util.Scanner;

public class Action_Cards extends Card{
    public Action_Cards(Color color, Type type) {
        super(color, type);
    }
    /**
     * Executes the special function of this action card
     * @param run The current game instance
     * @param scanner Scanner for user input (needed for color choice)
     * @return The new color if this is a wild card, null otherwise
     */
    public Color executeSpecialFunction(Run run, Scanner scanner) {
        switch (this.getType()) {
            case SKIP:
                return executeSkip(run);

            case REVERSE:
                return executeReverse(run);

            case DRAW_TWO:
                return executeDrawTwo(run);

            case WILD:
                return executeWild(scanner);

            case WILD_DRAW_FOUR:
                return executeWildDrawFour(run, scanner);

            default:
                return null; // NUMBER cards have no special function
        }
    }

    /**
     * SKIP card: Next player is skipped
     */
    private Color executeSkip(Run run) {
        System.out.println("SKIP card played! Next player is skipped.");
        // Skip the next player by calling nextPlayer() twice
        run.nextPlayer();
        return null;
    }

    /**
     * REVERSE card: Changes direction of play
     */
    private Color executeReverse(Run run) {
        System.out.println("REVERSE card played! Direction of play changed.");
        run.reverseDirection();
        return null;
    }

    /**
     * DRAW TWO card: Next player draws 2 cards and loses their turn
     */
    private Color executeDrawTwo(Run run) {
        System.out.println("DRAW TWO card played! Next player draws 2 cards and loses their turn.");
        run.nextPlayer();
        Player nextPlayer = run.getCurrentPlayer();

        // Draw 2 cards
        for (int i = 0; i < 2; i++) {
            Card drawnCard = run.getDeck().drawCard();
            if (drawnCard != null) {
                nextPlayer.drawCard(drawnCard);
            }
        }

        System.out.println(nextPlayer.getName() + " drew 2 cards and loses their turn.");
        return null;
    }

    /**
     * WILD card: Player chooses the next color
     */
    private Color executeWild(Scanner scanner) {
        System.out.println("WILD card played! Choose the next color:");
        return chooseColor(scanner);
    }

    /**
     * WILD DRAW FOUR card: Player chooses color, next player draws 4 cards
     * Note: In real UNO, this can only be played if player has no matching color
     */
    private Color executeWildDrawFour (Run run, Scanner scanner) {
        System.out.println("WILD DRAW FOUR card played!");

        // Next player draws 4 cards and loses turn
        run.nextPlayer();
        Player nextPlayer = run.getCurrentPlayer();

        // Draw 4 cards
        for (int i = 0; i < 4; i++) {
            Card drawnCard = run.getDeck().drawCard();
            if (drawnCard != null) {
                nextPlayer.drawCard(drawnCard);
            }
        }

        System.out.println(nextPlayer.getName() + " drew 4 cards and loses their turn.");

        // Player who played the card chooses the color
        System.out.println("Choose the next color:");
        return chooseColor(scanner);
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
     * Gets the point value of this card for scoring
     */
    public int getPoints() {
        switch (this.getType()) {
            case SKIP:
            case REVERSE:
            case DRAW_TWO:
                return 20;

            case WILD:
            case WILD_DRAW_FOUR:
                return 50;

            default:
                return 0; // NUMBER cards handled in base Card class
        }
    }

    /**
     * Checks if this card can be played on the given top card
     * @param topCard The current top card on the discard pile
     * @param currentColor The current active color (important for wild cards)
     * @return true if this card can be played
     */
    public boolean canPlayOn(Card topCard, Color currentColor) {
        // Wild cards can always be played
        if (this.getType() == Type.WILD || this.getType() == Type.WILD_DRAW_FOUR) {
            return true;
        }

        // Same color
        if (this.getColor() == currentColor || this.getColor() == topCard.getColor()) {
            return true;
        }

        // Same type (e.g., SKIP on SKIP)
        if (this.getType() == topCard.getType()) {
            return true;
        }

        return false;
    }
}
