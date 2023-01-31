package org.bsrserver.components;

import java.time.LocalDate;

public record ServerInfo(
        String serverName,
        String namedName,
        LocalDate foundationTime
) {
    @Override
    public String toString() {
        return "ServerInfo{" +
                "serverName='" + serverName + '\'' +
                ", namedName='" + namedName + '\'' +
                ", foundationTime=" + foundationTime +
                '}';
    }
}
