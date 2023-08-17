package org.bsrserver.data.servers;

import java.time.LocalDate;

public record ServerInfo(
        String serverName,
        String givenName,
        LocalDate foundationDate,
        int priority
) {
    @Override
    public String toString() {
        return "ServerInfo{" +
                "serverName='" + serverName + '\'' +
                ", givenName='" + givenName + '\'' +
                ", foundationDate=" + foundationDate +
                ", priority=" + priority +
                '}';
    }
}
