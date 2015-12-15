package restopoly.accesslayer.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import restopoly.dataaccesslayer.entities.Event;
import restopoly.dataaccesslayer.entities.Player;

/**
 * Created by octavian on 27.10.15.
 */
@RestController
@EnableWebSocketMessageBroker
public class PlayerController extends AbstractWebSocketMessageBrokerConfigurer {
    @Autowired
    private SimpMessagingTemplate template;

    private Player player;

    @RequestMapping(value = "/player", method = RequestMethod.GET)
    public Player getPlayer() {
        return player;
    }

    @RequestMapping(value = "/player/turn", method = RequestMethod.POST)
    public void informPlayerOfTurn() {
        template.convertAndSend("/intern/turn", "");
    }

    @RequestMapping(value = "/player/event", method = RequestMethod.POST)
    public void informPlayerOfEvent(@RequestBody Event event) {
        template.convertAndSend("/intern/event", event);
    }

    @Override public void configureMessageBroker (MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/intern");
    }

    @Override public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket").withSockJS();
    }
}
