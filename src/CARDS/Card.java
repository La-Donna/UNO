package CARDS;

public class Card {

    enum Color { RED, BLUE, GREEN, YELLOW, WILD }
    enum Type { NUMBER, SKIP, REVERSE, DRAW_TWO, WILD, WILD_DRAW_FOUR }

    private final Color color;
    private final Type type;
    private final Integer number; // Only used if type is NUMBER

    // Constructor for non-number cards:
    public Card(Color color, Type type) {
        if (type == Type.NUMBER) {
            throw new IllegalArgumentException("Use the number constructor for NUMBER cards");
        }
        this.color = color;
        this.type = type;
        this.number = null;
    }

    // Constructor for number cards:
    public Card(Color color, int number) {
        if (number < 0 || number > 9) {
            throw new IllegalArgumentException("UNO numbers must be between 0 and 9");
        }
        this.color = color;
        this.type = Type.NUMBER;
        this.number = number;

    }

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

    @Override
    public String toString() {
        if (type == Type.NUMBER) {
            return color + " " + number;
        }
        if (type == Type.WILD || type == Type.WILD_DRAW_FOUR) {
            return type.name();
        }
        return color + " " + type.name();
    }

}
