package org.bsrserver;

import java.sql.Date;

public record ServerInfo(
        String serverName,
        String namedName,
        Date foundationTime
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
