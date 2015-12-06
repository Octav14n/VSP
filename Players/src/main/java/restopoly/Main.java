package restopoly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import restopoly.entities.Service;

import java.io.File;
import java.io.IOException;

/**
 * Created by octavian on 13.10.15.
 */
@Configuration
@ComponentScan({"restopoly.accesslayer.player"})
@EnableAutoConfiguration
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        Services s = new Services(
            new Service(
                "Super toller Events Service.",
                "Hier gibt es frische Kekse",
                "events",
                Services.uri()
            )
        );
        //s.register();
    }

}
