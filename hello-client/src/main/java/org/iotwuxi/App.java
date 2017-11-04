package org.iotwuxi;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        URI uri = null;
        try {
            uri = new URI("coap://wsncoap.org/test");
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI: " + e.getMessage());
            System.exit(-1);
        }

        CoapClient client = new CoapClient(uri);
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
