package restopoly.accesslayer;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import restopoly.Main;
import restopoly.dataaccesslayer.entities.Event;
import restopoly.dataaccesslayer.entities.Player;
import restopoly.Services;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

/**
 * Created by octavian on 04.12.15.
 */
/*@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Main.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4*/
public class EventsControllerTest {
    //@Value("${local.server.port}")   // 6
        int port = 4567;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testPlayerTurn() {
        // POST - Inform user that it is his turn.
        given().when()
            .post("/player/turn").then()
            .statusCode(Matchers.greaterThanOrEqualTo(200))
            .statusCode(Matchers.lessThan(300));
    }

    @Test
    public void testPlayerEvent() {
        Player player = new Player("simon", "Simon der gruene", Services.uri());
        Event event = new Event("kick", "kick", "You are green...", Services.uri(), player);

        // POST - Inform user a new Event.
        given().when()
            .contentType(ContentType.JSON)
            .body(event)
            .post("/player/event").then()
            .statusCode(Matchers.greaterThanOrEqualTo(200))
            .statusCode(Matchers.lessThan(300));
    }
}
