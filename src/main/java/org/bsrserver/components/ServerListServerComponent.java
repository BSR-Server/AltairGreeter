package org.bsrserver.components;

import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;

public class ServerListServerComponent implements Comparable<ServerListServerComponent> {
    private final int priority;
    private final Component component;

    public ServerListServerComponent(int priority, Component component) {
        this.priority = priority;
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public int compareTo(@NotNull ServerListServerComponent o) {
        return this.priority - o.priority;
    }
}
