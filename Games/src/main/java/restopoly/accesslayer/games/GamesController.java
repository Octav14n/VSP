package restopoly.accesslayer.games;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import restopoly.accesslayer.exceptions.GameNotFoundException;
import restopoly.accesslayer.exceptions.PlayerNotFoundException;
import restopoly.dataaccesslayer.entities.*;

import java.util.List;

@RestController()
public class GamesController {
    private RestTemplate restTemplate = new RestTemplate();
    private String boardUri = "https://vs-docker.informatik.haw-hamburg.de/ports/18193/boards/";

    private GameList gameList = new GameList();
    //private PlayerList playerList = new PlayerList();

    @RequestMapping(value = "/games", method = RequestMethod.GET)
    public List<Game> getGames() {
        return gameList.getGames();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public Game createGame() {
        Game game = gameList.addGame();

        // Wir registrieren das neu erstellte Game im Board.
        restTemplate.put(boardUri + game.getGameid(), game);

        return game;
    }

    @RequestMapping(value = "/games/{gameid}/players", method = RequestMethod.GET)
    public List<Player> joinGame(@PathVariable String gameid) {
        Game game = gameList.getGame(gameid);
        if (game == null)
            throw new GameNotFoundException();

        return game.getPlayers();
    }

    @RequestMapping(value = "/games/{gameid}/players/{playerid}", method = RequestMethod.PUT)
    public void joinGame(@PathVariable String gameid, @PathVariable String playerid, @RequestParam String name, @RequestParam String uri) {
        Game game = gameList.getGame(gameid);
        if (game == null)
            throw new GameNotFoundException();

        Player player = new Player(playerid, name, uri);

        game.addPlayer(player);

        // Register player to the board.
        restTemplate.put(boardUri + game.getGameid() + "/players/" + playerid, player);
    }

    @RequestMapping(value = "/games/{gameid}/players/{playerid}/ready", method = RequestMethod.PUT)
    public void setReady(@PathVariable String gameid, @PathVariable String playerid) {
        Game game = gameList.getGame(gameid);
        if (game == null)
            throw new GameNotFoundException();
        Player player = game.getPlayer(playerid);
        if (player == null)
            throw new PlayerNotFoundException();

        game.setReady(player, true);
    }

    @RequestMapping(value = "/games/{gameid}/players/{playerid}/ready", method = RequestMethod.GET)
    public boolean getReady(@PathVariable String gameid, @PathVariable String playerid) {
        Game game = gameList.getGame(gameid);
        if (game == null)
            throw new GameNotFoundException();
        Player player = game.getPlayer(playerid);
        if (player == null)
            throw new PlayerNotFoundException();

        return game.getReady(player);
    }

    @RequestMapping(value = "/games/{gameid}/players/current")
    @ResponseStatus(HttpStatus.OK)
    public Player getCurrentPlayer(@PathVariable String gameid) {
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
