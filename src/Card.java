public class Card {
    enum Color { RED, BLUE, GREEN, YELLOW, WILD }
    enum Type { NUMBER, SKIP, REVERSE, DRAW_TWO, WILD, WILD_DRAW_FOUR }

    private Color color;
    private Type type;
    private int number; // Only used if type is NUMBER
    // Constructor, getters, toString() etc.
    public Card(Color color, Type type) {
        this.color = color;
        this.type = type;
    }

    public Card(Type type, int number, Color color) {
        this.type = type;
        this.number = number;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }


    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "Card{" +
                "color=" + color +
                ", type=" + type +
                ", number=" + number +
                '}';
    }

}
