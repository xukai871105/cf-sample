package org.iotwuxi;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        CoapServer server = new CoapServer();
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
