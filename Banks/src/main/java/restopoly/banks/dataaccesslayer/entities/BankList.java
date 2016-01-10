package restopoly.banks.dataaccesslayer.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by octavian on 16.11.15.
 */
public class BankList {
    private Map<String, Bank> bankMap;

    public BankList() {
        bankMap = new HashMap<>();
    }

    /**
     * This method returns the bank for the given gameid.
     *
     * @param gameid This gameid is given to get the bank.
     * @return       Returns a bank which is connected with the game of this ID.
     */
    public Bank getBank(String gameid) {
        return bankMap.get(gameid);
    }

    /**
     * This method will add a new bank to the list with all existing banks.
     *
     * @param bank The incoming bank which should added to the list.
     * @param gameid The gameid identifies to which game the bank is connected.
     */
    public void addBank(Bank bank, String gameid) {
        bankMap.put(gameid, bank);
    }
}
