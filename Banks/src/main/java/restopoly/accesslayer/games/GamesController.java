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
    private Bank bank;
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

//    /**
//     * Create the one and only bank for a single game.
//     *
//     * @param gameid    for the game with this ID there will be created the bank.
//     * @param bankId    this is the bank identifier for this game.
//     * @param name      this is the name of the bank which is created for a game.
//     * @param bankCode  the bankCode is a special code which is set to every bank.
//     */
//    @RequestMapping(value = "/games/{gameid}/banks", method = RequestMethod.POST)
//    @ResponseStatus(HttpStatus.CREATED)
//    public Bank createGameBank(@PathVariable int gameid, int bankId, String name, int bankCode) {
//        Game game = gameList.getGame(gameid);
//            if (game.getGameid() == gameid) {
//                if (bank == null) {
//                    bank = new Bank(bankId, name, bankCode);
//                    game.setBank(bank);
//                }
//            }
//        return game.getBank();
//    }

    /**
     * Get the bank for a selected game with the gameid of that game.
     *
     * @param gameid  this is the gameid for a game.
     * @return        the bank for the selected game with this gameid.
     */
    @RequestMapping(value = "/games/{gameid}/banks")
    @ResponseStatus(HttpStatus.OK)
    public Bank getGameBank(@PathVariable int gameid) {
        Game game = gameList.getGame(gameid);
        return game.getBank();
    }
}
