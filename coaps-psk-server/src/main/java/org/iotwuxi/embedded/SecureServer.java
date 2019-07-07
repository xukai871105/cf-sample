package org.iotwuxi.embedded;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.eclipse.californium.scandium.dtls.pskstore.InMemoryPskStore;

/**
 * @author xukai
 */
public class SecureServer
{
    private static final int COAP_SECURE_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_SECURE_PORT);
    private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

    public static void main(String[] argv) {

        CoapServer server = new CoapServer();

        // 第一步 设置PSK
        InMemoryPskStore pskStore = new InMemoryPskStore();
        pskStore.setKey("identity", "password".getBytes());

        // 第二步 构造dtls connector
        DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
        builder.setAddress(new InetSocketAddress(COAP_SECURE_PORT));
        builder.setPskStore(pskStore);
        builder.setSupportedCipherSuites(CipherSuite.TLS_PSK_WITH_AES_128_CCM_8,
                CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256);
        DTLSConnector connector = new DTLSConnector(builder.build());

        // 第三步 构造安全endpoint
        CoapEndpoint.Builder secureEndpointBuilder = new CoapEndpoint.Builder();
        secureEndpointBuilder.setConnector(connector);

        // 第四步 可选 构造非安全endpoint
        CoapEndpoint.Builder nosecureEndpointBuilder = new CoapEndpoint.Builder();
        nosecureEndpointBuilder.setInetSocketAddress(new InetSocketAddress(COAP_PORT));

        // 第五步 为server增加endpoint
        server.addEndpoint(secureEndpointBuilder.build());
        server.addEndpoint(nosecureEndpointBuilder.build());
        server.start();

        for (Endpoint ep : server.getEndpoints()) {
            ep.addInterceptor(new MessageTracer());
        }

        // 增加资源
        server.add(new CoapResource("secure") {
            @Override
            public void handleGET(CoapExchange exchange) {
                exchange.respond(ResponseCode.CONTENT, "Hello Security!");
            }
        });

        server.add(new CoapResource("time") {
            @Override
            public void handleGET(CoapExchange exchange) {
                Date date = new Date();
                // 打印当前时间
                exchange.respond(ResponseCode.CONTENT, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
            }
        });


    }
}
