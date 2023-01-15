package org.bsrserver;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EventListener {
    ProxyServer proxyServer;

    private int getOpenDays() {
        return (int) ChronoUnit.DAYS.between(LocalDate.of(2021, 1, 10), LocalDate.now());
    }

    private String getSentence() {
        try {
            // request
            URL url = new URL("https://www.bsrserver.org:8443/static?prefix=pages/gifs/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(connection.getInputStream());
            connection.disconnect();

            // parse
            NodeList nodes = document.getFirstChild().getChildNodes();
            ArrayList<String> sentences = new ArrayList<>(nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeName().equals("Contents")) {
                    String text = node.getFirstChild().getTextContent();
                    text = text
                            .replaceAll("^pages/gifs/", "")
                            .replaceAll(".jpg$", "")
                            .replaceAll(".png$", "");
                    sentences.add(text);
                }
            }

            // return
            return sentences.get((int) (Math.random() * sentences.size()));
        } catch (Exception exception) {
            exception.printStackTrace();
            return "";
        }
    }

    private Component getServerList(RegisteredServer server) {
        ArrayList<Component> components = new ArrayList<>();

        // for each server
        for (RegisteredServer registeredServer : proxyServer.getAllServers()) {
            String serverName = registeredServer.getServerInfo().getName();
            Component serverNameComponent;

            // this server or other server
            if (serverName.equals(server.getServerInfo().getName())) {
                serverNameComponent = Component.text("[§l" + serverName + "§r]")
                        .hoverEvent(HoverEvent.showText(Component.text("当前服务器")));
            } else {
                serverNameComponent = Component.text("[§a" + serverName + "§r]")
                        .clickEvent(ClickEvent.runCommand("/server " + serverName))
                        .hoverEvent(HoverEvent.showText(Component.text("点击加入服务器 §b" + serverName)));
            }
            components.add(serverNameComponent);
        }

        return Component.join(JoinConfiguration.separator(Component.text(" ")), components);
    }

    public EventListener(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void onServerConnectedEvent(ServerConnectedEvent event) {
        Component message = Component.text("-".repeat(40) + "\n")
                .append(Component.text("§e§l" + event.getPlayer().getUsername()))
                .append(Component.text("§r, 欢迎回到 §bBSR 服务器§r！\n"))
                .append(Component.text("这是 BSR 服务器开服的第 " + getOpenDays() + " 天\n\n"))
                .append(Component.text("[§a一言§r] " + getSentence() + "\n\n"))
                .append(getServerList(event.getServer()))
                .append(Component.text("\n"))
                .append(Component.text("-".repeat(40)));

        // send to player
        event.getPlayer().sendMessage(message);
    }
}
