package restopoly.dataaccesslayer.entities;

/**
 * Created by octavian on 04.12.15.
 */
public class Service {
    String name;
    String description;
    String service;
    String uri;

    private Service() {}

    public Service(String description, String service) {
        this.name = "ps_neon_" + service;
        this.description = description;
        this.service = service;
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

    public void setUri(String uri) { this.uri = uri; }

    public String getUri() {
        return uri;
    }


    @Override public String toString() {
        return "Service{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", service='" + service + '\'' +
            ", uri='" + uri + '\'' +
            '}';
    }
}
