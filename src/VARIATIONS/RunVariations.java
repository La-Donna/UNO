package VARIATIONS;

import UI.Menu; // Required for user interaction

/**
 * The RunVariations class manages optional game rule variations (house rules)
 * that can be enabled or disabled at the start of the game.
 */
public class RunVariations {
    private boolean allowStackingDrawCards; // Example: Can you play a +2 on a +2?
    private boolean jumpInRuleActive;       // Example: Can you play a card out of turn if it's identical?
    private int winningScore;               // Customizable winning score (e.g., 250, 500, 1000)

    /**
     * Constructor for RunVariations.
     * Initializes all variants to their default states.
     */
    public RunVariations() {
        this.allowStackingDrawCards = false; // Default: No stacking
        this.jumpInRuleActive = false;       // Default: No jump-in
        this.winningScore = 500;             // Default: Game ends at 500 points
    }

    /**
     * Presents the user with options to enable/disable game variants.
     * This method interacts with the Menu class to get user input.
     * @param menu The Menu instance to use for user interaction.
     */
    public void selectGameVariants(Menu menu) {
        System.out.println("\n--- Select Game Rule Variations ---");

        // Option for stacking Draw Two/Draw Four cards
        this.allowStackingDrawCards = menu.getYesNoInput("Enable stacking of Draw Two/Four cards? (y/n)");
        System.out.println("Stacking Draw Cards: " + (allowStackingDrawCards ? "Enabled" : "Disabled"));

        // Option for the Jump-In rule
        this.jumpInRuleActive = menu.getYesNoInput("Enable 'Jump-In' rule (playing out of turn with identical card)? (y/n)");
        System.out.println("'Jump-In' Rule: " + (jumpInRuleActive ? "Enabled" : "Disabled"));

        // Option for customizable winning score
        this.winningScore = getWinningScoreInput(menu);
        System.out.println("Winning Score set to: " + winningScore + " points.");

        System.out.println("------------------------------------");
        menu.pressEnterToContinue();
    }

    /**
     * Prompts the user to set the winning score for the game.
     * @param menu The Menu instance to use for user interaction.
     * @return The chosen winning score.
     */
    private int getWinningScoreInput(Menu menu) {
        int score = -1;
        while (score <= 0) { // Winning score must be positive
            menu.displayMessage("Enter the winning score for the game (e.g., 250, 500, 1000 - current default: " + winningScore + "): ");
            try {
                String input = menu.readLine();// Direct scanner access, but you could add a method to Menu for this
                score = Integer.parseInt(input);
                if (score <= 0) {
                    menu.displayMessage("Winning score must be a positive number.");
                }
            } catch (NumberFormatException e) {
                menu.displayMessage("Invalid input. Please enter a number.");
            }
        }
        return score;
    }

    /**
     * Checks if stacking of Draw Two/Four cards is allowed.
     * @return true if stacking is allowed, false otherwise.
     */
    public boolean isAllowStackingDrawCards() {
        return allowStackingDrawCards;
    }

    /**
     * Checks if the Jump-In rule is active.
     * @return true if Jump-In is active, false otherwise.
     */
    public boolean isJumpInRuleActive() {
        return jumpInRuleActive;
    }

    /**
     * Gets the current winning score for the game.
     * @return The winning score.
     */
    public int getWinningScore() {
        return winningScore;
    }
}