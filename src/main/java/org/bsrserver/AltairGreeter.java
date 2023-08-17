package org.bsrserver;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;

import org.bsrserver.config.Config;
import org.bsrserver.data.hitokoto.QuotationsManager;
import org.bsrserver.data.servers.ServersManager;
import org.bsrserver.event.ServerConnectedEventEventListener;

@Plugin(
        id = "altairgreeter",
        name = "Altair Greeter",
        version = "1.4.0",
        url = "https://www.bsrserver.org:8443",
        description = "A greeter",
        authors = {"Andy Zhang"}
)
public class AltairGreeter {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final QuotationsManager quotationsManager;
    private final ServersManager serversManager;

    @Inject
    public AltairGreeter(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;

        // load config
        Config.getInstance().loadConfig(dataDirectory);

        // init data
        this.quotationsManager = new QuotationsManager(this);
        this.serversManager = new ServersManager(this);
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        // register command
        proxyServer.getEventManager().register(this, new ServerConnectedEventEventListener(this));
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public Logger getLogger() {
        return logger;
    }

    public QuotationsManager getQuotationsManager() {
        return quotationsManager;
    }

    public ServersManager getServersManager() {
        return serversManager;
    }
}
