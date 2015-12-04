package restopoly.accesslayer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import restopoly.dataaccesslayer.entities.Event;
import restopoly.dataaccesslayer.entities.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by octavian on 04.12.15.
 */
@RestController("/events")
public class EventsController {
    private RestTemplate restTemplate = new RestTemplate();
    private List<Event> events = new ArrayList<>();
    private List<Subscription> subscriptions = new ArrayList<>();

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String createEvent(Event event, @RequestParam String gameid) {
        events.add(event);

        // inform everyone. Who has subscribed to this Game/Event.type combo.
        for (Subscription subscription : subscriptions) {
            if (subscription.getGameid().equals(gameid) &&
                subscription.getEvent().getType().equals(event.getType())) {
                restTemplate.postForObject(subscription.getUri(), null, String.class);
            }
        }

        return "/events/" + (events.indexOf(event));
    }

    @RequestMapping(value = "/events/subscriptions", method = RequestMethod.POST)
    public void subscribe(Subscription subscription) {
        subscriptions.add(subscription);
    }
}
