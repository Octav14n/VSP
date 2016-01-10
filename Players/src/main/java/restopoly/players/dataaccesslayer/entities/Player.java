package restopoly.players.dataaccesslayer.entities;

import javax.validation.constraints.NotNull;

/**
 * Created by octavian on 13.10.15.
 */
public class Player {
    private @NotNull String id;
    private @NotNull String name;
    private @NotNull String uri;
    private boolean isReady;

    public Player(String id, String name, String uri) {
        this.id = id;
        this.name = name;
        this.uri = uri;
    }

    // Needed by Spring.
    private Player() {}

    /**
     * This method will get the name of the Player.
     *
     * @return Returns the name as a String of this player.
     */
    public String getName() {
        return name;
    }

    /**
     * This method will get the id of the player.
     *
     * @return Returns the id for this player.
     */
    public String getId() {
        return id;
    }

    /**
     * This method will get the spezified URI for the player.
     *
     * @return Returns a String, which is the URI for the player.
     */
    public String getUri() {
        return uri;
    }

    /**
     * This method will get the status from a player.
     *
     * @return Returns true or false. If the player is at status ready the return value is true. Else it will be false.
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * This method will set the status for a player.
     *
     * @param isReady isReady is given to set the ready status for this player to ready or to false.
     */
    public void setIsReady(boolean isReady) {
        this.isReady = isReady;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Player player = (Player) o;

        if (id != null ? !id.equals(player.id) : player.id != null)
            return false;
        return !(uri != null ? !uri.equals(player.uri) : player.uri != null);

    }

    @Override public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        return result;
    }
}
