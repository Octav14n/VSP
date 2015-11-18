package restopoly.accesslayer.banks;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import restopoly.accesslayer.exceptions.BankAccountAlreadyExistsException;
import restopoly.accesslayer.exceptions.BankAccountNotFoundException;
import restopoly.accesslayer.exceptions.BankInsufficientFundsException;
import restopoly.accesslayer.exceptions.BankNotFoundException;
import restopoly.dataaccesslayer.entities.Bank;
import restopoly.dataaccesslayer.entities.BankAccount;
import restopoly.dataaccesslayer.entities.BankList;
import restopoly.dataaccesslayer.entities.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by octavian on 16.11.15.
 */
@RestController @RequestMapping("/banks") public class BanksController {
    private BankList bankList = new BankList();

    @RequestMapping(value = "/{gameid}/players", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createBankAccount(@PathVariable int gameid, @RequestBody BankAccount bankAccount) {
        Bank bank = bankList.getBank(gameid);
        if (bank == null)
            bank = bankList.createBank(gameid);
        if (bank.isBankAccountExists(bankAccount))
            throw new BankAccountAlreadyExistsException();
        bank.addBankAccount(bankAccount);
    }

    @RequestMapping(value = "/{gameid}/players", method = RequestMethod.GET)
    public List<BankAccount> getBankAccounts(@PathVariable int gameid) {
        Bank bank = bankList.getBank(gameid);
        if (bank == null)
            throw new BankNotFoundException();
        return bank.getBankAccounts();
    }

    @RequestMapping(value = "/{gameid}/players/{playerid}", method = RequestMethod.GET)
    public int getBankAccountSaldo(@PathVariable int gameid, @PathVariable String playerid) {
        BankAccount bankAccount = getBankAccount(gameid, playerid);
        return bankAccount.getSaldo();
    }

    @RequestMapping(value = "/{gameid}/transfer/to/{to}/{amount}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public List<Event> createBankTransferTo(@PathVariable int gameid, @PathVariable String to,
        @PathVariable int amount, @RequestBody String reason) {
        BankAccount bankAccount = getBankAccount(gameid, to);
        bankAccount.addSaldo(amount);
        return Collections.emptyList();
    }

    @RequestMapping(value = "/{gameid}/transfer/from/{from}/{amount}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public List<Event> createBankTransferFrom(@PathVariable int gameid, @PathVariable String from,
        @PathVariable int amount, @RequestBody String reason) {
        BankAccount bankAccount = getBankAccount(gameid, from);
        if (bankAccount.getSaldo() < amount)
            throw new BankInsufficientFundsException();
        bankAccount.addSaldo(-amount);
        return Collections.emptyList();
    }

    @RequestMapping(value = "/{gameid}/transfer/from/{from}/to/{to}/{amount}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public synchronized List<Event> createBankTransfer(@PathVariable int gameid, @PathVariable String from,
        @PathVariable String to, @PathVariable int amount, @RequestBody String reason) {
        List<Event> events = Collections.emptyList();

        // Here we get the values before we change them.
        BankAccount fromAccount = getBankAccount(gameid, from);
        BankAccount toAccount = getBankAccount(gameid, to);
        int fromSaldo = fromAccount.getSaldo();
        int toSaldo = toAccount.getSaldo();

        try {
            // Here we will change values.
            events = createBankTransferFrom(gameid, from, amount, reason);

            // Simulate an Exception after we collected the money from "from",
            // without giving it to "to".
            // Also: Mobbing Mary Poppins.
            if ("Supercalifragilisticexpialigetisch".equals(reason))
                throw new RuntimeException("Alles ist doof, ich will nicht mehr!");

            events.addAll(createBankTransferTo(gameid, to, amount, reason));
        } catch (Throwable e) {
            // If an Exception is thrown while we change values, we will revert the changes.
            fromAccount.setSaldo(fromSaldo);
            toAccount.setSaldo(toSaldo);
            throw e;
        }
        return events;
    }

    /**
     * Returns the BankAccount of the given player in the given game.
     * @param gameid Game where the BankAccount is connected to.
     * @param playerId Player which the BankAccount is connected to.
     * @return BankAccount you requested.
     */
    private BankAccount getBankAccount(int gameid, String playerId) {
        Bank bank = bankList.getBank(gameid);
        if (bank == null)
            throw new BankNotFoundException();
        BankAccount bankAccountFrom = bank.getBankAccount(playerId);
        if (bankAccountFrom == null)
            throw new BankAccountNotFoundException();
        return bankAccountFrom;
    }
}
