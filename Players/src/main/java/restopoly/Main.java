package restopoly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import restopoly.dataaccesslayer.entities.Service;

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
        Services s = new Services(new Service("events service. Patrick & Simon.", "events"), 13402);
        s.register();
    }
}
