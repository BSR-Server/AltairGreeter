package org.bsrserver.data.servers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import org.bsrserver.AltairGreeter;
import org.bsrserver.config.Config;

public class ServersManager {
    private final HashMap<String, ServerInfo> servers = new HashMap<>();
    private final Logger logger;

    public ServersManager(AltairGreeter altairGreeter) {
        this.logger = altairGreeter.getLogger();
        altairGreeter.getScheduledExecutorService().scheduleAtFixedRate(this::scheduledTask, 0, 30, TimeUnit.SECONDS);
    }

    private void scheduledTask() {
        try {
            updateServers();
        } catch (Exception e) {
            logger.error("Failed to get servers", e);
        }
    }

    private void updateServers() {
        // clear
        servers.clear();

        // request
        String authorization = "KeySecuredClient " + Config.getInstance().getBackendSecuredClientKey();
        OkHttpClient client = new OkHttpClient();
        Request getRequest = new Request.Builder()
                .url(Config.getInstance().getBackendBaseUrl() + "/v1/minecraft/servers")
                .header("Authorization", authorization)
                .build();

        // get servers
        JSONArray serversJSONArray = null;
        try {
            Response response = client.newCall(getRequest).execute();
            if (response.body() != null) {
                serversJSONArray = JSONObject
                        .parseObject(response.body().string())
                        .getJSONObject("data")
                        .getJSONArray("servers");
            }
        } catch (IOException exception) {
            logger.error("Failed to get servers", exception);
        }

        // save servers
        if (serversJSONArray != null) {
            for (JSONObject server : serversJSONArray.toArray(JSONObject.class)) {
                servers.put(
                        server.getString("serverName"),
                        new ServerInfo(
                                server.getString("serverName"),
                                server.getString("givenName"),
                                LocalDate.parse(server.getString("foundationDate")),
                                server.getInteger("priority")
                        )
                );
            }
        }
    }

    public Optional<ServerInfo> getServerInfo(String serverName) {
        return Optional.ofNullable(servers.get(serverName));
    }
}
