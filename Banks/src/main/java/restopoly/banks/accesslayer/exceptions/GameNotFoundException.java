package restopoly.banks.accesslayer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by octavian on 27.10.15.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Game not Found")
public class GameNotFoundException extends RuntimeException {
}
