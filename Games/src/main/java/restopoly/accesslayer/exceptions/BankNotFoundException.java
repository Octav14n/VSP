package restopoly.accesslayer.exceptions;

/**
 * Created by octavian on 16.11.15.
 */
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Bank not found")
public class BankNotFoundException extends RuntimeException {
}
