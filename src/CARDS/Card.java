package CARDS;

public class Card {

    public enum Color { RED, BLUE, GREEN, YELLOW, WILD }
    public enum Type { NUMBER, SKIP, REVERSE, DRAW_TWO, WILD, WILD_DRAW_FOUR }

    private final Color color;
    private final Type type;
    private final Integer number; // Only used if type is NUMBER

    // Constructor for non-number cards
    public Card(Color color, Type type) {
        if (type == Type.NUMBER) {
            throw new IllegalArgumentException("Use the number constructor for NUMBER cards");
        }
        this.color = color;
        this.type = type;
        this.number = null;
    }

    // Constructor for number cards
    public Card(Color color, int number) {
        if (number < 0 || number > 9) {
            throw new IllegalArgumentException("UNO numbers must be between 0 and 9");
        }
        this.color = color;
        this.type = Type.NUMBER;
        this.number = number;
    }

    // Basic getters
    public Color getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }

    public Integer getNumber() {
        if (type != Type.NUMBER) {
            throw new UnsupportedOperationException("Only NUMBER cards have a number");
        }
        return number;
    }

    /**
     * Gets the point value of this card for scoring
     * Number cards: face value (0-9 points)
     * Action cards (SKIP, REVERSE, DRAW_TWO): 20 points
     * Wild cards (WILD, WILD_DRAW_FOUR): 50 points
     */
    public int getPoints() {
        if (type == Type.NUMBER) {
            return number;
        } else if (type == Type.SKIP || type == Type.REVERSE || type == Type.DRAW_TWO) {
            return 20;
        } else if (type == Type.WILD || type == Type.WILD_DRAW_FOUR) {
            return 50;
        }
        return 0;
    }

    /**
     * Checks if this card can be played on the given top card
     * @param topCard The current top card on the discard pile
     * @param currentColor The current active color (important for wild cards)
     * @return true if this card can be played
     */
    public boolean canPlayOn(Card topCard, Color currentColor) {
        // Wild cards can always be played
        if (this.type == Type.WILD || this.type == Type.WILD_DRAW_FOUR) {
            return true;
        }

        // Same color as current active color
        if (this.color == currentColor) {
            return true;
        }

        // Same color as top card
        if (topCard != null && this.color == topCard.getColor()) {
            return true;
        }

        // Same type (e.g., SKIP on SKIP)
        if (topCard != null && this.type == topCard.getType()) {
            return true;
        }

        // Same number for number cards
        if (topCard != null && this.type == Type.NUMBER && topCard.getType() == Type.NUMBER) {
            assert this.number != null;
            return this.number.equals(topCard.number);
        }

        return false;
    }

    @Override
    public String toString() {
        if (type == Type.NUMBER) {
            return color + " " + number;
        }
        if (type == Type.WILD || type == Type.WILD_DRAW_FOUR) {
            return type.name().replace("_", " ");
        }
        return color + " " + type.name().replace("_", " ");
    }
}
