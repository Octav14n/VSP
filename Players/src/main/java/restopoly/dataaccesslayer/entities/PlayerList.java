package restopoly.dataaccesslayer.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by octavian on 27.10.15.
 */
public class PlayerList {
    private List<Player> players;

    public PlayerList () {
        players = new ArrayList<>();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getPlayer(String playerId) {
        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }

    public Player addPlayer(String playerId, String name, String uri) {
        Player player = new Player(playerId, name, uri);
        players.add(player);
        return player;
    }
}
