package org.iotwuxi.embedded;

import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.RawDataChannel;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ScandiumLogger;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.InMemoryPskStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class SampleServerDTLS
{
    static {
        ScandiumLogger.initialize();
        ScandiumLogger.setLevel(Level.FINE);
    }

    private static final int DEFAULT_PORT = 5684;
    private static final Logger LOG = Logger.getLogger(SampleServerDTLS.class.getName());
    private static final String TRUST_STORE_PASSWORD = "rootPass";
    private static final String KEY_STORE_PASSWORD = "endPass";
    private static final String KEY_STORE_LOCATION = "certs/keyStore.jks";
    private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks";

    private DTLSConnector dtlsConnector;

    public SampleServerDTLS() {
        InMemoryPskStore pskStore = new InMemoryPskStore();
        // put in the PSK store the default identity/psk for tinydtls tests
        pskStore.setKey("Client_identity", "secretPSK".getBytes());
        InputStream in = null;
        try {
            // load the key store
            KeyStore keyStore = KeyStore.getInstance("JKS");
            in = getClass().getClassLoader().getResourceAsStream(KEY_STORE_LOCATION);
            keyStore.load(in, KEY_STORE_PASSWORD.toCharArray());
            in.close();

            // load the trust store
            KeyStore trustStore = KeyStore.getInstance("JKS");
            InputStream inTrust = getClass().getClassLoader().getResourceAsStream(TRUST_STORE_LOCATION);
            trustStore.load(inTrust, TRUST_STORE_PASSWORD.toCharArray());

            // You can load multiple certificates if needed
            Certificate[] trustedCertificates = new Certificate[1];
            trustedCertificates[0] = trustStore.getCertificate("root");

            DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(new InetSocketAddress(DEFAULT_PORT));
            builder.setPskStore(pskStore);
            builder.setIdentity((PrivateKey)keyStore.getKey("server", KEY_STORE_PASSWORD.toCharArray()),
                    keyStore.getCertificateChain("server"), true);
            builder.setTrustStore(trustedCertificates);
            dtlsConnector = new DTLSConnector(builder.build());
            dtlsConnector.setRawDataReceiver(new RawDataChannelImpl(dtlsConnector));

        } catch (GeneralSecurityException | IOException e) {
            LOG.log(Level.SEVERE, "Could not load the keystore", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Cannot close key store file", e);
                }
            }
        }

    }

    public void start() {
        try {
            dtlsConnector.start();
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected error starting the DTLS UDP server",e);
        }
    }

    private class RawDataChannelImpl implements RawDataChannel {

        private Connector connector;

        public RawDataChannelImpl(Connector con) {
            this.connector = con;
        }

        @Override
        public void receiveData(final RawData raw) {
            LOG.log(Level.INFO, "Received request: {0}", new String(raw.getBytes()));
            connector.send(new RawData("ACK".getBytes(), raw.getAddress(), raw.getPort()));
        }
    }

    public static void main(String[] args) {

        SampleServerDTLS server = new SampleServerDTLS();
        server.start();
    }
}
