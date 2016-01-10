package restopoly.events.dataaccesslayer.entities;

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

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setIsReady(boolean isReady) {
        this.isReady = isReady;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        restopoly.games.dataaccesslayer.entities.Player
            player = (restopoly.games.dataaccesslayer.entities.Player) o;

        if (id != null ? !id.equals(player.getId()) : player.getId() != null)
            return false;
        return !(uri != null ? !uri.equals(player.getUri()) : player.getUri() != null);

    }

    @Override public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        return result;
    }
}
