package org.bsrserver.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Objects;

import com.moandjiezana.toml.Toml;

public class Config {
    private static final Config config = new Config();
    private Toml configToml;

    private Config() {
    }

    public static Config getInstance() {
        return config;
    }

    public void loadConfig(Path dataDirectory) {
        // check data directory
        if (!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdir();
        }

        // check file exists
        File configFile = new File(dataDirectory.toAbsolutePath().toString(), "config.toml");
        if (!configFile.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.toml")), configFile.toPath());
            } catch (IOException ignored) {
            }
        }

        configToml = new Toml().read(configFile);
    }

    public String getBackendBaseUrl() {
        return configToml.getString("backend.baseurl");
    }

    public String getBackendSecuredClientKey() {
        return configToml.getString("backend.securedClientKey");
    }
}
