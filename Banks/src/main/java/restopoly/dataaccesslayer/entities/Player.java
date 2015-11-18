package restopoly.dataaccesslayer.entities;

import javax.validation.constraints.NotNull;

/**
 * Created by octavian on 13.10.15.
 */
public class Player {
    private @NotNull String id;
    private @NotNull String name;
    private @NotNull Place place;

    public Player(String id, String name, Place place) {
        this.id = id;
        this.name = name;
        this.place = place;
    }

    // Needed by Spring.
    private Player() {}

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Place getPlace() {
        return place;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Player player = (Player) o;

        if (id != null ? !id.equals(player.id) : player.id != null)
            return false;
        if (name != null ? !name.equals(player.name) : player.name != null)
            return false;
        return !(place != null ? !place.equals(player.place) : player.place != null);

    }

    @Override public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (place != null ? place.hashCode() : 0);
        return result;
    }
}
