package restopoly.dataaccesslayer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paddy-Gaming on 14.11.2015.
 */
public class Bank {
    @JsonIgnore
    public Mutex muhtex = new Mutex();
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
