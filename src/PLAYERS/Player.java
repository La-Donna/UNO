import java.util.List;

public class Player {

    private String name;
    private int score;
    private boolean isHuman;
    private List<Card> hand = new ArrayList<>();
    //COnstructor
    public Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
        score = 0;
    }

}
