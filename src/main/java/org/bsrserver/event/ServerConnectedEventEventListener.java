package org.bsrserver.event;

import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.event.player.ServerConnectedEvent;

import org.bsrserver.AltairGreeter;
import org.bsrserver.components.ServerListServerComponent;
import org.bsrserver.data.servers.ServerInfo;
import org.bsrserver.data.servers.ServersManager;
import org.bsrserver.data.hitokoto.QuotationsManager;

public class ServerConnectedEventEventListener {
    private final ProxyServer proxyServer;
    private final QuotationsManager quotationsManager;
    private final ServersManager serversManager;

    public ServerConnectedEventEventListener(AltairGreeter altairGreeter) {
        this.proxyServer = altairGreeter.getProxyServer();
        this.quotationsManager = altairGreeter.getQuotationsManager();
        this.serversManager = altairGreeter.getServersManager();
    }

    private ServerInfo getServerInfo(String serverName) {
        return serversManager
                .getServerInfo(serverName)
                .orElseGet(() -> new ServerInfo(serverName, serverName, LocalDate.now(), Integer.MAX_VALUE));
    }

    private String getOpenDays(ServerInfo serverInfo) {
        int daysBetween = (int) ChronoUnit.DAYS.between(serverInfo.foundationDate(), LocalDate.now());
        return "这是 " + serverInfo.givenName() + " 开服的第 " + daysBetween + " 天\n\n";
    }

    private static Component getServerNameComponent(ServerInfo currentServerInfo, ServerInfo serverInfo) {
        Component serverNameComponent;

        // current server or other server
        if (currentServerInfo.serverName().equals(serverInfo.serverName())) {
            serverNameComponent = Component.text("[§l" + serverInfo.givenName() + "§r]")
                    .hoverEvent(HoverEvent.showText(Component.text("当前服务器")));
        } else {
            serverNameComponent = Component.text("[§a" + serverInfo.givenName() + "§r]")
                    .clickEvent(ClickEvent.runCommand("/server " + serverInfo.serverName()))
                    .hoverEvent(HoverEvent.showText(Component.text("点击加入服务器 §b" + serverInfo.givenName())));
        }

        return serverNameComponent;
    }

    private Component getServerListComponent(ServerInfo currentServerInfo) {
        ArrayList<ServerListServerComponent> serverArrayList = new ArrayList<>();

        // for each server
        for (RegisteredServer registeredServer : proxyServer.getAllServers()) {
            ServerInfo serverInfo = getServerInfo(registeredServer.getServerInfo().getName());
            Component serverNameComponent = getServerNameComponent(currentServerInfo, serverInfo);

            // save to list
            serverArrayList.add(new ServerListServerComponent(serverInfo.priority(), serverNameComponent));
        }

        // sort array and return joined component
        return Component.join(
                JoinConfiguration.separator(Component.text(" ")),
                serverArrayList.stream()
                        .sorted()
                        .map(ServerListServerComponent::getComponent)
                        .toList()
        );
    }

    @Subscribe
    public void onServerConnectedEvent(ServerConnectedEvent event) {
        // get server info
        ServerInfo currentServerInfo = getServerInfo(event.getServer().getServerInfo().getName());

        Component message = Component.text("-".repeat(40) + "\n")
                .append(Component.text("§e§l" + event.getPlayer().getUsername()))
                .append(Component.text("§r, 欢迎回到 §bBSR 服务器§r！\n"))
                .append(Component.text(getOpenDays(currentServerInfo)))
                .append(Component.text("[§a一言§r] " + quotationsManager.getRandomQuotation() + "\n\n"))
                .append(getServerListComponent(currentServerInfo))
                .append(Component.text("\n"))
                .append(Component.text("-".repeat(40)));

        // send to player
        event.getPlayer().sendMessage(message);
    }
}
