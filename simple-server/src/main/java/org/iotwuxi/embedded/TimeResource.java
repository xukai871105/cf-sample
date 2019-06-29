package org.iotwuxi.embedded;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeResource extends CoapResource {

    public TimeResource() {
        super("time");
        setObservable(true);
        setObserveType(CoAP.Type.CON);

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(new Runnable() {
            public void run() {
                changed();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        exchange.setMaxAge(10);
        Date date = new Date();
        exchange.respond(CoAP.ResponseCode.CONTENT,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
    }

    @Override
    public void handleDELETE(CoapExchange exchange) {
        delete();
        exchange.respond(CoAP.ResponseCode.DELETED);
    }
}
