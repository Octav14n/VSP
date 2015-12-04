package restopoly.entities;

import com.sun.istack.internal.NotNull;

/**
 * Created by octavian on 04.12.15.
 */
public class Service {
    String name;
    String description;
    @NotNull
    String service;
    @NotNull
    String uri;
    String status;
    String _uri;

    private Service() {}

    public Service(String name, String description, String service, String uri) {
        this.name = name;
        this.description = description;
        this.service = service;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getService() {
        return service;
    }

    public String getUri() {
        return uri;
    }

    public String getStatus() {
        return status;
    }

    public String get_uri() {
        return _uri;
    }

    @Override public String toString() {
        return "Service{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", service='" + service + '\'' +
            ", uri='" + uri + '\'' +
            ", status='" + status + '\'' +
            ", _uri='" + _uri + '\'' +
            '}';
    }
}
