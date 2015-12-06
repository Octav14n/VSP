package restopoly;

import org.springframework.web.client.RestTemplate;
import restopoly.entities.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;

/**
 * Created by octavian on 04.12.15.
 */
public class Services {
    RestTemplate restTemplate;
    String uri = "";
    Service service;

    static {
        System.out.println("Will now disable hostname check in SSL, only to be used during development");

        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

            }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException("Could not disable SSL checsk.");
        }
    }

    public Services(Service service) {
        this.uri = "https://vs-docker.informatik.haw-hamburg.de/ports/8053/services";
        this.service = service;
        restTemplate = new RestTemplate();
    }

    /**
     * Registers the provided service to our discovery server.
     * @return ID that our service is assigned to.
     */
    public int register() {
        System.out.println(restTemplate);
        Service haha = restTemplate.postForObject(uri, service, Service.class);
        System.out.println("Output: " + haha);
        return 0;
    }

    /**
     * Returns the URI of the Service running in this container.
     */
    public static String uri() {
        try {
            return String.format("http://%s:4567/", InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            return "http://0.0.0.0:4567/";
        }
    }
}
