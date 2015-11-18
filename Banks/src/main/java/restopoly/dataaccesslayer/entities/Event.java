package restopoly.dataaccesslayer.entities;

/**
 * Created by octavian on 16.11.15.
 */
public class Event {
    String type; // internal type of the event (e.g bank transfer, rent, got to jail, estate transfer)
    String name; // human readable name for this event
    String reason; // a description why this event occured
    String resource; // the uri of the resource related to this event
    GamePlayer gamePlayer; // The player issued this event

    public Event(String type, String name, String reason, String resource, GamePlayer gamePlayer) {
        this.type = type;
        this.name = name;
        this.reason = reason;
        this.resource = resource;
        this.gamePlayer = gamePlayer;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getReason() {
        return reason;
    }

    public String getResource() {
        return resource;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }
}
