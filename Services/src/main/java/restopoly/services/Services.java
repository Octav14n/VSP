package restopoly.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import restopoly.services.dataaccesslayer.entities.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.logging.Logger;

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

    public Services(Service service, int port) {
        try {
            service.setUri("http://" + getLocalHostLANAddress().getHostAddress() + ":" + port);
            InetAddress.getByName("vs-docker");
            this.uri = "http://vs-docker:8053/services";
            System.out.println("vsdocker ist vorhanden. Ich benutze jetzt: " + this.uri);
        } catch (UnknownHostException e) {
            this.uri = "https://vs-docker.informatik.haw-hamburg.de/ports/8053/services";
            System.out.println("vsdocker ist *nicht* vorhanden. Fallback zu " + this.uri);
        }
        this.service = service;
        restTemplate = new RestTemplate();
    }

    /**
     * Registers the provided service to our discovery server.
     * @return ID that our service is assigned to.
     */
    public String register() {
        String ret = "";
        try {
            final HttpHeaders headers = new HttpHeaders();
            final String base64Creds = "YWJxMzI5OkRLR1JIZDIwMTUy";
            headers.add("Authorization", "Basic " + base64Creds);
            final HttpEntity<Service> request = new HttpEntity<>(service, headers);
            System.out.println(restTemplate);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
            ret += response.getBody();
            System.out.println("VSDocker gefunden, werde mich jetzt registrieren:");
        } catch (Exception e) {
            System.out.println("VSDocker nicht gefunden, werde mich nicht registrieren.");
            e.printStackTrace();
            return ret + e.toString();
        }
        return ret;
    }

    public static String hostAddress(int port) {
        try {
            return "http://" + getLocalHostLANAddress().getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            return "http://localhost:" + port;
        }
    }

    /**
     *  -- Stolen from Torben and Louisa --
     *
     * Returns an <code>InetAddress</code> object encapsulating what is most likely the machine's LAN IP address.
     * <p/>
     * This method is intended for use as a replacement of JDK method <code>InetAddress.getLocalHost</code>, because
     * that method is ambiguous on Linux systems. Linux systems enumerate the loopback network interface the same
     * way as regular LAN network interfaces, but the JDK <code>InetAddress.getLocalHost</code> method does not
     * specify the algorithm used to select the address returned under such circumstances, and will often return the
     * loopback address, which is not valid for network communication. Details
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037">here</a>.
     * <p/>
     * This method will scan all IP addresses on all network interfaces on the host machine to determine the IP address
     * most likely to be the machine's LAN address. If the machine has multiple IP addresses, this method will prefer
     * a site-local IP address (e.g. 192.168.x.x or 10.10.x.x, usually IPv4) if the machine has one (and will return the
     * first site-local address if the machine has more than one), but if the machine does not hold a site-local
     * address, this method will return simply the first non-loopback address found (IPv4 or IPv6).
     * <p/>
     * If this method cannot find a non-loopback address using this selection algorithm, it will fall back to
     * calling and returning the result of JDK method <code>InetAddress.getLocalHost</code>.
     * <p/>
     *
     * @throws UnknownHostException If the LAN address of the machine cannot be found.
     */
    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (final Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                final NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (final Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    final InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.getHostAddress().startsWith("141")) {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr;
                        }
                        else if ((candidateAddress == null) && (inetAddr instanceof Inet4Address)) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            final InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        }
        catch (final Exception e) {
            final UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
}
