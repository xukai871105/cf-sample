package org.iotwuxi;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.tcp.TcpServerConnector;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        NetworkConfig net = NetworkConfig.createStandardWithoutFile()
                .setLong(NetworkConfig.Keys.MAX_MESSAGE_SIZE, 16 * 1024)
                .setInt(NetworkConfig.Keys.PROTOCOL_STAGE_THREAD_COUNT, 2)
                .setLong(NetworkConfig.Keys.EXCHANGE_LIFETIME, 10000);

        Connector serverConnector = new TcpServerConnector(new InetSocketAddress(CoAP.DEFAULT_COAP_PORT), 1, 100);
        CoapEndpoint endpoint = new CoapEndpoint(serverConnector, net);

        CoapServer server = new CoapServer(net);
        server.addEndpoint(endpoint);
        server.addEndpoint(new CoapEndpoint(new InetSocketAddress("127.0.0.1", 5683)));

        // CoapServer server = new CoapServer();
        server.add(new CoapResource("hello") {
            @Override
            public void handleGET(CoapExchange exchange) {
                exchange.respond(CoAP.ResponseCode.CONTENT, "Hello CoAP!");
            }
        });
        server.add(new CoapResource("time") {
            @Override
            public void handleGET(CoapExchange exchange) {

                Date date = new Date();
                exchange.respond(CoAP.ResponseCode.CONTENT,
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
            }
        });
        server.start();
    }
}
