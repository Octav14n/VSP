package restopoly.dataaccesslayer.entities;

import javax.validation.constraints.NotNull;

/**
 * GamePlayer is a helper entity for game to save additional data for every Player.
 */
public class GamePlayer {
    private @NotNull Player player;
    private int gameId;
    private boolean ready;

    public GamePlayer(Player player, int gameId) {
        this.player = player;
        this.gameId = gameId;
        this.ready = false;
    }

    // Needed by Spring.
    private GamePlayer() {}

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public Player getPlayer() {
        return player;
    }

    public int getGameId() {
        return gameId;
    }

    public boolean isReady() {
        return ready;
    }
}
