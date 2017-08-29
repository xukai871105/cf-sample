package org.iotwuxi.embedded;

import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.RawDataChannel;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ScandiumLogger;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.InMemoryPskStore;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SampleClientDTLS
{
    static {
        ScandiumLogger.initialize();
        ScandiumLogger.setLevel(Level.FINE);
    }

    private static final int DEFAULT_PORT = 5684;
    private static final Logger LOG = Logger.getLogger(SampleClientDTLS.class.getName());
    private static final String TRUST_STORE_PASSWORD = "rootPass";
    private static final String KEY_STORE_PASSWORD = "endPass";
    private static final String KEY_STORE_LOCATION = "certs/keyStore.jks";
    private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks";

    private DTLSConnector dtlsConnector;

    public SampleClientDTLS(final CountDownLatch latch) {
        InputStream inTrust = null;
        InputStream in = null;
        try {
            // load key store
            KeyStore keyStore = KeyStore.getInstance("JKS");
            in = getClass().getClassLoader().getResourceAsStream(KEY_STORE_LOCATION);
            keyStore.load(in, KEY_STORE_PASSWORD.toCharArray());
            in.close();

            // load trust store
            KeyStore trustStore = KeyStore.getInstance("JKS");
            inTrust = getClass().getClassLoader().getResourceAsStream(TRUST_STORE_LOCATION);
            trustStore.load(inTrust, TRUST_STORE_PASSWORD.toCharArray());

            // You can load multiple certificates if needed
            Certificate[] trustedCertificates = new Certificate[1];
            trustedCertificates[0] = trustStore.getCertificate("root");

            DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(new InetSocketAddress(0));
            builder.setPskStore(new StaticPskStore("Client_identity", "secretPSK".getBytes()));
            builder.setIdentity((PrivateKey)keyStore.getKey("client", KEY_STORE_PASSWORD.toCharArray()),
                    keyStore.getCertificateChain("client"), true);
            builder.setTrustStore(trustedCertificates);
            dtlsConnector = new DTLSConnector(builder.build());
            dtlsConnector.setRawDataReceiver(new RawDataChannel() {

                @Override
                public void receiveData(RawData raw) {
                    LOG.log(Level.INFO, "Received response: {0}", new String(raw.getBytes()));
                    latch.countDown();
                    dtlsConnector.destroy();
                }
            });

        } catch (GeneralSecurityException | IOException e) {
            LOG.log(Level.SEVERE, "Could not load the keystore", e);
        } finally {
            try {
                if (inTrust != null) {
                    inTrust.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Cannot close key store file", e);
            }
        }
    }

    private void test(InetSocketAddress peer) {
        try {
            dtlsConnector.start();
            dtlsConnector.send(new RawData("HELLO WORLD".getBytes(), peer));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Cannot send message", e);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);
        SampleClientDTLS client = new SampleClientDTLS(latch);
        InetSocketAddress peer = new InetSocketAddress("localhost", DEFAULT_PORT);
        if (args.length == 2) {
            peer = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        }
        client.test(peer);
        latch.await(5, TimeUnit.SECONDS);
    }
}
