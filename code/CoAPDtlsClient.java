package org.wsncoap.embedded;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ScandiumLogger;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.InMemoryPskStore;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;

public class CoAPDtlsClient {
    static {
        ScandiumLogger.initialize();
        ScandiumLogger.setLevel(Level.FINE);
    }

    private static final String LOCAL_SERVER_URI = "coaps://localhost/time";

    public static void main(String[] args) throws InterruptedException {

        DTLSConnector dtlsConnector;
        DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(new InetSocketAddress(0));

        // 设置PSK有两种方法 InMemoryPskStore
        // InMemoryPskStore pskStore = new InMemoryPskStore();
        // pskStore.setKey("identity", "123456".getBytes());
        // builder.setPskStore(pskStore);
        builder.setPskStore(new StaticPskStore("identity", "password".getBytes()));

        dtlsConnector = new DTLSConnector(builder.build());

        CoapResponse response = null;
        URI uri;
        try {
            if (args.length > 0) {
                uri =  new URI(args[0]);
            } else {
                uri = new URI(LOCAL_SERVER_URI);
            }
            System.out.println("URI: " + uri.toString());

            CoapClient client = new CoapClient(uri);
            client.setEndpoint(new CoapEndpoint(dtlsConnector, NetworkConfig.getStandard()));
            response = client.get();

        } catch (URISyntaxException e) {
            System.err.println("Invalid URI: " + e.getMessage());
            System.exit(-1);
        }

        if (response != null) {

            System.out.println("Response Code: " + response.getCode());
            System.out.println("Response Options: " + response.getOptions());
            System.out.println("Response Text: " + response.getResponseText());

            System.out.println("\nADVANCED\n");
            System.out.println(Utils.prettyPrint(response));

        } else {
            System.out.println("No response received.");
        }
    }
}
