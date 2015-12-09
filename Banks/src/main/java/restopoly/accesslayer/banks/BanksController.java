package restopoly.accesslayer.banks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import restopoly.accesslayer.exceptions.*;
import restopoly.businesslogiclayer.BanksServiceBusinessLogic;
import restopoly.businesslogiclayer.ReplicationBusinessLogic;
import restopoly.dataaccesslayer.entities.*;

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

        replicationLogic.sendToMaster("/{gameid}", null, gameid);
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

        replicationLogic.sendToMaster("/{gameid}/players", bankAccount, gameid);
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
//        BankAccount bankAccount = getBankAccount(gameid, playerid);
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

        return banksServiceBusinessLogic.transferMoney(gameid, fromAccount, toAccount, amount, reason);
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
