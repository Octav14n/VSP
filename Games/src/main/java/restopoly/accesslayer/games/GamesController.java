package restopoly.accesslayer.games;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import restopoly.accesslayer.exceptions.GameNotFoundException;
import restopoly.accesslayer.exceptions.PlayerNotFoundException;
import restopoly.accesslayer.player.PlayerController;
import restopoly.dataaccesslayer.entities.*;

import java.util.List;

@RestController()
public class GamesController {

    private GameList gameList = new GameList();
    @Autowired
    private PlayerController playerController;

    @RequestMapping(value = "/games", method = RequestMethod.GET)
    public List<Game> getGames() {
        return gameList.getGames();
    }

    @RequestMapping(value = "/games", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Game createGame() {
        return gameList.addGame();
    }

    @RequestMapping(value = "/games/{gameid}/players/{playerid}", method = RequestMethod.PUT)
    public void joinGame(@PathVariable int gameid, @PathVariable String playerid) {
        Game game = gameList.getGame(gameid);
        if (game == null)
            throw new GameNotFoundException();
        Player player = playerController.getPlayer(playerid);
        if (player == null)
            throw new PlayerNotFoundException();

        game.addPlayer(player);
    }

    @RequestMapping(value = "/games/{gameid}/players/{playerid}/ready", method = RequestMethod.PUT)
    public void setReady(@PathVariable int gameid, @PathVariable String playerid) {
        Game game = gameList.getGame(gameid);
        if (game == null)
            throw new GameNotFoundException();
        Player player = playerController.getPlayer(playerid);
        if (player == null)
            throw new PlayerNotFoundException();

        game.setReady(player, true);
    }

    @RequestMapping(value = "/games/{gameid}/players/{playerid}/ready", method = RequestMethod.GET)
    public boolean getReady(@PathVariable int gameid, @PathVariable String playerid) {
        Game game = gameList.getGame(gameid);
        if (game == null)
            throw new GameNotFoundException();
        Player player = playerController.getPlayer(playerid);
        if (player == null)
            throw new PlayerNotFoundException();

        return game.getReady(player);
    }

    @RequestMapping(value = "/games/{gameid}/players/current")
    @ResponseStatus(HttpStatus.OK)
    public Player getCurrentPlayer(@PathVariable int gameid) {
        Game game = gameList.getGame(gameid);
        if(game == null) {
            throw new GameNotFoundException();
        }
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer == null) {
            throw new PlayerNotFoundException();
        }

        return currentPlayer;
    }
}
