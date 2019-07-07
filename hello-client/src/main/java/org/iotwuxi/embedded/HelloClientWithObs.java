package org.iotwuxi.embedded;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.elements.exception.ConnectorException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author xukai
 */
public class HelloClientWithObs {
    public static void main(String[] args) throws IOException, InterruptedException {
        URI uri = null;

        if (args.length > 0) {
            try {
                uri = new URI(args[0]);
            } catch (URISyntaxException e) {
                System.err.println("Invalid URI: " + e.getMessage());
                System.exit(-1);
            }
        } else {
            try {
                uri = new URI("coap://127.0.0.1/time");
            } catch (URISyntaxException e) {
                System.err.println("Invalid URI: " + e.getMessage());
                System.exit(-1);
            }
        }
        NetworkConfig networkConfig = NetworkConfig.createStandardWithoutFile();
        CoapEndpoint.Builder coapEndpointBuilder = new CoapEndpoint.Builder();
        coapEndpointBuilder.setNetworkConfig(networkConfig);

        CoapClient client = new CoapClient();
        client.setEndpoint(coapEndpointBuilder.build());
        client.setURI(uri.toString());
        CoapObserveRelation relation = client.observe(new ObserveTimeHandler());

        Thread.sleep(60*1000);

        relation.reactiveCancel();

    }

    public static class ObserveTimeHandler implements CoapHandler {
        public ObserveTimeHandler() {
            super();
        }

        public void onLoad(CoapResponse response) {
            if (response != null) {
                System.out.println(Utils.prettyPrint(response));
            } else {
                System.out.println("No response received.");
            }
        }

        public void onError() {

        }
    }
}
