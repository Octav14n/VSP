package restopoly.businesslogiclayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restopoly.dataaccesslayer.entities.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Paddy-Gaming on 06.12.2015.
 */
@Component
public class BanksServiceBusinessLogic {
    private BankList listWithAvailableBanks;
    @Autowired
    private ReplicationBusinessLogic replicationLogic;

    public BanksServiceBusinessLogic() {
        listWithAvailableBanks = new BankList();
    }

    /**
     * Gets the bank for the given game.
     * @param gameid ID of the game from which you want the bank.
     * @return Bank if the game already owns a bank. null otherwise.
     */
    public Bank getBank(String gameid) {
        return listWithAvailableBanks.getBank(gameid);
    }

    /**
     * This method will get the newBankAccount for a player if it exists.
     *
     * @param bank     The given bank for which the newBankAccount of this playerid is.
     * @param playerid Is the playerid for which the bankaccount will be found.
     * @return         Returns the bankaccount for a player or null if there isn't a match.
     */
    public BankAccount getBankAccount(Bank bank, String playerid) {
        List<BankAccount> bankAccounts = bank.getBankAccounts();

        for (BankAccount bankAccount : bankAccounts) {
            if (playerid.equals(bankAccount.getPlayer().getId())) {
                return bankAccount;
            }
        }
        return null;
    }

    /**
     * This Method will check, that an newBankAccount is maybe already in use.
     * So this check helps to create a newBankAccount only once.
     *
     * @param newBankAccount The newBankAccount which will be checked for existenz.
     * @param bank        The bank which will own the given newBankAccount.
     * @return            Returns true if the given newBankAccount exists or false when the given newBankAccount
     *                    doesn't exist.
     */
    public boolean isBankAccountExisting(BankAccount newBankAccount, Bank bank) {
        boolean bankAccountExists = false;

        String playerID = newBankAccount.getPlayer().getId();
        BankAccount alreadyAvailableBankAccount = getBankAccount(bank, playerID);
        if (alreadyAvailableBankAccount != null) {
            bankAccountExists = true;
        }
        return bankAccountExists;
    }

    /**
     * This method creates a new bank. After creating the bank will be added to the list with all available banks.
     * @param gameid                 The gameid is given to set the new bank to the game with this ID.
     * @return                       Returns a new Bank.
     */
    public Bank createBank(String gameid) {
        Bank newBank = new Bank();
        listWithAvailableBanks.addBank(newBank, gameid);
        return newBank;
    }

    public void addTransferToTransferList(Transfer transfer, String gameid) {

    }

    /**
     * This method will return all actual available transfers as a List of transfers.
     *
     * @param transferList This list holds every Transfer which is / was available.
     * @param gameid       The gameid shows the identifier for the transfers into a game.
     * @return             Returns a list with all available transfers.
     */
    public List<Transfer> getAllAvailableTransfers(TransferList transferList, String gameid) {
        Map<String, Transfer> transferMap = transferList.getTransfers();
        List<Transfer> transfers = new ArrayList<>();

        for (Transfer transfer : transferMap.values()) {
            transfers.add(transfer);
        }

        return transfers;
    }

    /**
     * This method will add the given amount of money to the given BankAccount.
     * @param from account where the money will be added/substracted to/from.
     * @param amount amount, if positive we will add money, if negative we will substract money.
     * @return Events that happened while adding/substracting money.
     */
    public List<Event> transferMoney(String gameid, BankAccount from, BankAccount to, int amount, String reason) {
        try {
            // TODO: Inform all others that we will do things to this bankAccount.
            replicationLogic.lockBankMutex(gameid);
            int tmpFrom = 0;
            int tmpTo = 0;
            if (from != null) {
                tmpFrom = from.getSaldo();
            }
            if (to != null) {
                tmpTo = to.getSaldo();
            }

            try {
                if (from != null) {
                    from.addSaldo(-amount);
                }

                // Simulate an Exception after we collected the money from "from",
                // without giving it to "to".
                // Also: Mobbing Mary Poppins.
                if ("Supercalifragilisticexpialigetisch".equals(reason)) {
                    throw new RuntimeException(
                        "Supercalifragilisticexpialigetisch ist ein verbotener Ueberweisungszweck!");
                }

                if (to != null) {
                    to.addSaldo(amount);
                }
            } catch (Exception e) {
                if (from != null) {
                    from.setSaldo(tmpFrom);
                }
                if (to != null) {
                    to.setSaldo(tmpTo);
                }
                throw new RuntimeException("Send money doesnt work! Rollback...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            replicationLogic.unlockBankMutex(gameid);
        }

        return Collections.emptyList(); // We didn't produce any events.
    }

//    /**
//     * Waits for the Bank to be locked to manipulate data.
//     * @param bank
//     */
//    public void lockBankMutex(Bank bank) throws InterruptedException {
//        // TODO: Inform all other services that we want the muhtex.
//        bank.muhtex.acquire();
//    }
//
//    /**
//     * Unlocks the muhtex of the Bank after manipulating data.
//     * @param bank
//     */
//    public void unlockBankMutex(Bank bank) {
//        // TODO: Inform all other services that we don't want the muhtex.
//        bank.muhtex.release();
//    }
//
//    public boolean isLockedBankMutex(Bank bank) {
//        try {
//            return bank.muhtex.attempt(0);
//        } catch (InterruptedException e) {
//            return false;
//        }
//    }
}
