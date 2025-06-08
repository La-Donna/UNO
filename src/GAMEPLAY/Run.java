package GAMEPLAY;


import PLAYERS.Player;
import PLAYERS.BotPlayer;
import CARDS.Deck;
import CARDS.Card;
import CARDS.Action_Cards;
import RULES.Referee;
import VARIATIONS.RunVariations;
import UI.Menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

/**
 * The central class to run the UNO game.
 * It orchestrates the entire game lifecycle: setup, managing player turns,
 * applying card effects, handling round and game endings, and interacting
 * with other game components like the Deck, Players, Referee, and Menu.
 */
public class Run {
    private List<Player> players;            // List of all players in the game (human and bot)
    private Deck gameDeck;                   // The game's deck of cards (draw and discard piles)
    private Card.Color currentActiveColor;   // The color that must be matched for the next play (can be set by Wild cards)
    private int currentPlayerIndex;          // Index of the player whose turn it currently is
    private boolean isClockwise;             // True for clockwise play direction, false for counter-clockwise
    private Scanner scanner;                 // Scanner for user input (passed to Menu)
    private Menu menu;                       // UI component for displaying menus and getting input
    private Referee referee;                 // Rule enforcer for validating plays and applying penalties
    private RunVariations gameVariants;      // Manages active game rule variations
    private final int MAX_PLAYERS = 4;       // Fixed maximum number of players for the game

    /**
     * Constructor for the Run class.
     * Initializes core game components that persist throughout the game's execution.
     */
    public Run() {
        scanner = new Scanner(System.in); // Initialize scanner once
        menu = new Menu(scanner);         // Menu instance for all UI interactions
        players = new ArrayList<>();      // List to hold all player objects
        gameDeck = new Deck();            // The game's deck of UNO cards
        referee = new Referee();          // The referee for rule enforcement
        gameVariants = new RunVariations(); // Manages optional game rules
        isClockwise = true;               // Game always starts in clockwise direction
    }

    /**
     * Orchestrates the initial setup phase of the game.
     * This includes:
     * 1. Getting player information (human vs. bot, names).
     * 2. Allowing the selection of optional game variants.
     * 3. Dealing initial hands of cards to all players.
     * 4. Setting up the initial card on the discard pile, handling special first cards.
     * 5. Determining the starting player.
     */
    public void setupGame() {
        System.out.println("--- Game Setup ---");

        // 1. Get human player names and automatically create bots to reach MAX_PLAYERS
        getPlayersInput();

        // 2. Allow player to select game variants
        gameVariants.selectGameVariants(menu); // Menu is passed for user interaction within RunVariations

        // 3. Deal 7 cards to each player
        dealInitialCards();

        // 4. Set up the initial discard pile (first card)
        setupInitialDiscardPile();

        // 5. Determine and display the starting player
        determineStartingPlayer();

        System.out.println("--- Setup Complete! Let the game begin! ---");
        menu.pressEnterToContinue(); // Pause before starting the game loop
    }

    /**
     * Gathers input for the number of human players and their names.
     * Automatically creates BotPlayers to fill the remaining slots up to MAX_PLAYERS.
     */
    private void getPlayersInput() {
        // Get the number of human players (0-MAX_PLAYERS)
        int humanPlayersCount = menu.getGameSetupInput(MAX_PLAYERS);

        // Create human player instances
        for (int i = 0; i < humanPlayersCount; i++) {
            String name = menu.getPlayerNameInput("Enter name for Human Player " + (i + 1) + ": ");
            players.add(new Player(name, false)); // false indicates not a bot
        }

        // Create bot player instances to reach MAX_PLAYERS
        int botPlayersCount = MAX_PLAYERS - humanPlayersCount;
        for (int i = 0; i < botPlayersCount; i++) {
            players.add(new BotPlayer("Bot " + (i + 1))); // true indicates a bot
        }

        System.out.println("\nPlayers in the game:");
        for (Player p : players) {
            System.out.println("- " + p.getName() + (p.isBot() ? " (Bot)" : " (Human)"));
        }
        System.out.println();
    }

    /**
     * Deals 7 cards from the shuffled deck to each player.
     */
    private void dealInitialCards() {
        System.out.println("Dealing initial cards...");
        for (int i = 0; i < 7; i++) { // Deal 7 cards to each player
            for (Player player : players) {
                player.drawCard(gameDeck.drawCard()); // Draw a card from the deck and add to player's hand
            }
        }
        System.out.println("Cards dealt to all players.");
        System.out.println();
    }

    /**
     * Draws the first card for the discard pile.
     * Special handling for action cards (Skip, Reverse, Draw Two) and Wild Draw Four
     * if they are the very first card:
     * - WILD_DRAW_FOUR: Reshuffled back into the deck, new card drawn.
     * - REVERSE: Direction of play is immediately reversed.
     * - SKIP: The first player's turn is skipped.
     * - DRAW_TWO: The first player draws 2 cards and their turn is skipped.
     * - WILD: The starting player chooses the initial color.
     */
    private void setupInitialDiscardPile() {
        Card firstCard;
        Action_Cards.ActionResult initialEffectResult = null; // To store effects of initial action card

        do {
            firstCard = gameDeck.drawCard();
            // If the first card drawn is a WILD_DRAW_FOUR, it must be reshuffled.
            if (firstCard.getType() == Card.Type.WILD_DRAW_FOUR) {
                System.out.println("Initial card was a WILD DRAW FOUR. Reshuffling and drawing again...");
                gameDeck.discardCard(firstCard); // Put it back to be reshuffled
                gameDeck.shuffleDrawPile();      // Reshuffle the draw pile
            } else if (firstCard.getType() == Card.Type.WILD) {
                // If it's a regular WILD, it's a valid first card. The starting player will choose color.
                break;
            }
            // For other action cards (SKIP, REVERSE, DRAW_TWO), they are valid initial cards,
            // but their effects apply to the first player.
            else {
                break; // A number card or a non-wild action card is a valid start
            }
        } while (true); // Loop continues until a valid initial card is drawn

        gameDeck.discardCard(firstCard); // Place the valid first card on discard pile
        currentActiveColor = firstCard.getColor(); // Set initial active color (will be NONE if it's a wild card type)

        System.out.println("First card on discard pile: " + firstCard);
        // Only show initial active color if it's not a Wild card (which will be set by the first player)
        if (firstCard.getType() != Card.Type.WILD && firstCard.getType() != Card.Type.WILD_DRAW_FOUR) {
            System.out.println("Initial active color: " + currentActiveColor);
        }
        System.out.println();

        // Apply initial effects of action cards if the first card was one
        if (firstCard instanceof Action_Cards) {
            Action_Cards actionCard = (Action_Cards) firstCard;
            initialEffectResult = actionCard.executeSpecialFunction(players.size());
            // Apply effects immediately to the game state
            handleActionCardEffect(initialEffectResult);

            // If the initial card was a WILD, the starting player chooses the color
            if (firstCard.getType() == Card.Type.WILD || firstCard.getType() == Card.Type.WILD_DRAW_FOUR) {
                // The current player (which will be the starting player after determineStartingPlayer() call below)
                // chooses the initial color.
                // This prompt needs to happen AFTER the starting player is determined.
                // For now, we set a temporary active color or let the determineStartingPlayer
                // and subsequent turn logic handle it.
                // For a starting WILD card, the color choice needs to be part of the initial "turn" setup.
                // To keep it simple, we'll let the regular turn logic handle the color choice
                // when the first player's turn comes, as the `needsColorChoice` will be true.
            }
        }
        menu.pressEnterToContinue(); // Pause before next setup step
    }

    /**
     * Randomly selects the player who will start the game.
     * The `currentPlayerIndex` is set to this player's index.
     */
    private void determineStartingPlayer() {
        Random random = new Random();
        currentPlayerIndex = random.nextInt(players.size());
        Player startingPlayer = players.get(currentPlayerIndex);
        System.out.println(startingPlayer.getName() + " starts the game!");

        // If the initial card was a WILD or WILD_DRAW_FOUR, the starting player needs to choose the color now
        if (gameDeck.peekTopDiscard().getType() == Card.Type.WILD || gameDeck.peekTopDiscard().getType() == Card.Type.WILD_DRAW_FOUR) {
            System.out.println(startingPlayer.getName() + ", as the starting player, please choose the initial color for the game.");
            currentActiveColor = menu.promptForColorChoice(); // Prompt for color
            System.out.println("Initial active color set to: " + currentActiveColor);
        }
        System.out.println();
    }

    /**
     * The main game loop.
     * This loop continues until a game-winning condition is met (e.g., a player reaches 500 points).
     * Each iteration manages a player's turn, checks for round/game endings, and resets for new rounds.
     */
    public void startGameLoop() {
        boolean gameOver = false;
        while (!gameOver) {
            Player currentPlayer = players.get(currentPlayerIndex); // Get the current player

            // Display current game state before player's turn
            displayGameState(currentPlayer);

            // Check if game win condition (e.g., score limit) is already met
            if (referee.checkGameWinCondition(players, gameVariants)) {
                gameOver = true; // Game ends
                break;
            }

            // Execute the current player's turn (handles playing cards, drawing, etc.)
            playPlayerTurn(currentPlayer);

            // After a player's turn, check if they won the current round (empty hand)
            if (currentPlayer.getHand().isEmpty()) {
                System.out.println("\n--- Round End ---");
                System.out.println(currentPlayer.getName() + " wins the round by playing their last card!");

                // Calculate points for the round based on cards in opponents' hands
                referee.calculateRoundPoints(players, currentPlayer);

                // Update total game points and display scores for all players
                for (Player p : players) {
                    p.endRound(); // Adds round points and penalties to game points and resets round/penalty points
                    p.showScore(); // Displays individual player's current scores
                }

                // Check if the game has ended based on accumulated scores
                if (referee.checkGameWinCondition(players, gameVariants)) {
                    gameOver = true; // Game ends
                    System.out.println("\n--- Game Over! ---");
                    displayGameEndScores(); // Show final scores
                    break;
                } else {
                    // If game not over, reset for a new round
                    System.out.println("\n--- Starting Next Round ---");
                    gameDeck.reset(); // Re-initialize and shuffle the deck
                    for (Player p : players) {
                        p.getHand().clear(); // Clear all players' hands
                    }
                    dealInitialCards();         // Deal new hands
                    setupInitialDiscardPile();  // Set up new first card for the round
                    determineStartingPlayer();  // Determine new starting player for the round
                    menu.pressEnterToContinue(); // Pause before next round begins
                }
            }
            // Advance to the next player only if the game is not yet over (neither round nor game ended)
            if (!gameOver) {
                advanceToNextPlayer();
            }
        }
        System.out.println("\nThanks for playing UNO!");
        scanner.close(); // Close the scanner when the game terminates
    }

    /**
     * Manages a single player's turn. This includes:
     * - For human players: displaying their hand, prompting for card choice or drawing,
     * handling invalid plays, and menu options.
     * - For bot players: executing their AI logic to choose a card or draw.
     * - Applying effects of played action cards.
     * - Checking for "UNO" call conditions.
     *
     * @param currentPlayer The player whose turn it is.
     */
    private void playPlayerTurn(Player currentPlayer) {
        boolean turnEnded = false; // Flag to control the turn loop
        while (!turnEnded) {
            if (currentPlayer.isBot()) {
                // --- Bot Player's Turn Logic ---
                BotPlayer bot = (BotPlayer) currentPlayer;
                System.out.println(bot.getName() + "'s turn. " + bot.getName() + " is thinking...");
                // Simulate bot thinking time for better user experience
                try {
                    Thread.sleep(1500); // Pause for 1.5 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                }

                // Bot decides which card to play (or null if it needs to draw)
                Card playedCard = bot.chooseCardToPlay(gameDeck.peekTopDiscard(), currentActiveColor);

                if (playedCard != null) {
                    // Bot found a playable card, try to play it
                    if (referee.isValidPlay(bot, playedCard, gameDeck.peekTopDiscard(), currentActiveColor, gameVariants)) {
                        bot.playCard(playedCard);          // Remove card from bot's hand
                        gameDeck.discardCard(playedCard);  // Add card to discard pile
                        System.out.println(bot.getName() + " plays: " + playedCard);
                        applyCardEffects(playedCard);      // Apply special effects of the card

                        // Bot checks if it has UNO and should call it
                        if (bot.hasUNO() && bot.shouldCallUNO()) {
                            System.out.println(bot.getName() + " calls UNO!");
                        }
                        turnEnded = true; // Bot successfully played a card, turn ends
                    } else {
                        // This case should ideally not happen with a well-designed bot AI.
                        // If it does, the bot is forced to draw.
                        System.out.println(bot.getName() + " attempted an invalid play and draws one card.");
                        Card drawnCard = gameDeck.drawCard();
                        bot.drawCard(drawnCard);
                        // Bot's turn ends after drawing due to invalid play (or could try to play drawn card)
                        turnEnded = true;
                    }
                } else {
                    // Bot has no playable cards, so it draws a card
                    Card drawnCard = gameDeck.drawCard();
                    bot.drawCard(drawnCard);
                    System.out.println(bot.getName() + " draws a card.");

                    // Check if the newly drawn card can be played immediately
                    if (referee.isValidPlay(bot, drawnCard, gameDeck.peekTopDiscard(), currentActiveColor, gameVariants)) {
                        // If playable, bot might play it (simple bot always plays if valid)
                        bot.playCard(drawnCard);
                        gameDeck.discardCard(drawnCard);
                        System.out.println(bot.getName() + " plays the drawn card: " + drawnCard);
                        applyCardEffects(drawnCard); // Apply effects of the drawn card
                        if (bot.hasUNO() && bot.shouldCallUNO()) {
                            System.out.println(bot.getName() + " calls UNO!");
                        }
                    } else {
                        System.out.println(bot.getName() + " cannot play the drawn card.");
                    }
                    turnEnded = true; // Bot's turn ends after drawing/potentially playing drawn card
                }
            } else {
                // --- Human Player's Turn Logic ---
                int choice = menu.getCardChoice(currentPlayer.getHand()); // Diese Methode zeigt die Hand bereits an!

                if (choice == 0) {
                    // Player chose to draw a card
                    Card drawnCard = gameDeck.drawCard();
                    currentPlayer.drawCard(drawnCard);
                    System.out.println(currentPlayer.getName() + " draws a card: " + drawnCard);

                    // Check if the drawn card can be played immediately
                    if (referee.isValidPlay(currentPlayer, drawnCard, gameDeck.peekTopDiscard(), currentActiveColor, gameVariants)) {
                        if (menu.getYesNoInput("Do you want to play the drawn card? (y/n): ")) {
                            currentPlayer.playCard(drawnCard);
                            gameDeck.discardCard(drawnCard);
                            System.out.println(currentPlayer.getName() + " plays the drawn card: " + drawnCard);
                            applyCardEffects(drawnCard);
                            turnEnded = true; // Turn ends after playing drawn card
                        } else {
                            System.out.println(currentPlayer.getName() + " chose not to play the drawn card.");
                            turnEnded = true; // Turn ends if player chooses not to play drawn card
                        }
                    } else {
                        System.out.println(currentPlayer.getName() + " cannot play the drawn card.");
                        turnEnded = true; // Turn ends if drawn card is not playable
                    }
                } else if (choice > 0) {
                    // Player chose to play a card from their hand
                    Card chosenCard = currentPlayer.getHand().get(choice - 1); // Get the actual Card object

                    // Validate if the chosen card can be played
                    if (referee.isValidPlay(currentPlayer, chosenCard, gameDeck.peekTopDiscard(), currentActiveColor, gameVariants)) {
                        currentPlayer.playCard(chosenCard);        // Remove card from hand
                        gameDeck.discardCard(chosenCard);          // Add card to discard pile
                        System.out.println(currentPlayer.getName() + " plays: " + chosenCard);
                        applyCardEffects(chosenCard);              // Apply special effects of the card

                        // Check for UNO call if player has 1 card left
                        if (currentPlayer.hasUNO()) {
                            if (!menu.getYesNoInput("You have UNO! Do you want to call UNO? (y/n): ")) {
                                // Penalty for forgetting UNO (Run class handles drawing based on Referee call)
                                referee.applyPenalty(currentPlayer, 2);
                                for(int i=0; i<2; i++) currentPlayer.drawCard(gameDeck.drawCard()); // Actually draw penalty cards
                                System.out.println("Penalty! " + currentPlayer.getName() + " draws 2 cards for not calling UNO!");
                            } else {
                                System.out.println(currentPlayer.getName() + " calls UNO!");
                            }
                        }
                        turnEnded = true; // Card played successfully, turn ends
                    } else {
                        System.out.println("Invalid card. You cannot play " + chosenCard + " on " + gameDeck.peekTopDiscard() + ".");
                        System.out.println("Please choose another card or '0' to draw.");
                        // Turn does NOT end, player is prompted to choose again.
                    }
                } else {
                    // Invalid input for card choice (getCardChoice returned -1, implying an issue)
                    System.out.println("Please enter a valid card number or '0' to draw.");
                    // Turn does NOT end, player is prompted to choose again.
                }
            }
        }
        // Pause before the next player's turn starts
        menu.pressEnterToContinue();
    }

    /**
     * Applies the specific effects of a played card.
     * This method delegates to the `Action_Cards` class for effect definition,
     * and then `handleActionCardEffect` interprets and applies them to the game state.
     *
     * @param playedCard The `Card` object that was just played.
     */
    private void applyCardEffects(Card playedCard) {
        // Only apply effects if it's an action card
        if (playedCard instanceof Action_Cards) {
            Action_Cards actionCard = (Action_Cards) playedCard;
            // Execute the action card's special function.
            // It returns an ActionResult object describing what should happen.
            // No Scanner is passed here, as Action_Cards doesn't handle input directly.
            Action_Cards.ActionResult result = actionCard.executeSpecialFunction(players.size());
            handleActionCardEffect(result); // Apply the effects to the game state
        }
        // For non-action cards, the current active color is simply the color of the played card.
        // This is handled AFTER card effects in the main game loop, or if no action card.
        currentActiveColor = playedCard.getColor(); // Update the active color to the color of the played card
    }

    /**
     * Interprets and applies the results of an `Action_Cards.ActionResult` to the game state.
     * This method modifies `isClockwise`, advances `currentPlayerIndex` for skips,
     * forces players to draw cards, and prompts for color choice if necessary.
     *
     * @param result The `ActionResult` object returned by an `Action_Cards` method.
     */
    private void handleActionCardEffect(Action_Cards.ActionResult result) {
        if (result.reverseDirection) {
            isClockwise = !isClockwise; // Toggle play direction
            System.out.println("Play direction reversed!");
        }
        if (result.skipNextPlayer) {
            System.out.println("Next player's turn is skipped!");
            advanceToNextPlayer(); // Advance `currentPlayerIndex` to effectively skip the next player
        }
        if (result.cardsToDrawByNextPlayer > 0) {
            Player playerToDraw = getNextPlayer(); // Get the player who must draw cards
            System.out.println(playerToDraw.getName() + " must draw " + result.cardsToDrawByNextPlayer + " cards!");
            for (int i = 0; i < result.cardsToDrawByNextPlayer; i++) {
                playerToDraw.drawCard(gameDeck.drawCard()); // Player draws cards from the deck
            }
        }
        if (result.needsColorChoice) {
            // The current player (who played the Wild card) gets to choose the new color
            currentActiveColor = menu.promptForColorChoice(); // Prompt user via menu
            System.out.println("Current active color set to: " + currentActiveColor);
        }
    }

    /**
     * Advances the `currentPlayerIndex` to the next player based on the current
     * direction of play (`isClockwise`). Handles wrapping around the player list.
     */
    private void advanceToNextPlayer() {
        if (isClockwise) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size(); // Move clockwise
        } else {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
            // The modulo operator in Java can return negative results for negative inputs.
            // Adding players.size() ensures a positive result before modulo.
            if (currentPlayerIndex < 0) {
                currentPlayerIndex += players.size();
            }
        }
    }

    /**
     * Returns the `Player` object that would be next in turn, without actually
     * changing the `currentPlayerIndex`. Useful for applying effects to the "next" player
     * before their turn officially begins (e.g., Draw Two/Four effects).
     *
     * @return The `Player` object who is next in line.
     */
    public Player getNextPlayer() {
        int nextIndex;
        if (isClockwise) {
            nextIndex = (currentPlayerIndex + 1) % players.size();
        } else {
            nextIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
            if (nextIndex < 0) { // Ensure positive index when wrapping around from 0 to last player
                nextIndex = players.size() - 1;
            }
        }
        return players.get(nextIndex);
    }

    /**
     * Displays the current state of the game to the console.
     * This includes:
     * - Whose turn it is.
     * - The top card on the discard pile and the current active color.
     * - The current direction of play.
     * - The number of cards in each player's hand.
     *
     * @param currentPlayer The `Player` object whose turn it currently is.
     */
    private void displayGameState(Player currentPlayer) {
        System.out.println("\n------------------------------------");
        System.out.println("Current Turn: " + currentPlayer.getName());
        System.out.println("Top Card on Discard Pile: " + gameDeck.peekTopDiscard());
        System.out.println("Current Active Color: " + currentActiveColor);
        System.out.println("Play Direction: " + (isClockwise ? "Clockwise ->" : "<- Counter-Clockwise"));
        System.out.println("--- Player Hands ---");
        for (Player p : players) {
            System.out.println("- " + p.getName() + ": " + p.getHand().size() + " cards");
        }
        System.out.println("------------------------------------");
    }

    /**
     * Displays the final scores for all players at the end of the entire game.
     * This is called once the overall game win condition is met.
     */
    private void displayGameEndScores() {
        System.out.println("\n--- Final Game Scores ---");
        // Sort players by score if needed, or just display current totals
        // For simplicity, just display as they are in the list
        for (Player p : players) {
            p.showScore(); // Displays name, total game points
        }
        System.out.println("-------------------------");
    }

    /**
     * Ends the current game. This can be called if a player quits or if the game
     * concludes. It resets game-specific data for a clean restart.
     */
    public void endGame() {
        System.out.println("Game aborted. Resetting scores...");
        for (Player p : players) {
            p.endGame(); // Reset total game points and penalties for each player
        }
        // Additional cleanup like resetting the deck might be done here if a new game isn't automatically starting
    }
}