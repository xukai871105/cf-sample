package org.iotwuxi.embedded;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;


import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xukai
 */
public class HelloServer {
    private static final Integer COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);
    public static void main(String[] argv) {

        NetworkConfig networkConfig = NetworkConfig.createStandardWithoutFile();
        CoapEndpoint.Builder coapEndpointBuilder = new CoapEndpoint.Builder();
        coapEndpointBuilder.setNetworkConfig(networkConfig);
        coapEndpointBuilder.setInetSocketAddress(new InetSocketAddress("127.0.0.1", COAP_PORT));

        CoapServer server = new CoapServer();
        server.addEndpoint(coapEndpointBuilder.build());

        // 增加CoAP资源的快速方法
        server.add(new CoapResource("hello") {
            @Override
            public void handleGET(CoapExchange exchange) {
                exchange.respond(CoAP.ResponseCode.CONTENT, "Hello CoAP!");
            }
        });

        // 增加CoAP资源的常规方法
        server.add(new TimeResource());

        server.start();
    }
}
