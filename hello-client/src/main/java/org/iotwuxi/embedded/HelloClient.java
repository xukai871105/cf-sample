package org.iotwuxi.embedded;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.elements.exception.ConnectorException;

/**
 * @author xukai
 */
public class HelloClient {
    // private static final String serverUrl = "coap://127.0.0.1:5683/time";
    private static final String serverUrl = "coap://139.196.187.107:5683/test";
    public static void main(String[] args) throws IOException, ConnectorException {

        NetworkConfig networkConfig = NetworkConfig.createStandardWithoutFile();
        CoapEndpoint.Builder coapEndpointBuilder = new CoapEndpoint.Builder();
        coapEndpointBuilder.setNetworkConfig(networkConfig);

        CoapClient client = new CoapClient();
        client.setEndpoint(coapEndpointBuilder.build());
        client.setURI(serverUrl);

        CoapResponse response = client.get();

        if (response != null) {

            System.out.println(response.getCode());
            System.out.println(response.getOptions());
            System.out.println(response.getResponseText());

            System.out.println("\nADVANCED\n");
            System.out.println(Utils.prettyPrint(response));
        } else {
            System.out.println("No response received.");
        }
    }
}
