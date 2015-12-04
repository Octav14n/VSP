package restopoly.accesslayer.banks;

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
import restopoly.Main;
import restopoly.dataaccesslayer.entities.*;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

/**
 * Created by octavian on 16.11.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Main.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4
public class BanksControllerTest {
    @Value("${local.server.port}")   // 6
        int port;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testAll() {

        Player playerSimon = new Player("simon", "Simon der Grune", "http://125.0.0.1");
        Player playerPatrick = new Player("patrick", "Patrick der Linke", "http://1.0.0.125");
        Game game = new Game("0");
        BankAccount bankAccountSimon = new BankAccount(playerSimon, 0);
        BankAccount bankAccountPatrick = new BankAccount(playerPatrick,  42);

        // POST --> Creates a bank account for one specific game / user combo.
        given()
            .contentType(ContentType.JSON)
            .body(bankAccountSimon)
            .when().post("/banks/" + game.getGameid() + "/players").then()
            .statusCode(HttpStatus.CREATED.value());

        // GET --> GET one player with a banking account for one game.
        given().when()
            .get("/banks/" + game.getGameid() + "/players").then()
            .statusCode(HttpStatus.OK.value())
            .body("player[0].id", Matchers.equalTo(playerSimon.getId()))
            .body("saldo[0]", Matchers.equalTo(bankAccountSimon.getSaldo()));

        // POST --> Can't create the same account twice.
        given()
            .contentType(ContentType.JSON)
            .body(bankAccountSimon)
            .when().post("/banks/" + game.getGameid() + "/players").then()
            .statusCode(HttpStatus.CONFLICT.value());

        // POST --> Add money (2000) to my bank account (from the bank).
        given()
            .body("Schweigen der Loge der Maurer.")
            .post("/banks/" + game.getGameid() + "/transfer/to/" + playerSimon.getId() + "/2000").then()
            .statusCode(HttpStatus.CREATED.value());

        // GET --> GET one player with a banking account for one game.
        given().when()
            .get("/banks/" + game.getGameid() + "/players").then()
            .statusCode(HttpStatus.OK.value())
            .body("player[0].id", Matchers.equalTo(playerSimon.getId()))
            .body("saldo[0]", Matchers.equalTo(2000));

        // POST --> Add money (2000) to my bank account (from the bank).
        given()
            .body("Schmiergelder, vermoegenswirksame Leistungen.")
            .post("/banks/" + game.getGameid() + "/transfer/from/" + playerSimon.getId() + "/1000").then()
            .statusCode(HttpStatus.CREATED.value());

        // GET --> GET one player with a banking account for one game.
        given().when()
            .get("/banks/" + game.getGameid() + "/players").then()
            .statusCode(HttpStatus.OK.value())
            .body("player[0].id", Matchers.equalTo(playerSimon.getId()))
            .body("saldo[0]", Matchers.equalTo(1000));

        // POST --> Add money (2000) to my bank account (from the bank).
        given()
            .body("Ich kauf mir die Welt.")
            .post("/banks/" + game.getGameid() + "/transfer/from/" + playerSimon.getId() + "/2000").then()
            .statusCode(HttpStatus.FORBIDDEN.value());


        // POST --> Creates a bank account for one specific game / user combo.
        given()
            .contentType(ContentType.JSON)
            .body(bankAccountPatrick)
            .when().post("/banks/" + game.getGameid() + "/players").then()
            .statusCode(HttpStatus.CREATED.value());

        // POST --> POST money to another players banking account.
        given()
            .body("Hier armer Schlucker.")
            .post("/banks/" + game.getGameid() + "/transfer/from/" + playerSimon.getId() + "/to/" + playerPatrick.getId() + "/100").then()
            .statusCode(HttpStatus.CREATED.value());

        // GET --> GET one player with a banking account for one game.
        given().when()
            .get("/banks/" + game.getGameid() + "/players").then()
            .statusCode(HttpStatus.OK.value())
            .body("player[0].id", Matchers.equalTo(playerSimon.getId()))
            .body("saldo[0]", Matchers.equalTo(900));

        // GET --> GET one player with a banking account for one game.
        given().when()
            .get("/banks/" + game.getGameid() + "/players").then()
            .statusCode(HttpStatus.OK.value())
            .body("player[1].id", Matchers.equalTo(playerPatrick.getId()))
            .body("saldo[1]", Matchers.equalTo(142));

        // POST --> POST money to another players banking account.
        // This should run into an Exception and the sados should be as they were before transaction.
        System.out.println("\nThe next RuntimeException is thrown to test transactions! Don't respect it.\n");
        given()
            .body("Supercalifragilisticexpialigetisch")
            .post("/banks/" + game.getGameid() + "/transfer/from/" + playerPatrick.getId() + "/to/" + playerSimon.getId() + "/100").then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        // Now check both accounts if the saldo equals the saldo before the transaction.
        // GET --> GET one player with a banking account for one game.
        given().when()
            .get("/banks/" + game.getGameid() + "/players").then()
            .statusCode(HttpStatus.OK.value())
            .body("player[0].id", Matchers.equalTo(playerSimon.getId()))
            .body("saldo[0]", Matchers.equalTo(900));

        // GET --> GET one player with a banking account for one game.
        given().when()
            .get("/banks/" + game.getGameid() + "/players").then()
            .statusCode(HttpStatus.OK.value())
            .body("player[1].id", Matchers.equalTo(playerPatrick.getId()))
            .body("saldo[1]", Matchers.equalTo(142));

    }
}
