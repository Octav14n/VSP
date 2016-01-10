package restopoly.banks.accesslayer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by octavian on 08.12.15.
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Bank already exists.")
public class BankAlreadyExistsException extends RuntimeException {
}
