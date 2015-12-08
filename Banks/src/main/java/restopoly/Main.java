package restopoly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import restopoly.businesslogiclayer.BanksServiceBusinessLogic;
import restopoly.businesslogiclayer.ReplicationBusinessLogic;
import restopoly.dataaccesslayer.entities.Service;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by octavian on 13.10.15.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableSwagger2
public class Main {
    @Autowired
    ReplicationBusinessLogic replication;
    @Value("${server.port}")
    String port;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    @Bean
    public ReplicationBusinessLogic getReplicationBusinessLogic() {
        return new ReplicationBusinessLogic();
    }

    @Bean
    public BanksServiceBusinessLogic getBanksServiceBusinessLogic() {
        return new BanksServiceBusinessLogic();
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        Services s = new Services(new Service("Super toller Banks Service.", "Hier gibt es frische Kekse", "banks", "http://127.0.0.1"));
        //s.register();

        if (!"4567".equals(port)) {
            // Der 1. darf sich nicht bei sich selber registrieren.
            replication.registerMeAt("http://127.0.0.1:4567/banks/replication");
        }
    }
}
