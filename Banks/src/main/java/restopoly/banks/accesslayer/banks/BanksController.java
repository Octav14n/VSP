package restopoly.banks.accesslayer.banks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import restopoly.banks.accesslayer.exceptions.*;
import restopoly.banks.businesslogiclayer.BanksServiceBusinessLogic;
import restopoly.banks.dataaccesslayer.entities.Bank;
import restopoly.banks.dataaccesslayer.entities.BankAccount;
import restopoly.banks.dataaccesslayer.entities.Event;
import restopoly.banks.dataaccesslayer.entities.TransferList;
import restopoly.banks.businesslogiclayer.ReplicationBusinessLogic;

import java.util.Collections;
import java.util.List;

/**
 * Created by octavian on 16.11.15.
 */
@RestController
@RequestMapping("/banks")
public class BanksController {
    private TransferList transferList = new TransferList();
    @Autowired
    private BanksServiceBusinessLogic banksServiceBusinessLogic;
    @Autowired
    private ReplicationBusinessLogic replicationLogic;

    @RequestMapping(value = "/{gameid}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Bank getBankForGameID(@PathVariable String gameid) {
        Bank bank = banksServiceBusinessLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        return bank;
    }

    @RequestMapping(value = "/{gameid}", method = RequestMethod.PUT)
    public void createBank(@PathVariable String gameid) {
        Bank bank = banksServiceBusinessLogic.getBank(gameid);

        if (bank != null) {
            throw new BankAlreadyExistsException();
        }

        if (!replicationLogic.isMaster()) {
            // Master must create the bank.
            replicationLogic.sendToMaster("/{gameid}", null, gameid);
        } else {
            // We will create the bank.
            replicationLogic.createBank(gameid);
        }
    }

    /*@RequestMapping(value = "/{gameid}/transfers", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Transfer> getAllAvailableTransfers(@PathVariable String gameid) {
        List<Transfer> transfers = banksServiceBusinessLogic.getAllAvailableTransfers(transferList, gameid);

        if (transfers == null) {
            // TODO: No available transfer exception!
        }

        return transfers;
    }*/

    @RequestMapping(value = "/{gameid}/players", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createBankAccount(@PathVariable String gameid, @RequestBody BankAccount bankAccount) {
        Bank bank = banksServiceBusinessLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        if (banksServiceBusinessLogic.isBankAccountExisting(bankAccount, bank)) {
            throw new BankAccountAlreadyExistsException();
        }

        if (!replicationLogic.isMaster()) {
            // Master must create the bank account.
            replicationLogic.sendToMaster("/{gameid}/players", bankAccount, gameid);
        } else {
            // We will create the bank account.
            replicationLogic.createBankAccount(gameid, bank, bankAccount);
        }
    }

    @RequestMapping(value = "/{gameid}/players", method = RequestMethod.GET)
    public List<BankAccount> getBankAccounts(@PathVariable String gameid) {
        Bank bank = banksServiceBusinessLogic.getBank(gameid);
        if (bank == null) {
            throw new BankNotFoundException();
        }
        return bank.getBankAccounts();
    }

    @RequestMapping(value = "/{gameid}/players/{playerid}", method = RequestMethod.GET)
    public int getBankAccountSaldo(@PathVariable String gameid, @PathVariable String playerid) {

        Bank bank = banksServiceBusinessLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        BankAccount bankAccount = banksServiceBusinessLogic.getBankAccount(bank, playerid);
        return bankAccount.getSaldo();
    }

    @RequestMapping(value = "/{gameid}/transfer/to/{to}/{amount}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public List<Event> createBankTransferTo(@PathVariable String gameid, @PathVariable String to,
        @PathVariable int amount, @RequestBody String reason) {

        Bank bank = banksServiceBusinessLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        BankAccount bankAccount = banksServiceBusinessLogic.getBankAccount(bank, to);

        return banksServiceBusinessLogic.transferMoney(gameid, null, bankAccount, amount, reason);
    }

    @RequestMapping(value = "/{gameid}/transfer/from/{from}/{amount}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public List<Event> createBankTransferFrom(@PathVariable String gameid, @PathVariable String from,
        @PathVariable int amount, @RequestBody String reason) {

        Bank bank = banksServiceBusinessLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        BankAccount bankAccount = banksServiceBusinessLogic.getBankAccount(bank, from);

        if (bankAccount.getSaldo() < amount)
            throw new BankInsufficientFundsException();

        return banksServiceBusinessLogic.transferMoney(gameid, bankAccount, null, amount, reason);
    }

    @RequestMapping(value = "/{gameid}/transfer/from/{from}/to/{to}/{amount}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public synchronized List<Event> createBankTransfer(@PathVariable String gameid, @PathVariable String from,
        @PathVariable String to, @PathVariable int amount, @RequestBody String reason) {
        List<Event> events = Collections.emptyList();

        Bank bank = banksServiceBusinessLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        BankAccount fromAccount = banksServiceBusinessLogic.getBankAccount(bank, from);
        BankAccount toAccount = banksServiceBusinessLogic.getBankAccount(bank, to);

        if (!replicationLogic.isMaster()) {
            replicationLogic.sendToMaster("/{gameid}/transfer/from/{from}/to/{to}/{amount}", reason, gameid, from, to);
        } else {
            return replicationLogic.createBankTransfer(gameid, fromAccount, toAccount, amount, reason);
        }
        return events;
    }

    /**
     * Returns the BankAccount of the given player in the given game.
     * @param gameid Game where the BankAccount is connected to.
     * @param playerId Player which the BankAccount is connected to.
     * @return BankAccount you requested.
     */
    private BankAccount getBankAccount(String gameid, String playerId) {
        Bank bank = banksServiceBusinessLogic.getBank(gameid);
        if (bank == null) {
            throw new BankNotFoundException();
        }

        BankAccount bankAccountFromPlayer = banksServiceBusinessLogic.getBankAccount(bank, playerId);

        if (bankAccountFromPlayer == null) {
            throw new BankAccountNotFoundException();
        }
        return bankAccountFromPlayer;
    }
}
