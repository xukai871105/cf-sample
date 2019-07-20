package org.iotwuxi.embedded;

import org.eclipse.californium.elements.AddressEndpointContext;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.RawDataChannel;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;



/**
 * @author xukai
 */
public class DTLSClientWithSystemIn
{
    private static final int DEFAULT_PORT = 4433;
    private static final Logger LOG = LoggerFactory.getLogger(DTLSClientWithSystemIn.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {

        DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
        byte[] psk = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F};
        builder.setPskStore(new StaticPskStore("Client_identity", psk));
        builder.setSupportedCipherSuites(CipherSuite.TLS_PSK_WITH_AES_128_CCM_8);

        DTLSConnector dtlsConnector = new DTLSConnector(builder.build());
        dtlsConnector.setRawDataReceiver(new RawDataChannel() {
            @Override
            public void receiveData(RawData raw) {
                LOG.info("Received response: {}", new String(raw.getBytes()));
            }
        });

        InetSocketAddress peer = new InetSocketAddress("localhost", DEFAULT_PORT);
        if (args.length == 2) {
            peer = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        }
        dtlsConnector.start();

        // 控制台循环输入
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            dtlsConnector.send(RawData.outbound(message.getBytes(),
                    new AddressEndpointContext(peer), null, false));
        }
    }
}
