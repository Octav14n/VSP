package restopoly.dataaccesslayer.entities;

import restopoly.accesslayer.exceptions.BankAccountAlreadyExistsException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paddy-Gaming on 14.11.2015.
 */
public class Bank {
    private List<BankAccount> bankAccounts;

    public Bank() {
        this.bankAccounts = new ArrayList<>();
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public BankAccount getBankAccount(String playerId) {
        for (BankAccount bankAccount : bankAccounts) {
            if (playerId.equals(bankAccount.getPlayer().getPlayer().getId())) {
                return bankAccount;
            }
        }
        return null;
    }

    public void addBankAccount(BankAccount bankAccount) {
        bankAccounts.add(bankAccount);
    }

    public boolean isBankAccountExists(BankAccount bankAccount) {
        return getBankAccount(bankAccount.getPlayer().getPlayer().getId()) != null;
    }
}
