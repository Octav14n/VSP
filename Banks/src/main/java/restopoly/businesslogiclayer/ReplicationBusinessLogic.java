package restopoly.businesslogiclayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import restopoly.dataaccesslayer.entities.Bank;

import java.util.*;

/**
 * Created by octavian on 08.12.15.
 */
public class ReplicationBusinessLogic {
    static final int MILLISECONDS_TILL_NEXT_BULLY_CAN_START = 2500;
    @Autowired
    BanksServiceBusinessLogic banksLogic;
    List<String> otherServices = new ArrayList<>();
    int ourBullyIntensity;
    @Value("${server.port}")
        int port;

    RestTemplate template = new RestTemplate();

    public ReplicationBusinessLogic() {
        // Generate our bully intensity.
        ourBullyIntensity = (int)(Math.random() * Integer.MAX_VALUE);
        // We are free to being bullied right now.
        nextBullyTime = new Date();
    }

    // After nextBullyTime has passed we will interpret every access to /bully
    // as a new started bullying atempt.
    Date nextBullyTime;
    // Bullying value of our master.
    int masterBully;
    // Way to call our master.
    String masterUri;

    /**
     * Returns the Bank for the given game.
     * @param gameid GameID for which the game is requested.
     */
    public Bank getBank(String gameid) {
        return banksLogic.getBank(gameid);
    }

    /**
     * We are getting bullied by another banks-service. Handle this bully.
     * @param bullyIntensity How hard are we getting bullied (ID in Ksk. PDFs)
     * @param uri Uri of the Service that ist bullying us.
     * @param port Port of the Service that ist bullying us.
     * @return Returns our bully-intensity.
     */
    public int bully(int bullyIntensity, String uri, int port) {
        System.out.println("Bullying: we are Lvl: " + ourBullyIntensity + " the Enemy is Lvl: " + bullyIntensity);

        // Check if this is a new Bullying attempt.
        if (nextBullyTime.before(new Date())) {
            reBully();
        }

        // If this Bully hits harder than our old master we accept him as new master.
        if (bullyIntensity > masterBully) {
            masterUri = "http://" + uri + ":" + port + "/banks/replication";
            masterBully = bullyIntensity;
            System.out.println("We got a new Master: " + masterUri + " he is Lvl: " + masterBully);
        }
        return ourBullyIntensity;
    }

    /**
     * If we are getting unexpectidly bullied we will think that we are the master.
     * After this we will bully every other Service. If he is stronger than we,
     * we will accept him as our lord and saivior.
     */
    private void reBully() {
        System.out.println("This is a new Bully atempt. We are the master.");
        // Reset the "timeout" after which we will accept a new bully-attempt.
        nextBullyTime = new Date(new Date().getTime() + MILLISECONDS_TILL_NEXT_BULLY_CAN_START);

        masterBully = ourBullyIntensity;

        // Send the bully in every direction. We are bullying everyone. No exceptions.
        Map<String, Integer> responses = broadcastMessagePost("/bully?port=" + port + "&bullyIntensity=" + ourBullyIntensity, Integer.class);
        for (String key : responses.keySet()) {
            int responseBullyIntensity = responses.get(key);
            if (masterBully < responseBullyIntensity) {
                masterBully = responseBullyIntensity;
                masterUri = key;
                System.out.println("Our new master and saivior is: " + masterUri + " his Lvl: " + masterBully);
            }
        }
    }

    /**
     * This function sends POST message to every other banks-service that is registered by us.
     * The response will be put into a Map with the service-uri as key and the resulting Object as
     * value.
     * @param path Relative path we want to access. Has to start with "/".
     * @param responseObject Class we exect the request-answere to be an object of.
     * @param <T> same as "responseObject".
     * @return Returns a Map where "service" is the Key and the request-result is the value.
     */
    private <T> Map<String, T> broadcastMessagePost(String path, Class<T> responseObject) {
        Map<String, T> responses = new HashMap<>();
        for (String service : otherServices) {
            responses.put(service, template.postForObject(service + path, null, responseObject));
        }
        return responses;
    }

    /**
     * Will lock the given Bank.
     * @param bank bank to aquire a mutex of.
     * @throws InterruptedException
     */
    public void lockBankMutex(Bank bank) throws InterruptedException {
        banksLogic.lockBankMutex(bank);
    }

    /**
     * Will unlock the given Bank.
     * @param bank bank to release a mutex of.
     */
    public void unlockBankMutex(Bank bank) {
        banksLogic.unlockBankMutex(bank);
    }

    /**
     * Registers a new Service as a new replication-Service of our "Verbund"
     * // TODO: translate "Verbund" to english.
     * @param hostname Hostname or IP where to find the new service.
     * @param port Port under which to access the new service.
     * @return Returns a list of all services currently registered at our service.
     *         (Without the newly added service)
     */
    public List<String> registerReplicationService(String hostname, int port) {
        hostname = makeUrl(hostname, port);
        System.out.println("Registriere neuen Banks-Service: " + hostname);
        // temporarily save a list without the soon-to-be-added service.
        List<String> retServices = new ArrayList<>(otherServices);

        otherServices.add(hostname);
        return retServices;
    }

    /**
     * This function will register this service at an other one.
     * @param url URL where to register this service at.
     */
    public void registerMeAt(String url) {
        System.out.println("Will now register myself at: " + url);
        String[] otherServices = template.postForObject(url + "/register?port=" + port, null, String[].class);
        // Jetzt registrieren wir uns noch an allen anderen Services, die bekannt sind.
        this.otherServices.add(url);
        for (String service : otherServices) {
            if (this.otherServices.contains(service)) {
                continue;
            }
            this.otherServices.add(service);
            registerMeAt(service);
        }
    }

    private String makeUrl(String hostname, int port) {
        return "http://" + hostname + ":" + port + "/banks/replication";
    }
}
