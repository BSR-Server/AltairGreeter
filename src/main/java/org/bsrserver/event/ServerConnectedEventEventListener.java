package org.bsrserver.event;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import org.bsrserver.Main;
import org.bsrserver.components.Sentences;
import org.bsrserver.components.ServerInfo;

public class ServerConnectedEventEventListener {
    private final ProxyServer proxyServer;
    private final HashMap<String, ServerInfo> serverInfoHashMap;
    private final Sentences sentences;

    public ServerConnectedEventEventListener(Main main) {
        this.proxyServer = main.getProxyServer();
        this.serverInfoHashMap = main.getServerInfoHashMap();
        this.sentences = main.getSentences();
    }

    private Optional<ServerInfo> getServerInfo(String serverName) {
        return Optional.ofNullable(serverInfoHashMap.get(serverName));
    }

    private String getServerInfoNamedName(ServerInfo serverInfo) {
        String namedName = serverInfo.namedName();
        return namedName != null ? namedName : serverInfo.serverName();
    }

    private String getServerInfoNamedName(String serverName) {
        Optional<ServerInfo> serverInfo = getServerInfo(serverName);
        if (serverInfo.isPresent()) {
            return getServerInfoNamedName(serverInfo.get());
        } else {
            return serverName;
        }
    }

    private LocalDate getServerInfoFoundationTime(String serverName) {
        Optional<ServerInfo> serverInfo = getServerInfo(serverName);
        if (serverInfo.isPresent()) {
            return serverInfo.get().foundationTime();
        } else {
            return LocalDate.now();
        }
    }

    private String getOpenDays(RegisteredServer server) {
        String serverName = server.getServerInfo().getName();
        int daysBetween = (int) ChronoUnit.DAYS.between(getServerInfoFoundationTime(serverName), LocalDate.now());
        return "这是 " + getServerInfoNamedName(serverName) + " 开服的第 " + daysBetween + " 天\n\n";
    }

    private Component getServerList(RegisteredServer server) {
        ArrayList<Component> components = new ArrayList<>();

        // for each server
        for (RegisteredServer registeredServer : proxyServer.getAllServers()) {
            String serverName = registeredServer.getServerInfo().getName();
            Component serverNameComponent;

            // this server or other server
            if (serverName.equals(server.getServerInfo().getName())) {
                serverNameComponent = Component.text("[§l" + getServerInfoNamedName(serverName) + "§r]")
                        .hoverEvent(HoverEvent.showText(Component.text("当前服务器")));
            } else {
                serverNameComponent = Component.text("[§a" + getServerInfoNamedName(serverName) + "§r]")
                        .clickEvent(ClickEvent.runCommand("/server " + serverName))
                        .hoverEvent(HoverEvent.showText(Component.text("点击加入服务器 §b" + getServerInfoNamedName(serverName))));
            }
            components.add(serverNameComponent);
        }

        return Component.join(JoinConfiguration.separator(Component.text(" ")), components);
    }

    @Subscribe
    public void onServerConnectedEvent(ServerConnectedEvent event) {
        Component message = Component.text("-".repeat(40) + "\n")
                .append(Component.text("§e§l" + event.getPlayer().getUsername()))
                .append(Component.text("§r, 欢迎回到 §bBSR 服务器§r！\n"))
                .append(Component.text(getOpenDays(event.getServer())))
                .append(Component.text("[§a一言§r] " + sentences.getRandomSentence() + "\n\n"))
                .append(getServerList(event.getServer()))
                .append(Component.text("\n"))
                .append(Component.text("-".repeat(40)));

        // send to player
        event.getPlayer().sendMessage(message);
    }
}
