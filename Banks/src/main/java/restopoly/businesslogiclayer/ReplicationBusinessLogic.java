package restopoly.businesslogiclayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import restopoly.dataaccesslayer.entities.Bank;
import restopoly.dataaccesslayer.entities.BankAccount;
import restopoly.dataaccesslayer.entities.Event;

import java.util.*;

/**
 * Created by octavian on 08.12.15.
 */
@Component
public class ReplicationBusinessLogic {
    static final int MILLISECONDS_TILL_NEXT_BULLY_CAN_START = 2500;
    @Autowired
    BanksServiceBusinessLogic banksLogic;
    List<String> otherServices = new ArrayList<>();
    @Value("${bully.intensity}")
    int ourBullyIntensity;
    @Value("${server.port}")
        int port;

    RestTemplate template = new RestTemplate();

    public ReplicationBusinessLogic() {
        // Generate our bully intensity.
        if (0 == ourBullyIntensity) {
            ourBullyIntensity = (int) (Math.random() * Integer.MAX_VALUE);
        }
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
     * Creates a Bank for a given game.
     * If we are the Master this bank will be created on all Services.
     * @param gameid
     */
    public void createBank(String gameid) {
        System.out.println("Erstelle Bank fuer Spiel " + gameid + ".");
        banksLogic.createBank(gameid);
        if (this.isMaster()) {
            System.out.println("Informiere andere ueber erstellung von Spiel " + gameid + ".");
            this.broadcastMessagePost("/{gameid}", null, String.class, gameid);
        }
    }

    /**
     * Creates a BankAccount for a given game.
     * If we are the Master this bankaccount will be created on all Services.
     * @param gameid
     * @param bank
     * @param bankAccount
     */
    public void createBankAccount(String gameid, Bank bank, BankAccount bankAccount) {
        System.out.println("Erstelle BankAccount " + bankAccount.getPlayer().getId() + " fuer Spiel " + gameid + ".");
        bank.addBankAccount(bankAccount);
        if (this.isMaster()) {
            System.out.println("Informiere ueber BankAccount " + bankAccount.getPlayer().getId() + " fuer Spiel " + gameid + ".");
            this.broadcastMessagePost("/{gameid}/players", bankAccount, String.class, gameid);
        }
    }

    public synchronized List<Event> createBankTransfer(String gameid, BankAccount from, BankAccount to, int amount, String reason) {
        List<Event> eventList = new ArrayList<>();
        banksLogic.transferMoney(gameid, from, to, amount, reason);
        if (this.isMaster()) {
            Map<String, Event[]> events = this.broadcastMessagePost("/{gameid}/transfer/from/{from}/to/{to}/{amount}", reason, Event[].class, gameid, from, to, amount);
            for (Event[] event : events.values()) {
                eventList.addAll(Arrays.asList(event));
            }
        }
        return eventList;
    }

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
     * @param host Hostname or IP Address of the Service that ist bullying us.
     * @param port Port of the Service that ist bullying us.
     * @return Returns our bully-intensity.
     */
    public int bully(int bullyIntensity, String host, int port) {
        System.out.println("Bullying: we are Lvl: " + ourBullyIntensity + " the Enemy is Lvl: " + bullyIntensity);

        // Check if this is a new Bullying attempt.
        if (nextBullyTime.before(new Date())) {
            reBully();
        }

        // If this Bully hits harder than our old master we accept him as new master.
        if (bullyIntensity > masterBully) {
            masterUri = makeUrl(host, port);
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
        masterUri = null;

        // Send the bully in every direction. We are bullying everyone. No exceptions.
        Map<String, Integer> responses = broadcastMessagePost("/bully?port={port}&bullyIntensity={ourBullyIntensity}", null, Integer.class, port, ourBullyIntensity);
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
     * @param bodyObject Object that will be jsonified in the body.
     * @param responseObject Class we exect the request-answere to be an object of.
     * @param <T> same as "responseObject".
     * @return Returns a Map where "service" is the Key and the request-result is the value.
     */
    public <T> Map<String, T> broadcastMessagePost(String path, Object bodyObject, Class<T> responseObject, Object... uriVariables) {
        Map<String, T> responses = new HashMap<>();
        for (String service : otherServices) {
            System.out.println("Informiere Service " + service + " mit path " + path + ".");
            try {
                responses.put(service, template
                    .postForObject(service + path, bodyObject, responseObject, uriVariables));
            } catch (Exception e) {
                System.out.println("Couldn't connect to: " + service);
            }
        }
        return responses;
    }

    /**
     * This function sends POST message to the Master-Service or if this is the master:
     * it will send to every other banks-service that is registered by us.
     * @param path Relative path we want to access. Has to start with "/".
     */
    public void sendToMaster(String path, Object requestObject, Object... uriVariables) {
        if (null == masterUri) {
            throw new RuntimeException("Master triest to send something to Master: " + path);
        }
        boolean isSend = false;
        while (!isSend) {
            try {
                template.postForObject(masterUri + path, requestObject, String.class, uriVariables);
                isSend = true;
            } catch (Exception e) {
                System.out.println("Tried to send to Master: " + masterUri + "/" + path + " but failed. Will now reBully.");
                reBully();
                if (isMaster()) {
                    template.postForObject(thisService() + path, requestObject, String.class, uriVariables);
                    isSend = true;
                }
            }
        }
    }

    public boolean isMaster() {
        return (null == masterUri);
    }

    /**
     * Will lock the given Bank.
     * @param gameid game of whichs bank to aquire a mutex of.
     * @throws InterruptedException
     */
    public void lockBankMutex(String gameid) throws InterruptedException {
        lockBankMutex(gameid, "us self", 0);
    }
    public void lockBankMutex(String gameid, String host, int port) throws InterruptedException {
        if (null == masterUri) {
            System.out.println("Request:  Locked the Mutex for " + makeUrl(host, port));
            banksLogic.getBank(gameid).muhtex.acquire();
            System.out.println("Grant:    Locked the Mutex for " + makeUrl(host, port));
        } else {
            template.postForObject(masterUri + "/{gameid}/lock?myPort={port}", null, String.class, gameid, port);
        }
    }

    /**
     * Will unlock the given Bank.
     * @param gameid game of whichs bank bank to release a mutex of.
     */
    public void unlockBankMutex(String gameid) {
        unlockBankMutex(gameid, "us self", 0);
    }
    public void unlockBankMutex(String gameid, String host, int port) {
        if (null == masterUri) {
            System.out.println("Request: UnLocked the Mutex by " + makeUrl(host, port));
            banksLogic.getBank(gameid).muhtex.release();
            System.out.println("Grant:   UnLocked the Mutex by " + makeUrl(host, port));
        } else {
            template.delete(masterUri + "/{gameid}/lock?myPort={port}", gameid, port);
        }
    }

    /**
     * Requests wether the bank of the given game is currently unlocked.
     * @param gameid Game of which you wish to know the mutex status from.
     * @return true, if the mutex is not locked.
     */
    public boolean isUnlockedBankMutex(String gameid) {
        if (null == masterUri) {
            try {
                return banksLogic.getBank(gameid).muhtex.attempt(0);
            } catch (InterruptedException e) {
                return false;
            }
        } else {
            return template.getForObject(masterUri + "/{gameid}/lock", Boolean.class, gameid);
        }
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

    public boolean isRegistered(String hostname, int port) {
        hostname = makeUrl(hostname, port);
        return otherServices.contains(hostname);
    }

    /**
     * This function will register this service at an other one.
     * @param url URL where to register this service at.
     */
    public void registerMeAt(String url) {
        registerMeAt(url, true);
    }
    private void registerMeAt(String url, boolean setMaster) {
        System.out.println("Will now register myself at: " + url);
        if (setMaster) {
            setMaster(url);
        }
        String[] otherServices = template.postForObject(url + "/register?port={port}", null, String[].class, port);
        // Jetzt registrieren wir uns noch an allen anderen Services, die bekannt sind.
        this.otherServices.add(url);
        for (String service : otherServices) {
            if (this.otherServices.contains(service)) {
                continue;
            }
            registerMeAt(service, false);
        }
    }

    private String makeUrl(String hostname, int port) {
        return "http://" + hostname + ":" + port + "/banks/replication";
    }

    private String thisService() {
        return makeUrl("127.0.0.1", port);
    }

    public void setMaster(String masterUri) {
        masterBully = 0;
        this.masterUri = masterUri;
    }
}
