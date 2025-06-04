package GAMEPLAY;

import CARDS.Card;
import CARDS.Deck;
import PLAYERS.Player;

import java.util.List;

public class UnoGame {

    private List<Player> players;
    private Deck deck;
    private List<Card> discardPile;
    private int currentPlayerIndex;
    private boolean isClockwise;


    public void startGame() {
        // Deal cards, initialize game state
        // While no one has won:
        // - Show current player's hand
        // - Accept input to play or draw
        // - Handle special cards (skip, reverse, draw two, etc.)
    }
}