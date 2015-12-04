package restopoly.accesslayer.player;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import restopoly.accesslayer.exceptions.ParameterIsInvalidException;
import restopoly.dataaccesslayer.entities.Place;
import restopoly.dataaccesslayer.entities.Player;
import restopoly.dataaccesslayer.entities.PlayerList;

import java.util.List;

/**
 * Created by octavian on 27.10.15.
 */
@RestController
public class PlayerController {
    private PlayerList playerList = new PlayerList();

    @RequestMapping(value = "/players", method = RequestMethod.GET)
    public List<Player> getPlayers() {
        return playerList.getPlayers();
    }

    @RequestMapping(value = "/players", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Player addPlayer(String playerId, String name, Place place) {
        if (playerId == null || playerId.isEmpty())
            throw new ParameterIsInvalidException("playerId");
        if (name == null || name.isEmpty())
            throw new ParameterIsInvalidException("name");
        if (place == null)
            throw new ParameterIsInvalidException("place");
        return playerList.addPlayer(playerId, name, place);
    }

    @RequestMapping(value = "/players/{playerId}", method = RequestMethod.GET)
    public Player getPlayer(@PathVariable String playerId) {
        return playerList.getPlayer(playerId);
    }
}
