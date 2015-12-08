package restopoly.accesslayer.banks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import restopoly.accesslayer.exceptions.BankNotFoundException;
import restopoly.businesslogiclayer.BanksServiceBusinessLogic;
import restopoly.businesslogiclayer.ReplicationBusinessLogic;
import restopoly.dataaccesslayer.entities.Bank;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * This Controller will provide access to internas for other Bank-Services by Patrick & Simon.
 */
@RestController
@RequestMapping("/banks/replication")
public class ReplicationController {
    @Autowired
    ReplicationBusinessLogic replicationLogic;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public List<String> registerReplicationService(HttpServletRequest request, @RequestParam int port) {
        return replicationLogic.registerReplicationService(request.getRemoteAddr(), port);
    }

    @RequestMapping(value = "/bully", method = RequestMethod.POST)
    public int bully(HttpServletRequest request, @RequestParam int port, @RequestParam int bullyIntensity) {
        return replicationLogic.bully(bullyIntensity, request.getRemoteAddr(), port);
    }

    @RequestMapping(value = "/{gameid}/lock", method = RequestMethod.POST)
    public void getMutex(@PathVariable String gameid) throws InterruptedException {
        Bank bank = replicationLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        replicationLogic.lockBankMutex(bank);
    }

    @RequestMapping(value = "/{gameid}/lock", method = RequestMethod.DELETE)
    public void deleteMutex(@PathVariable String gameid) throws InterruptedException {
        Bank bank = replicationLogic.getBank(gameid);

        if (bank == null) {
            throw new BankNotFoundException();
        }

        replicationLogic.unlockBankMutex(bank);
    }
}
