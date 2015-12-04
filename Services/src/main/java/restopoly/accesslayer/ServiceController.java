package restopoly.accesslayer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by octavian on 04.12.15.
 */
@RestController
public class ServiceController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void imAlive() {}
}
