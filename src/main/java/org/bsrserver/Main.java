package org.bsrserver;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;

@Plugin(
        id = "bsrgreeter",
        name = "BSR Greeter",
        version = "1.0.0",
        url = "https://www.bsrserver.org:8443",
        description = "A greeter",
        authors = {"Andy Zhang"}
)
public class Main {
    private final ProxyServer proxyServer;

    @Inject
    public Main(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        proxyServer.getEventManager().register(this, new EventListener(proxyServer));
    }
}
