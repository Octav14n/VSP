package restopoly.games;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import restopoly.services.dataaccesslayer.entities.Service;
import restopoly.services.Services;


/**
 * Created by octavian on 13.10.15.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        Services
            s = new Services(new Service("Super toller Games Service.", "Hier gibt es frische Kekse", "games", "http://127.0.0.1/"));
        //s.register();
    }
}
