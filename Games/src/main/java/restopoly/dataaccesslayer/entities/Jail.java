package restopoly.dataaccesslayer.entities;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;

/**
 * Created by octavian on 27.10.15.
 */
public class Jail {
    private ArrayList<Player> inmates = new ArrayList<>();

    public void addInmate(Player player) {
        inmates.add(player);
    }

    public ArrayList<Player> showInmates() {
        return inmates;
    }
}
