package restopoly.games.accesslayer.games;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import restopoly.games.accesslayer.exceptions.GameNotFoundException;
import restopoly.games.accesslayer.exceptions.PlayerNotFoundException;
import restopoly.games.businesslogiclayer.GamesServiceBusinessLogic;
import restopoly.games.dataaccesslayer.entities.Game;
import restopoly.games.dataaccesslayer.entities.GameComponents;
import restopoly.games.dataaccesslayer.entities.GameList;
import restopoly.games.dataaccesslayer.entities.Player;
import restopoly.services.Services;
import restopoly.services.dataaccesslayer.entities.Service;

import java.util.List;

@RestController()
public class GamesController {
    private RestTemplate restTemplate = new RestTemplate();
    private GamesServiceBusinessLogic gamesServiceBusinessLogic = new GamesServiceBusinessLogic();

    @Value("${server.port}")
    int port;


    private GameList gameList = new GameList();

    @RequestMapping(value = "/games", method = RequestMethod.GET)
    public List<Game> getGames() {
        return gameList.getGames();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public Game createGame(@RequestBody GameComponents components) {
        String gameUrl = Services.hostAddress(port) + "/games/";
        Game game = gameList.addGame(gameUrl, components);

        // Wir registrieren das neu erstellte Game im Board.
        String boardPutUrl = components.getBoard() + "/" + game.getGameid();
        System.out.println("I will now register myself at the board: " + boardPutUrl);
        restTemplate.put(boardPutUrl, game);

        return game;
    }

    @RequestMapping(value = "/games/{gameid}/players", method = RequestMethod.GET)
    public List<Player> joinGame(@PathVariable String gameid) {
        Game game = gameList.getGame(gameid);
        if (game == null) {
            throw new GameNotFoundException();
        }

        return game.getPlayers();
    }

    @RequestMapping(value = "/games/{gameid}/players/{playerid}", method = RequestMethod.PUT)
    public void joinGame(@PathVariable String gameid, @PathVariable String playerid, @RequestParam String name, @RequestParam String uri) {
        Game game = gameList.getGame(gameid);
        if (game == null) {
            throw new GameNotFoundException();
        }

        Player player = new Player(playerid, name, uri);

        game.addPlayer(player);

        // Register player to the board.
        restTemplate.put(game.getComponents().getBoard() + "/" + game.getGameid() + "/players/" + playerid, player);
    }

    @RequestMapping(value = "/games/{gameid}/players/{playerid}/ready", method = RequestMethod.PUT)
    public void setReady(@PathVariable String gameid, @PathVariable String playerid) {
        Game game = gameList.getGame(gameid);

        if (game == null) {
            throw new GameNotFoundException();
        }

        Player player = gamesServiceBusinessLogic.getPlayer(game, playerid);

        if (player == null) {
            throw new PlayerNotFoundException();
        }

        gamesServiceBusinessLogic.setPlayerReady(game, player, true);
    }

    @RequestMapping(value = "/games/{gameid}/players/{playerid}/ready", method = RequestMethod.GET)
    public boolean getReady(@PathVariable String gameid, @PathVariable String playerid) {
        Game game = gameList.getGame(gameid);

        if (game == null) {
            throw new GameNotFoundException();
        }

        Player player = gamesServiceBusinessLogic.getPlayer(game, playerid);

        if (player == null) {
            throw new PlayerNotFoundException();
        }

        return gamesServiceBusinessLogic.getReadyPlayer(game, player);
    }

    @RequestMapping(value = "/games/{gameid}/players/current")
    @ResponseStatus(HttpStatus.OK)
    public Player getCurrentPlayer(@PathVariable String gameid) {
        Game game = gameList.getGame(gameid);
        if(game == null) {
            throw new GameNotFoundException();
        }
        Player currentPlayer = gamesServiceBusinessLogic.getCurrentPlayer(game);
        if (currentPlayer == null) {
            throw new PlayerNotFoundException();
        }

        return currentPlayer;
    }
}
