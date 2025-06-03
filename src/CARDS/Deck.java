package CARDS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck {
    //Use a List<Card> for the deck
    private Stack<Card> cards;

/// find out more about stacks and make sure the card-stacks are available + draw and discard
    public Deck() {
       // this.cards = new ArrayList<>();
        this.cards = new Stack<>();
        initializeDeck();
        shuffle();
    }

    //Deck Composition - initializeDeck(): fill the list
    public void initializeDeck() {
        cards.clear();

        for(Card.Color color : Card.Color.values()) {
            if (color == Card.Color.WILD) continue;

            //1x all colors x [0]:
            cards.add(new Card(color, 0));

            //2x all colors x [1-9]:
            for (int i = 1; i <= 9; i++){
                cards.add(new Card(color, i));
                cards.add(new Card(color, i));
            }
            //2x each action card [SKIP, REVERSE, DRAW_TWO]:
            for (int i = 1; i < 2; i++){
                cards.add(new Card(color, Card.Type.SKIP));
                cards.add(new Card(color, Card.Type.REVERSE));
                cards.add(new Card(color, Card.Type.DRAW_TWO));
            }
        }
        //4x each wild card [WILD, WILD_DRAW_FOUR]
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Card.Color.WILD, Card.Type.WILD));
            cards.add(new Card(Card.Color.WILD, Card.Type.WILD_DRAW_FOUR));
        }
    }

    //Shuffle with Collections.shuffle() - shuffle the cards
    public void shuffle(){
        Collections.shuffle(cards);
    }

    //drawCard(): remove and return top card
    public Card drawCard() {
        if (isEmpty()){
            throw new IllegalStateException("The deck is empty!");
        }
        return cards.remove(0);
    }
/// if (isEmpty() ) --> reshuffle discard pile!!
/// add Draw-Pile and Discard-Pile temp

    //isEmpty(): check if the deck is empty
    public boolean isEmpty(){
        return cards.isEmpty();
    }

    public int size(){
        return cards.size();
    }

    //reset(): reinitialize and shuffle
    public void reset(){
        initializeDeck();
        shuffle();
    }

    public void printDeck(){
        for(Card card : cards) {
            System.out.println(card);
        }
    }

/// implement separate class for Hand which will be given to the players
/// idea: referee class to moderate and count points of hands???

}