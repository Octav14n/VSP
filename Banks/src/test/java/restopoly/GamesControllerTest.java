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
import restopoly.dataaccesslayer.entities.Bank;
import restopoly.dataaccesslayer.entities.BankAccount;
import restopoly.dataaccesslayer.entities.Place;
import restopoly.dataaccesslayer.entities.Player;

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

        // GET Players --> Empty Array
        given().when()
            .get("/players").then()
            .statusCode(HttpStatus.OK.value())
            .body("isEmpty()", Matchers.is(true));

        // GET Jail --> Empty Array
        given().when()
            .get("/jail").then()
            .statusCode(HttpStatus.OK.value())
            .body("", Matchers.hasSize(0));

        Player player = new Player("simon", "Simon der Grune", new Place("Wonderland"));

        // POST Players --> Player Object. // Creates a Player.
        given().when()
            .contentType(ContentType.URLENC)
            .param("playerId", "simon")
            .param("name", "Simon der Grune")
            .param("place", "Wonderland")
            .post("/players").then()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", Matchers.equalTo(player.getId()))
            .body("name", Matchers.equalTo(player.getName()))
            .body("place.name", Matchers.equalTo(player.getPlace().getName()));

        // GET Players --> Array with simon.
        given().when()
            .get("/players").then()
            .statusCode(HttpStatus.OK.value())
            .body("", Matchers.hasSize(1))
            .body("id[0]", Matchers.equalTo(player.getId())) // "Feld[0]" entspricht dem Feld aus dem ersten Eintrag des Arrays.
            .body("name[0]", Matchers.equalTo(player.getName()))
            .body("place[0].name", Matchers.equalTo(player.getPlace().getName()));

        // POST Games --> Object with game. // Creates a Game.
        given().when()
            .post("/games").then()
            .statusCode(HttpStatus.CREATED.value())
            .body("gameid", Matchers.equalTo(0))
            .body("players", Matchers.hasSize(0));

        // GET Games --> Array with one game.
        given().when()
            .get("/games").then()
            .statusCode(HttpStatus.OK.value())
            .body("", Matchers.hasSize(1))
            .body("gameid[0]", Matchers.equalTo(0))
            .body("players[0]", Matchers.hasSize(0));

        // PUT Game --> Void // Joins the Game.
        given().when()
            .put("/games/0/players/simon").then()
            .statusCode(HttpStatus.OK.value())
            .body(Matchers.isEmptyOrNullString());

        // GET Games --> Array with one game.
        given().when()
            .get("/games").then()
            .statusCode(HttpStatus.OK.value())
            .body("", Matchers.hasSize(1))
            .body("gameid[0]", Matchers.equalTo(0))
            .body("players[0][0].id", Matchers.equalTo(player.getId()));

        // GET Ready
        given().when()
            .get("/games/0/players/simon/ready").then()
            .statusCode(HttpStatus.OK.value())
            .body(Matchers.equalTo("false"));

        // GET CurrentPlayer --> HttpStatus 404
        given().when()
            .get("/games/0/players/current").then()
            .statusCode(HttpStatus.NOT_FOUND.value());

        // PUT Ready --> Void // Setzt den Spieler ready.
        given().when()
            .put("/games/0/players/simon/ready").then()
            .statusCode(HttpStatus.OK.value())
            .body(Matchers.isEmptyOrNullString());

        // GET Ready
        given().when()
            .get("/games/0/players/simon/ready").then()
            .statusCode(HttpStatus.OK.value())
            .body(Matchers.equalTo("true"));

        // GET CurrentPlayer --> Player simon.
        given().when()
            .get("/games/0/players/current").then()
            .statusCode(HttpStatus.OK.value())
            .body("id", Matchers.equalTo(player.getId()))
            .body("name", Matchers.equalTo(player.getName()))
            .body("place.name", Matchers.equalTo(player.getPlace().getName()));
    }
}
