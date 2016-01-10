package restopoly.banks.dataaccesslayer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Paddy-Gaming on 14.11.2015.
 */
public class Bank {
    @JsonIgnore
    public Lock muhtex = new ReentrantLock();
    private List<BankAccount> bankAccounts;

    public Bank() {
        this.bankAccounts = new ArrayList<>();
    }

    /**
     * This method will get all bankAccounts which are registered at the bank.
     *
     * @return Returns a list with all bankAccounts of this bank.
     */
    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    /**
     * This method will register a new bankAccount at this bank.
     *
     * @param bankAccount This bankAccount will be registered at the bank.
     */
    public void addBankAccount(BankAccount bankAccount) {
        bankAccounts.add(bankAccount);
    }
}
