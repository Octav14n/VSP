package restopoly.accesslayer.banks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import restopoly.accesslayer.exceptions.BankAccountAlreadyExistsException;
import restopoly.accesslayer.exceptions.BankAlreadyExistsException;
import restopoly.accesslayer.exceptions.BankNotFoundException;
import restopoly.accesslayer.exceptions.ServiceAlreadyRegisteredException;
import restopoly.businesslogiclayer.BanksServiceBusinessLogic;
import restopoly.businesslogiclayer.ReplicationBusinessLogic;
import restopoly.dataaccesslayer.entities.Bank;
import restopoly.dataaccesslayer.entities.BankAccount;
import restopoly.dataaccesslayer.entities.Event;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This Controller will provide access to internas for other Bank-Services by Patrick & Simon.
 */
@RestController
@RequestMapping("/banks/replication")
public class ReplicationController {
    @Autowired
    ReplicationBusinessLogic replicationLogic;
    @Autowired
    BanksServiceBusinessLogic banksLogic;

    @RequestMapping(value = "/{gameid}", method = RequestMethod.POST)
    public void createBank(@PathVariable String gameid) {
        Bank bank = replicationLogic.getBank(gameid);

        if (bank != null) {
            throw new BankAlreadyExistsException();
        }

        replicationLogic.createBank(gameid);
    }

    @RequestMapping(value = "/{gameid}/players", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createBankAccount(@PathVariable String gameid, @RequestBody BankAccount bankAccount) {
        Bank bank = banksLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        if (banksLogic.isBankAccountExisting(bankAccount, bank)) {
            throw new BankAccountAlreadyExistsException();
        }

        replicationLogic.createBankAccount(gameid, bank, bankAccount);
    }

    @RequestMapping(value = "/{gameid}/transfer/from/{from}/to/{to}/{amount}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public synchronized List<Event> createBankTransfer(@PathVariable String gameid, @PathVariable String from,
                                                       @PathVariable String to, @PathVariable int amount, @RequestBody String reason) {
        List<Event> events = Collections.emptyList();

        Bank bank = banksLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        BankAccount fromAccount = banksLogic.getBankAccount(bank, from);
        BankAccount toAccount = banksLogic.getBankAccount(bank, to);

        return banksLogic.transferMoney(gameid, fromAccount, toAccount, amount, reason);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public List<String> registerReplicationService(HttpServletRequest request, @RequestParam int port) {
        if (replicationLogic.isRegistered(request.getRemoteAddr(), port))
            throw new ServiceAlreadyRegisteredException();
        return replicationLogic.registerReplicationService(request.getRemoteAddr(), port);
    }

    @RequestMapping(value = "/bully", method = RequestMethod.POST)
    public int bully(HttpServletRequest request, @RequestParam int port, @RequestParam int bullyIntensity) {
        return replicationLogic.bully(bullyIntensity, request.getRemoteAddr(), port);
    }

    @RequestMapping(value = "/{gameid}/lock", method = RequestMethod.POST)
    public void lockMutex(HttpServletRequest request, @PathVariable String gameid, @RequestParam int myPort) throws InterruptedException {
        Bank bank = replicationLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        replicationLogic.lockBankMutex(gameid, request.getRemoteAddr(), myPort);
    }

    @RequestMapping(value = "/{gameid}/lock", method = RequestMethod.GET)
    public boolean isLockedMutex(@PathVariable String gameid) {
        Bank bank = replicationLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        return replicationLogic.isUnlockedBankMutex(gameid);
    }

    @RequestMapping(value = "/{gameid}/lock", method = RequestMethod.DELETE)
    public void deleteMutex(HttpServletRequest request, @PathVariable String gameid, @RequestParam int myPort) throws InterruptedException {
        Bank bank = replicationLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        replicationLogic.unlockBankMutex(gameid, request.getRemoteAddr(), myPort);
    }
}
