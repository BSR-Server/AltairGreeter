package org.bsrserver;

import java.sql.*;
import java.nio.file.Path;
import java.util.HashMap;

import org.slf4j.Logger;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;

import org.bsrserver.config.Config;
import org.bsrserver.components.ServerInfo;
import org.bsrserver.event.ServerConnectedEventEventListener;

@Plugin(
        id = "bsrgreeter",
        name = "BSR Greeter",
        version = "1.1.0",
        url = "https://www.bsrserver.org:8443",
        description = "A greeter",
        authors = {"Andy Zhang"}
)
public class Main {
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path dataDirectory;
    private final HashMap<String, ServerInfo> serverInfoHashMap = new HashMap<>();

    @Inject
    public Main(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        // load config
        Config.getInstance().loadConfig(dataDirectory);

        // load database
        this.loadDatabase();

        // register command
        proxyServer.getEventManager().register(this, new ServerConnectedEventEventListener(this));
    }

    private void loadDatabase() {
        try {
            // connect
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(
                    Config.getInstance().getDatabaseUrl(),
                    Config.getInstance().getDatabaseUser(),
                    Config.getInstance().getDatabasePassword()
            );
            logger.info("Successfully connected to database");

            // select
            String tableName = Config.getInstance().getDatabaseTable();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            while (resultSet.next()) {
                String serverName = resultSet.getString("server_name");
                ServerInfo serverInfo = new ServerInfo(
                        serverName,
                        resultSet.getString("named_name"),
                        resultSet.getDate("foundation_time").toLocalDate()
                );
                serverInfoHashMap.put(serverName, serverInfo);
            }
            resultSet.close();
            statement.close();
            connection.close();
            logger.info("Loaded servers: " + serverInfoHashMap.keySet());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Fail connect to database");
        }
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public HashMap<String, ServerInfo> getServerInfoHashMap() {
        return serverInfoHashMap;
    }
}
