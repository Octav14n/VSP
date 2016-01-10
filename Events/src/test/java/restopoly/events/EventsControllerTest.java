package restopoly.events;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import restopoly.events.dataaccesslayer.entities.Event;
import restopoly.events.dataaccesslayer.entities.Player;
import restopoly.events.dataaccesslayer.entities.Subscription;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by octavian on 04.12.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Main.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4
public class EventsControllerTest {
    @Value("${local.server.port}")   // 6
        int port;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testAll() {

        String theBestGameYouHaveEverPlayed = "B180";
        Player playerRayer = new Player("hugendubel", "almoedi", "Ente");
        Event whatANiceEvent = new Event("a", "b", "c", "d", playerRayer);
        Subscription sub = new Subscription(theBestGameYouHaveEverPlayed, "test", whatANiceEvent);

        given().contentType(ContentType.JSON)
                .body(whatANiceEvent)
                .body(theBestGameYouHaveEverPlayed)
                .queryParam("gameid", theBestGameYouHaveEverPlayed)
                .when().post("/events").then()
                .statusCode(HttpStatus.CREATED.value());

        given().contentType(ContentType.JSON)
                .body(sub)
                .when().post("/events/subscriptions").then()
                .statusCode(HttpStatus.CREATED.value());
    }
}
