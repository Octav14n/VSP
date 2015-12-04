package restopoly.dataaccesslayer.entities;

/**
 * Created by octavian on 04.12.15.
 */
public class Subscription {
    String gameid;
    String uri;
    Event event;

    public Subscription(String gameid, String uri, Event event) {
        this.gameid = gameid;
        this.uri = uri;
        this.event = event;
    }

    public String getGameid() {
        return gameid;
    }

    public String getUri() {
        return uri;
    }

    public Event getEvent() {
        return event;
    }
}
