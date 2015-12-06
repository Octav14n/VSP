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

    /**
     * This method will add the new player to the playerlist of the game.
     *
     * @param player The player, which will be added to the playerlist.
     */
    public void addPlayer(Player player) {
        players.add(player);
    }
}
