package restopoly.businesslogiclayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import restopoly.dataaccesslayer.entities.Bank;

import java.util.*;

/**
 * Created by octavian on 08.12.15.
 */
public class ReplicationBusinessLogic {
    static final int MILLISECONDS_TILL_NEXT_BULLY_CAN_START = 700;
    @Autowired
    BanksServiceBusinessLogic banksLogic;
    List<String> otherServices = new ArrayList<>();
    int ourBullyIntensity;
    @Value("${server.port}")
        int port;

    RestTemplate template = new RestTemplate();

    public ReplicationBusinessLogic() {
        ourBullyIntensity = (int)(Math.random() * Integer.MAX_VALUE);
        nextBullyTime = new Date();
    }

    Date nextBullyTime;
    int masterBully;
    String masterUri;

    public Bank getBank(String gameid) {
        return banksLogic.getBank(gameid);
    }

    public int bully(int bullyIntensity, String uri, int port) {
        System.out.println("Bullying: we are Lvl: " + ourBullyIntensity + " the Enemy is Lvl: " + bullyIntensity);
        if (nextBullyTime.before(new Date())) {
            reBully();
        }

        if (bullyIntensity > masterBully) {
            masterUri = "http://" + uri + ":" + port + "/banks/replication";
        }
        return ourBullyIntensity;
    }

    private void reBully() {
        System.out.println("This is a new Bully atempt. We are the master.");
        nextBullyTime = new Date(new Date().getTime() + MILLISECONDS_TILL_NEXT_BULLY_CAN_START);

        masterBully = ourBullyIntensity;
        Map<String, String> responses = broadcastMessagePost("/bully?port=" + port + "&bullyIntensity=" + ourBullyIntensity);
        for (String key : responses.keySet()) {
            int responseBullyIntensity = Integer.parseInt(responses.get(key));
            if (masterBully < responseBullyIntensity) {
                masterBully = responseBullyIntensity;
                masterUri = key;
                System.out.println("Unser neuer Master und Herscher ist: " + masterUri + " mit Lvl: " + masterBully);
            }
        }
    }

    private Map<String, String> broadcastMessagePost(String path) {
        Map<String, String> responses = new HashMap<>();
        for (String service : otherServices) {
            responses.put(service, template.postForObject(service + path, null, String.class));
        }
        return responses;
    }

    public void lockBankMutex(Bank bank) throws InterruptedException {
        banksLogic.lockBankMutex(bank);
    }

    public void unlockBankMutex(Bank bank) {
        banksLogic.unlockBankMutex(bank);
    }

    public List<String> registerReplicationService(String url, int port) {
        url = "http://" + url + ":" + port + "/banks/replication";
        System.out.println("Registriere neuen Banks-Service: " + url);
        otherServices.add(url);

        List<String> retServices = new ArrayList<>(otherServices);
        retServices.remove(url);
        return retServices;
    }

    public void registerMeAt(String url) {
        System.out.println("Will now register myself at: " + url);
        String[] otherServices = template.postForObject(url + "/register?port=" + port, null, String[].class);
        // Jetzt registrieren wir uns noch an allen anderen Services, die bekannt sind.
        for (String service : otherServices) {
            if (this.otherServices.contains(service)) {
                continue;
            }
            this.otherServices.add(service);
            registerMeAt(service);
        }
    }
}
