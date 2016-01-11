package restopoly.games.dataaccesslayer.entities;

import javax.validation.constraints.NotNull;

/**
 * Created by octavian on 11.01.16.
 */
public class GameComponents {
    private @NotNull String game;
    private String dice;
    private @NotNull String board;
    private @NotNull String bank;
    private String broker;
    private String decks;
    private @NotNull String events;

    private GameComponents() {}

    public GameComponents(String game, String dice, String board, String bank, String broker,
        String decks, String events) {
        this.game = game;
        this.dice = dice;
        this.board = board;
        this.bank = bank;
        this.broker = broker;
        this.decks = decks;
        this.events = events;
    }

    public String getGame() {
        return game;
    }

    public String getDice() {
        return dice;
    }

    public String getBoard() {
        return board;
    }

    public String getBank() {
        return bank;
    }

    public String getBroker() {
        return broker;
    }

    public String getDecks() {
        return decks;
    }

    public String getEvents() {
        return events;
    }
}
