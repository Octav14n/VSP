package restopoly.accesslayer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by octavian on 16.11.15.
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Insufficient funds")
public class BankInsufficientFundsException extends RuntimeException {
}
