package restopoly.games.dataaccesslayer.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by octavian on 27.10.15.
 */
public class Jail {
    private List<Player> inmates = new ArrayList<>();

    public void addInmate(Player player) {
        inmates.add(player);
    }

    public List<Player> showInmates() {
        return inmates;
    }
}
