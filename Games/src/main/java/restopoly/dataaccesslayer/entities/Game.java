package restopoly.dataaccesslayer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by octavian on 27.10.15.
 */
@JsonIgnoreProperties("currentPlayer")
public class Game {
    private int gameid;
    private @NotEmpty @NotNull List<GamePlayer> gamePlayers;

    public Game(int gameid) {
        this.gameid = gameid;
        gamePlayers = new ArrayList<>();
    }

    public int getGameid() {
        return gameid;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (GamePlayer gamePlayer : gamePlayers) {
            players.add(gamePlayer.getPlayer());
        }
        return players;
    }

    public void addPlayer(Player player) {
        gamePlayers.add(new GamePlayer(player, this.getGameid()));
    }

    public void setReady(Player player, boolean ready) {
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getPlayer().equals(player)) {
                gamePlayer.setReady(ready);
                return;
            }
        }
    }

    public boolean getReady(Player player) {
        boolean isReady = false;
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getPlayer().equals(player)) {
                if (gamePlayer.isReady() == true) {
                    isReady = true;
                }
            }
        }
        return isReady;
    }

    public Player getCurrentPlayer() {
        if (gamePlayers.isEmpty()) {
            return null;
        }
        // Check that all players are ready.
        for (GamePlayer gamePlayer : gamePlayers) {
            if (!gamePlayer.isReady()) {
                return null;
            }
        }
        GamePlayer gamePlayer = gamePlayers.get(0);
        Player currentPlayer = gamePlayer.getPlayer();
        return currentPlayer;
    }
}
