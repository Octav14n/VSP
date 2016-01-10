package restopoly.banks.accesslayer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by octavian on 09.12.15.
 */

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Service is already registered.")
public class ServiceAlreadyRegisteredException extends RuntimeException {
}
