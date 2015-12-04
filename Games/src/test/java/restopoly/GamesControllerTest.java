package restopoly;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import restopoly.dataaccesslayer.entities.Place;
import restopoly.dataaccesslayer.entities.Player;

import java.util.Date;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by octavian on 11.11.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Main.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4
public class GamesControllerTest {
    @Value("${local.server.port}")   // 6
    int port;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testall() {
        // GET Games --> Empty Array
        given().when()
            .get("/games").then()
            .statusCode(HttpStatus.OK.value())
            .body("isEmpty()", Matchers.is(true));

        // GET Jail --> Empty Array
        given().when()
            .get("/jail").then()
            .statusCode(HttpStatus.OK.value())
            .body("", Matchers.hasSize(0));

        Player player = new Player("simon", "Simon der Grune (um " + new Date().toString() + " Uhr)", "http://256.0.0.1/");

        // POST Games --> Object with game. // Creates a Game.
        String gameId =
            given().when()
                .post("/games").then()
                .statusCode(HttpStatus.CREATED.value())
                .body("players", Matchers.hasSize(0))
                .extract().path("gameid");

        // GET Games --> Array with one game and no players.
        given().when()
            .get("/games").then()
            .statusCode(HttpStatus.OK.value())
            .body("", Matchers.hasSize(1))
            .body("gameid[0]", Matchers.equalTo(gameId))
            .body("players[0]", Matchers.hasSize(0));

        // POST Players --> Player Object. // Joins the game.
        given().when()
            .pathParam("gameId", gameId)
            .pathParam("playerId", player.getId())
            .queryParam("name", player.getName())
            .queryParam("uri", player.getUri())
            .put("/games/{gameId}/players/{playerId}").then()
            .statusCode(Matchers.greaterThanOrEqualTo(200)) // Is it somewhere in the "OK" area.
            .statusCode(Matchers.lessThan(300));

        // GET Players --> Array with simon.
        given().when()
            .pathParam("gameId", gameId)
            .get("/games/{gameId}/players").then()
            .statusCode(HttpStatus.OK.value())
            .body("", Matchers.hasSize(1))
            .body("id[0]", Matchers.equalTo(player.getId())) // "Feld[0]" entspricht dem Feld aus dem ersten Eintrag des Arrays.
            .body("name[0]", Matchers.equalTo(player.getName()))
            .body("uri[0]", Matchers.equalTo(player.getUri()));

        // GET Games --> Array with one game.
        given().when()
            .get("/games").then()
            .statusCode(HttpStatus.OK.value())
            .body("", Matchers.hasSize(1))
            .body("gameid[0]", Matchers.equalTo(gameId))
            .body("players[0][0].id", Matchers.equalTo(player.getId()));

        // GET Ready
        given().when()
            .pathParam("gameId", gameId)
            .pathParam("playerId", player.getId())
            .get("/games/{gameId}/players/{playerId}/ready").then()
            .statusCode(HttpStatus.OK.value())
            .body(Matchers.equalTo("false"));

        // GET CurrentPlayer --> HttpStatus 404
        given().when()
            .pathParam("gameId", gameId)
            .get("/games/{gameId}/players/current").then()
            .statusCode(HttpStatus.NOT_FOUND.value());

        // PUT Ready --> Void // Setzt den Spieler ready.
        given().when()
            .pathParam("gameId", gameId)
            .pathParam("playerId", player.getId())
            .put("/games/{gameId}/players/{playerId}/ready").then()
            .statusCode(HttpStatus.OK.value())
            .body(Matchers.isEmptyOrNullString());

        // GET Ready
        given().when()
            .pathParam("gameId", gameId)
            .pathParam("playerId", player.getId())
            .get("/games/{gameId}/players/{playerId}/ready").then()
            .statusCode(HttpStatus.OK.value())
            .body(Matchers.equalTo("true"));

        // GET CurrentPlayer --> Player simon.
        given().when()
            .pathParam("gameId", gameId)
            .get("/games/{gameId}/players/current").then()
            .statusCode(HttpStatus.OK.value())
            .body("id", Matchers.equalTo(player.getId()))
            .body("name", Matchers.equalTo(player.getName()));
    }
}
