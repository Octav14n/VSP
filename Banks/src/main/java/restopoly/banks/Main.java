package restopoly.banks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import restopoly.banks.businesslogiclayer.ReplicationBusinessLogic;
import restopoly.services.dataaccesslayer.entities.Service;
import restopoly.services.Services;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by octavian on 13.10.15.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableSwagger2
//@RestController
public class Main {
    @Autowired
    ReplicationBusinessLogic replication;
    @Value("${server.port}")
    int port;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        registerMe();
    }

    //@RequestMapping(value="/registerme", method = RequestMethod.GET)
    public String registerMe() {
        System.out.println("Ich werde jetzt die Registrierungen durchführen.");
        Services s = new Services(new Service("Bank service. Patrick & Simon.", "bank"), port);
        String out = s.register();

        if (4567 != port) {
            // Der 1. darf sich nicht bei sich selber registrieren.
            replication.registerMeAt("http://127.0.0.1:4567/banks/replication");
        }
        return "Registrierungsprozess durchgefuehrt: " + out;
    }
}
