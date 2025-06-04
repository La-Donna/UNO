import GAMEPLAY.UnoGame;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        System.out.println("Hello and welcome to Uno!");

        UnoGame game = new UnoGame();
        game.startGame();




        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose a card to play or type 'draw':");
        String input = scanner.nextLine();
    }

}