package restopoly.banks.accesslayer.exceptions;

/**
 * Created by octavian on 28.10.15.
 */
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid parameter")
public class ParameterIsInvalidException extends RuntimeException {
    public ParameterIsInvalidException() {}
    public ParameterIsInvalidException(String parameter) { super(parameter); }
}
