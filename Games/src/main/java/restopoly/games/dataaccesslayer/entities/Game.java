package restopoly.games.dataaccesslayer.entities;

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

    private String gameid;
    private GameComponents components;
    private @NotEmpty @NotNull List<Player> players;
    private String uri;

    public Game(String gameid, String uri, GameComponents components) {
        this.gameid = gameid;
        this.uri = uri;
        this.components = components;
        this.players = new ArrayList<>();
    }

    /**
    * This method will get the gameid for a game.
    *
    * @return Returns the gameid as a String (UID).
    */
    public String getGameid() {
        return gameid;
    }

    /**
     * This method will get the available components linked with this game.
     *
     * @return
     */
    public GameComponents getComponents() {
        return components;
    }

    /**
     * This method will get all players, which have join the game.
     *
     * @return Returns a list with all players of the game.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * This method will add a new player into the players list.
     *
     * @param player This player will be added to the playerlist.
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * This method will set the ready flag for the player at the game.
     *
     * @param player This is the player for which the ready flag should be set.
     * @param ready  This parameter could be true or false for setting the ready flag.
     */
    public void setReady(Player player, boolean ready) {
        player.setIsReady(ready);
    }

    /**
     * This method gets the ready flag for the given player.
     * This player is from the List of players at this game.
     * The player was determined at the method in gamesServiceBusinessLogic.
     *
     * @param player The player for which the ready status should be asked.
     * @return       Returns a boolean which is true if the player is ready or false if he is not ready.
     */
    public boolean getReady(Player player) {
        return player.isReady();
    }

    /**
     * This method will get the current player for the game.
     *
     * @return Returns the player which will make the next turn.
     */
    public Player getCurrentPlayer() {
        Player currentPlayer = players.get(0);
        return currentPlayer;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Game game = (Game) object;

        if (gameid != null ? !gameid.equals(game.gameid) : game.gameid != null) {
            return false;
        }

        return !(players != null ? !players.equals(game.players) : game.players != null);

    }

    @Override
    public int hashCode() {
        int result = gameid != null ? gameid.hashCode() : 0;
        result = 31 * result + (players != null ? players.hashCode() : 0);
        return result;
    }
}
