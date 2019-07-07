package org.iotwuxi.embedded;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
import java.net.InetSocketAddress;


/**
 * @author xukai
 */
public class HelloServer {
    private static final Integer COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);
    public static void main(String[] argv) {
        // 第一步 配置文件
        NetworkConfig networkConfig = NetworkConfig.createStandardWithoutFile();
        // 第二步 创建Endpoint
        CoapEndpoint.Builder coapEndpointBuilder = new CoapEndpoint.Builder();
        coapEndpointBuilder.setNetworkConfig(networkConfig);
        coapEndpointBuilder.setInetSocketAddress(new InetSocketAddress(COAP_PORT));

        // 第三步 创建CoAP服务器 并绑定Endpoint
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

        // 最后，启动服务器
        server.start();
    }
}
