package restopoly.dataaccesslayer.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by octavian on 16.11.15.
 */
public class BankList {
    private Map<Integer, Bank> bankMap;

    public BankList() {
        bankMap = new HashMap<>();
    }

    public Bank getBank(int gameid) {
        return bankMap.get(gameid);
    }

    public Bank createBank(int gameid) {
        Bank bank = new Bank();
        bankMap.put(gameid, bank);
        return bank;
    }
}
