package restopoly.businesslogiclayer;

import restopoly.dataaccesslayer.entities.*;

import java.util.List;

/**
 * Created by Paddy-Gaming on 06.12.2015.
 */
public class BanksServiceBusinessLogic {

    public BanksServiceBusinessLogic() {

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
     * @param listWithAvailableBanks This is the list with every existing bank for any game.
     * @param gameid The gameid is given to set the new bank to the game with this ID.
     * @return Returns a new Bank.
     */
    public Bank createBank(BankList listWithAvailableBanks, int gameid) {
        Bank newBank = new Bank();
        listWithAvailableBanks.addBank(newBank, gameid);
        return newBank;
    }
}
