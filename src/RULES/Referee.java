package RULES;

import CARDS.Card;
import PLAYERS.Player;
import VARIATIONS.RunVariations;

import java.util.List;

/**
 * The Referee class enforces the rules of the UNO game.
 * It provides methods to:
 * - Validate if a player's chosen card can be played on the discard pile.
 * - Apply penalties (e.g., drawing cards).
 * - Calculate points at the end of a round.
 * - Check for overall game win conditions.
 */
public class Referee {

    /**
     * Checks if a chosen card is a valid play according to UNO rules.
     * A card is valid if:
     * 1. Its color matches the current active color.
     * 2. Its type/value matches the top card on the discard pile.
     * 3. It's a Wild card (which can always be played).
     *
     * @param player           The player attempting to play the card.
     * @param chosenCard       The card the player wants to play.
     * @param topDiscardCard   The card currently on top of the discard pile.
     * @param currentActiveColor The color that must be matched (can be set by a Wild card).
     * @param gameVariants     The active game rule variations.
     * @return true if the play is valid, false otherwise.
     */
    public boolean isValidPlay(Player player, Card chosenCard, Card topDiscardCard, Card.Color currentActiveColor, RunVariations gameVariants) {
        // Rule 1: Wild cards can always be played.
        if (chosenCard.getType() == Card.Type.WILD || chosenCard.getType() == Card.Type.WILD_DRAW_FOUR) {
            return true;
        }

        // Rule 2: If the top card is a Wild card and its color was chosen,
        // the played card must match that chosen color.
        if (topDiscardCard.getType() == Card.Type.WILD || topDiscardCard.getType() == Card.Type.WILD_DRAW_FOUR) {
            return chosenCard.getColor() == currentActiveColor;
        }

        // Rule 3: Match color or type
        return chosenCard.getColor() == topDiscardCard.getColor() ||
                chosenCard.getType() == topDiscardCard.getType() ||
                chosenCard.getColor() == currentActiveColor; // Match chosen color from previous wild card
    }

    /**
     * Applies a penalty to a player, usually forcing them to draw cards.
     *
     * @param player       The player to whom the penalty is applied.
     * @param numberOfCards The number of cards the player must draw.
     */
    public void applyPenalty(Player player, int numberOfCards) {
        // In the `Run` class, the actual drawing of cards will be handled
        // after this method is called (e.g., in the UNO call penalty).
        // This method primarily logs the penalty for the score tracking,
        // or can be extended to directly draw if the Deck instance is passed.
        // For now, it updates the player's penalty count.
        player.addPenaltyPoints(numberOfCards * 10); // Example: 10 points per card penalty
        System.out.println(player.getName() + " received a penalty of " + numberOfCards + " cards!");
    }


    /**
     * Calculates the points for the winning player at the end of a round.
     * Points are awarded based on the cards remaining in opponents' hands.
     *
     * @param players      The list of all players in the game.
     * @param roundWinner  The player who won the current round (played their last card).
     */
    public void calculateRoundPoints(List<Player> players, Player roundWinner) {
        int roundPoints = 0;
        System.out.println("--- Calculating Round Points ---");
        for (Player p : players) {
            if (p != roundWinner) { // Don't count cards in the winner's hand
                int playerCardPoints = 0;
                for (Card card : p.getHand()) {
                    playerCardPoints += card.getPoints(); // Each card type has a point value
                }
                p.addRoundPoints(playerCardPoints); // Add to current round's score
                roundPoints += playerCardPoints; // Accumulate for the winner
                System.out.println("- " + p.getName() + " had " + p.getHand().size() + " cards worth " + playerCardPoints + " points.");
            }
        }
        roundWinner.addRoundPoints(roundPoints); // Winner gets all points from opponents' hands
        System.out.println(roundWinner.getName() + " gains " + roundPoints + " points this round.");
        System.out.println("--------------------------------");
    }

    /**
     * Checks if the overall game win condition has been met by any player.
     * The game typically ends when a player reaches a certain score threshold (e.g., 500 points).
     *
     * @param players      The list of all players in the game.
     * @param gameVariants The active game rule variations, which may define the winning score.
     * @return true if the game-winning condition is met by any player, false otherwise.
     */
    public boolean checkGameWinCondition(List<Player> players, RunVariations gameVariants) {
        int winningScore = gameVariants.getWinningScore(); // Get winning score from game variants

        for (Player player : players) {
            if (player.getGamePoints() >= winningScore) {
                System.out.println("\n--- Game Win Condition Met! ---");
                System.out.println(player.getName() + " has reached " + player.getGamePoints() + " points, surpassing the target of " + winningScore + "!");
                return true; // Game ends
            }
        }
        return false; // Game continues
    }
}
