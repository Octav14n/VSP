package restopoly.dataaccesslayer.entities;

import restopoly.dataaccesslayer.entities.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by octavian on 27.10.15.
 */
public class GameList {
    private int nextGameId = 0;
    private List<Game> games;

    public GameList() {
        games = new ArrayList<>();
    }

    public Game addGame() {
        Game game = new Game(nextGameId);
        nextGameId++;
        games.add(game);
        return game;
    }

    public Game getGame(int gameId) {
        for (Game game : games) {
            if (game.getGameid() == gameId) {
                return game;
            }
        }
        return null;
    }

    public List<Game> getGames() {
        return games;
    }
}
