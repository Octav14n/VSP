package restopoly.dataaccesslayer.entities;

import restopoly.dataaccesslayer.entities.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by octavian on 27.10.15.
 */
public class GameList {
    private List<Game> games;

    public GameList() {
        games = new ArrayList<>();
    }

    public Game addGame() {
        Game game = new Game(UUID.randomUUID().toString());
        games.add(game);
        return game;
    }

    public Game getGame(String gameId) {
        for (Game game : games) {
            if (game.getGameid().equals(gameId)) {
                return game;
            }
        }
        return null;
    }

    public List<Game> getGames() {
        return games;
    }
}
