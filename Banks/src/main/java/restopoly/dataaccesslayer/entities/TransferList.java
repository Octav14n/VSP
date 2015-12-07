package restopoly.dataaccesslayer.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paddy-Gaming on 07.12.2015.
 */
public class TransferList {

    private Map<Integer, Transfer> transfers;

    public TransferList() {
        this.transfers = new HashMap<>();
    }

    public Map<Integer, Transfer> getTransfers() {
        return transfers;
    }

    public void setTransfer(Transfer transfer, int gameid) {
        transfers.put(gameid, transfer);
    }
}
