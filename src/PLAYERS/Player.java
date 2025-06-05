package PLAYERS;

import CARDS.Card;
import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name;
    private int score;
    private boolean isHuman;
    private List<Card> hand;

    // Constructor
    public Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
        this.score = 0;
        this.hand = new ArrayList<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public List<Card> getHand() {
        return new ArrayList<>(hand); // Return a copy to prevent external modification
    }

    public int getHandSize() {
        return hand.size();
    }

    // Hand management methods
    public void drawCard(Card card) {
        if (card != null) {
            hand.add(card);
        }
    }

    public void drawCards(List<Card> cards) {
        if (cards != null) {
            hand.addAll(cards);
        }
    }

    public boolean playCard(Card card) {
        return hand.remove(card);
    }

    public Card playCard(int index) {
        if (index >= 0 && index < hand.size()) {
            return hand.remove(index);
        }
        return null;
    }

    public boolean hasCard(Card card) {
        return hand.contains(card);
    }

    public boolean hasCards() {
        return !hand.isEmpty();
    }

    public boolean hasWon() {
        return hand.isEmpty();
    }

    // Score management
    public void addScore(int points) {
        this.score += points;
    }

    public void resetScore() {
        this.score = 0;
    }

    /**
     * Calculates the total points in the player's hand
     * Used for scoring when a round ends
     */
    public int calculateHandPoints() {
        int totalPoints = 0;
        for (Card card : hand) {
            totalPoints += card.getPoints();
        }
        return totalPoints;
    }

    /**
     * Clears the player's hand (for new round)
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Gets playable cards from hand based on top card and current color
     */
    public List<Card> getPlayableCards(Card topCard, Card.Color currentColor) {
        List<Card> playableCards = new ArrayList<>();
        for (Card card : hand) {
            if (card.canPlayOn(topCard, currentColor)) {
                playableCards.add(card);
            }
        }
        return playableCards;
    }

    /**
     * Checks if player has any playable cards
     */
    public boolean hasPlayableCard(Card topCard, Card.Color currentColor) {
        return !getPlayableCards(topCard, currentColor).isEmpty();
    }

    /**
     * Displays the player's hand with indices
     */
    public void displayHand() {
        System.out.println(name + "'s hand (" + hand.size() + " cards):");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i + 1) + ". " + hand.get(i));
        }
    }

    /**
     * Displays only playable cards with indices
     */
    public void displayPlayableCards(Card topCard, Card.Color currentColor) {
        List<Card> playableCards = getPlayableCards(topCard, currentColor);
        System.out.println(name + "'s playable cards:");

        if (playableCards.isEmpty()) {
            System.out.println("No playable cards!");
            return;
        }

        for (int i = 0; i < playableCards.size(); i++) {
            Card card = playableCards.get(i);
            int handIndex = hand.indexOf(card);
            System.out.println((i + 1) + ". " + card + " (Hand position: " + (handIndex + 1) + ")");
        }
    }

    @Override
    public String toString() {
        return name + " (Score: " + score + ", Cards: " + hand.size() + ")";
    }
}
