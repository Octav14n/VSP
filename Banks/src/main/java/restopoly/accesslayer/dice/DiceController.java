package restopoly.accesslayer.dice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import restopoly.dataaccesslayer.entities.Roll;

import java.util.Random;

/**
 * Created by octavian on 13.10.15.
 */
@RestController
public class DiceController {
    Random rand = new Random(System.currentTimeMillis());

    @RequestMapping(value = "/dice", method = RequestMethod.GET)
    public Roll roll() {
        Roll r = new Roll(rand.nextInt(6) + 1);
        return r;
    }
}
