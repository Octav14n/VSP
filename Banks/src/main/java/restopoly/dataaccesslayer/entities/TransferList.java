package restopoly.dataaccesslayer.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paddy-Gaming on 07.12.2015.
 */
public class TransferList {

    private Map<String, Transfer> transfers;

    public TransferList() {
        this.transfers = new HashMap<>();
    }

    public Map<String, Transfer> getTransfers() {
        return transfers;
    }

    public void setTransfer(Transfer transfer, String gameid) {
        transfers.put(gameid, transfer);
    }
}
