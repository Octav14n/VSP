package restopoly.dataaccesslayer.entities;

import javax.validation.constraints.NotNull;

/**
 * Created by octavian on 27.10.15.
 */
public class Place {
    private @NotNull String name;

    public Place(String name) {
        this.name = name;
    }

    // Needed by Spring.
    private Place() {}

    public String getName() {
        return name;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Place place = (Place) o;

        return !(name != null ? !name.equals(place.name) : place.name != null);

    }

    @Override public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
