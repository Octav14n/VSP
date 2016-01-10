package restopoly.players.businesslogiclayer;

import restopoly.players.dataaccesslayer.entities.Player;
import restopoly.players.dataaccesslayer.entities.PlayerList;

/**
 * Created by Patrick Steinhauer on 06.12.2015.
 */
public class PlayersServiceBusinessLogic {

    private PlayerList playerList;

    public PlayersServiceBusinessLogic(PlayerList playerList) {
        this.playerList = playerList;
    }

    /**
     * This method will add a new player to the playerlist.
     *
     * @param playerid This ID will represent the unique playerid.
     * @param name     The name from the new player.
     * @param uri      The URI which is special for the player.
     * @return         Returns a player, which is added to the playerlist.
     */
    public Player addPlayer(String playerid, String name, String uri) {
        Player player = new Player(playerid, name, uri);
        playerList.addPlayer(player);
        return player;
    }

    /**
     * This method will get the player for the given ID.
     *
     * @param playerid This ID identifies the player and will be used for getting this player.
     * @return         Returns a player for the given playerid.
     */
    public Player getPlayer(String playerid) {
        for (Player player : playerList.getPlayers()) {
            if (player.getId().equals(playerid)) {
                return player;
            }
        }
        return null;
    }
}
