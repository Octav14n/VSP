package restopoly.accesslayer.jail;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import restopoly.dataaccesslayer.entities.Jail;
import restopoly.dataaccesslayer.entities.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by octavian on 13.10.15.
 */
@RestController
public class JailController {
    private Jail jail = new Jail();

    @RequestMapping(value = "/jail", method = RequestMethod.POST)
    public void addInmate(Player player) {
        jail.addInmate(player);
    }

    @RequestMapping(value = "/jail", method = RequestMethod.GET)
    public List<Player> showInmates() {
        return jail.showInmates();
    }
}
