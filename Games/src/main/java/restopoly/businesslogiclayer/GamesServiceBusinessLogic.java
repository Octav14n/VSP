package restopoly.businesslogiclayer;

import restopoly.dataaccesslayer.entities.Game;
import restopoly.dataaccesslayer.entities.Player;

import java.util.List;

/**
 * Created by patri on 07.12.2015.
 */
public class GamesServiceBusinessLogic {

    public GamesServiceBusinessLogic() {

    }

    /**
     * This method will get a single player of the game.
     *
     * @param game     The given game is the game, where the player should be joined.
     * @param playerid The playerid is given to get the player of this game.
     * @return         Returns a Player.
     */
    public Player getPlayer(Game game, String playerid) {
        Player player = null;
        for (Player playerFromGame : game.getPlayers()) {
            if (playerFromGame.getId().equals(playerid)) {
                player = playerFromGame;
            }
        }
        return player;
    }

    /**
     * This method will set the ready flag for a player at the game.
     * This method delegates to the method included in game.
     *
     * @param game    This is the game in which the player joined.
     * @param player  This is the player where the ready flag should be set.
     * @param ready   This ready flag can be false or true.
     */
    public void setPlayerReady(Game game, Player player, boolean ready) {
        List<Player> listWithPlayers = game.getPlayers();
        for (Player playerFromListWithPlayers : listWithPlayers) {
            if (player.equals(playerFromListWithPlayers)) {
                game.setReady(player, ready);
            }
        }
    }

    /**
     * This method checks for a game, if the requested player is ready.
     *
     * @param game      This is the game where the player will be checked for his ready status.
     * @param player    This is the player who should be in the game.
     * @return          Returns a boolean, which shows, if the player at the game is ready or not.
     */
    public boolean getReadyPlayer(Game game, Player player) {
        List<Player> listWithPlayers = game.getPlayers();
        boolean isReady = false;
        for (Player gamePlayer : listWithPlayers) {
            if (player.equals(gamePlayer)) {
                if (gamePlayer.isReady() == true) {
                    isReady = game.getReady(player);
                }
            }
        }
        return isReady;
    }

    /**
     * This method will get the player, who has the current turn.
     *
     * @param game This is the game, where the currentPlayer is available.
     * @return     Returns a player, who will make the next turn.
     */
    public Player getCurrentPlayer(Game game) {
        List<Player> listWithPlayers = game.getPlayers();

        if (listWithPlayers.isEmpty()) {
            return null;
        }

        for (Player player : listWithPlayers) {
            if (!game.getReady(player)) {
                return null;
            }
        }

        Player currentPlayer = game.getCurrentPlayer();
        return currentPlayer;
    }
}
