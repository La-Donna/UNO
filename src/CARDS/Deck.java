package CARDS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck {
    //Use a Stack<Card> for a draw pile where you generally pop from the top.
    private Stack<Card> drawPile;
    private Stack<Card> discardPile;

    public Deck() {
        drawPile = new Stack<>();
        discardPile = new Stack<>();
        initializeDeck();
        shuffleDrawPile();
    }

    //Deck Composition - initializeDeck(): fill the list
    public void initializeDeck() {
        drawPile.clear();

        for(Card.Color color : Card.Color.values()) {
            if (color == Card.Color.WILD) continue;

            //1x all colors x [0]:
            drawPile.add(new Card(color, 0));

            //2x all colors x [1-9]:
            for (int i = 1; i <= 9; i++){
                drawPile.add(new Card(color, i));
                drawPile.add(new Card(color, i));
            }
            //2x each action card [SKIP, REVERSE, DRAW_TWO]:
            for (int i = 0; i < 2; i++){
                drawPile.add(new Card(color, Card.Type.SKIP));
                drawPile.add(new Card(color, Card.Type.REVERSE));
                drawPile.add(new Card(color, Card.Type.DRAW_TWO));
            }
        }
        //4x each wild card [WILD, WILD_DRAW_FOUR]
        for (int i = 0; i < 4; i++) {
            drawPile.add(new Card(Card.Color.WILD, Card.Type.WILD));
            drawPile.add(new Card(Card.Color.WILD, Card.Type.WILD_DRAW_FOUR));
        }
    }

    //Shuffle with Collections.shuffle() - shuffle the cards
    public void shuffleDrawPile(){
        Collections.shuffle(drawPile);
    }

    //drawCard(): remove and return top card
    public Card drawCard() {
        if (drawPile.isEmpty()){
            if(discardPile.isEmpty()){
                throw new IllegalStateException("The draw and discard piles are empty!");
            }
            // if (isEmpty() ) --> reshuffle discard pile:
            reshuffleDiscardIntoDrawPile();
        }
        return drawPile.pop(); //.pop() for draw and .push(Card) for placing cards back on top
    }

    private void reshuffleDiscardIntoDrawPile(){
        Card topCard = discardPile.pop(); //Keep the top discard
        drawPile.addAll(discardPile);
        discardPile.clear();
        discardPile.push(topCard); //Add the top card back to discard
        Collections.shuffle(drawPile);
    }

    public void discardCard(Card card){
        discardPile.push(card);
    }

    //isEmpty(): check if the deck is empty
    public boolean isEmpty(){
        return drawPile.isEmpty();
    }

    //reset(): reinitialize and shuffle
    public void reset(){
        drawPile.clear();
        discardPile.clear();
        initializeDeck();
        shuffleDrawPile();
    }

    public Card peekTopDiscard(){
        if(discardPile.isEmpty()) return null;
        return discardPile.peek();
    }

    public int size(){
        return drawPile.size();
    }

    public void printDeck(){
        for(Card card : drawPile) {
            System.out.println(card);
        }
    }

/// implement separate class for Hand which will be given to the players
/// idea: referee class to moderate and count points of hands???

}