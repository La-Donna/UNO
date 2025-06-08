package PLAYERS;
import CARDS.Card; // Make sure this import is there
import java.util.List;

/**
 * The BotPlayer class represents an AI-controlled player in the UNO game.
 * It extends the Player class and implements its own logic for choosing cards.
 */
public class BotPlayer extends Player {

    public BotPlayer(String name) {
        super(name, true); // Call the Player constructor with name and 'true' for isBot
    }

    /**
     * Bot's logic to choose a card to play from its hand.
     * It tries to play a card that matches the top discard card's color or type.
     * Prioritizes playing action cards if possible.
     * If no direct match, it might try to play a Wild card.
     *
     * @param topDiscardCard The card currently on top of the discard pile.
     * @param currentActiveColor The color that must be matched (set by a Wild card).
     * @return The card the bot chooses to play, or null if it cannot play any card.
     */
    public Card chooseCardToPlay(Card topDiscardCard, Card.Color currentActiveColor) {
        List<Card> currentHand = getHand(); // Access hand from superclass

        // Strategy 1: Find a direct match (color or type) or an action card of matching color
        for (Card card : currentHand) {
            // Check if it's a direct color match, or a direct type match
            if (card.getColor() == currentActiveColor ||
                    card.getType() == topDiscardCard.getType()) {
                return card; // Found a playable card
            }
        }

        // Strategy 2: If no direct match, look for a Wild card
        for (Card card : currentHand) {
            if (card.getType() == Card.Type.WILD || card.getType() == Card.Type.WILD_DRAW_FOUR) {
                return card; // Play a wild card if nothing else fits
            }
        }

        return null; // No playable card found, bot must draw
    }

    /**
     * Bot's logic to decide if it should call UNO.
     * A simple bot always calls UNO if it has one card left.
     * @return true if the bot should call UNO, false otherwise.
     */
    public boolean shouldCallUNO() {
        return true; // Simple bot always calls UNO
    }
}