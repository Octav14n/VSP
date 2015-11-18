package restopoly.accesslayer.exceptions;

/**
 * Created by octavian on 16.11.15.
 */
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Bank account not found")
public class BankAccountNotFoundException extends RuntimeException {
}
