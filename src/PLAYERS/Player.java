package PLAYERS;

import java.util.ArrayList;
import java.util.List;
import CARDS.Card; // Make sure this import is there

/**
 * The Player class represents a human player in the UNO game.
 * It manages the player's hand, score, and basic game actions.
 */
public class Player {
    private String name;
    private List<Card> hand;
    private int roundPoints; // Points accumulated in the current round
    private int gamePoints;  // Total points accumulated across all rounds
    private int penaltyPoints; // Points from penalties (e.g., forgetting UNO)
    private boolean isBot;   // New field to distinguish between human and bot players

    // Constructor 1: For human players (or when the 'isBot' status is implied as false)
    public Player(String name) {
        this(name, false); // Calls the other constructor, setting isBot to false by default
    }

    // Constructor 2: For both human and bot players (this is the one you need!)
    public Player(String name, boolean isBot) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.roundPoints = 0;
        this.gamePoints = 0;
        this.penaltyPoints = 0;
        this.isBot = isBot; // Initialize the new field
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getRoundPoints() {
        return roundPoints;
    }

    public int getGamePoints() {
        return gamePoints;
    }

    public int getPenaltyPoints() {
        return penaltyPoints;
    }

    public boolean isBot() { // New getter for the isBot status
        return isBot;
    }

    // --- Game Actions ---
    public void drawCard(Card card) {
        hand.add(card);
    }

    public void playCard(Card card) {
        hand.remove(card);
    }

    public boolean hasUNO() {
        return hand.size() == 1;
    }

    public void addRoundPoints(int points) {
        this.roundPoints += points;
    }

    public void addPenaltyPoints(int points) {
        this.penaltyPoints += points;
    }

    /**
     * Ends the current round for the player.
     * Adds round points and penalty points to total game points and resets round points.
     */
    public void endRound() {
        this.gamePoints += this.roundPoints;
        this.gamePoints += this.penaltyPoints; // Penalties are added to game points at round end
        this.roundPoints = 0; // Reset for next round
        this.penaltyPoints = 0; // Reset penalties for next round
    }

    /**
     * Resets total game points and penalties for a completely new game.
     */
    public void endGame() {
        this.gamePoints = 0;
        this.roundPoints = 0;
        this.penaltyPoints = 0;
        this.hand.clear(); // Clear hand for a new game
    }

    /**
     * Displays the player's current scores.
     */
    public void showScore() {
        System.out.println(name + " - Total Game Points: " + gamePoints);
    }

    @Override
    public String toString() {
        return name;
    }
}