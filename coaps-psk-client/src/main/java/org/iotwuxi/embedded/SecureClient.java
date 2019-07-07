package org.iotwuxi.embedded;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;

import java.io.IOException;

/**
 * @author xukai
 */
public class SecureClient  {

    private static final String serverUrl = "coaps://localhost/time";

    public void test(String[] args) {

        DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
        builder.setPskStore(new StaticPskStore("identity", "password".getBytes()));
        builder.setSupportedCipherSuites(CipherSuite.TLS_PSK_WITH_AES_128_CCM_8,
                CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256);
        DTLSConnector dtlsConnector = new DTLSConnector(builder.build());

        CoapClient client = new CoapClient();
        CoapEndpoint.Builder coapEndpointBuilder = new CoapEndpoint.Builder();
        coapEndpointBuilder.setConnector(dtlsConnector);

        CoapResponse response = null;
        client.setEndpoint(coapEndpointBuilder.build());
        client.setURI(serverUrl);

        try {
            response = client.get();
        } catch (ConnectorException e) {
            System.err.println("Connect Error: " + e.getMessage());
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public static void main(String[] args) throws InterruptedException {

        SecureClient client = new SecureClient();
        client.test(args);

        synchronized (SecureClient.class) {
            SecureClient.class.wait();
        }
    }
}
