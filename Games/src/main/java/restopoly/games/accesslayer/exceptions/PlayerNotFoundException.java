package restopoly.games.accesslayer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by octavian on 27.10.15.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Player not Found")
public class PlayerNotFoundException extends RuntimeException {
}
