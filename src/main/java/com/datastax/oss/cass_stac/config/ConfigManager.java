package com.datastax.oss.cass_stac.config;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public class ConfigManager {
    private static ConfigManager instance;
    private final Properties defaultProperties;
    private final Properties overrideProperties;

    private ConfigManager() {
        defaultProperties = new Properties();
        overrideProperties = new Properties();
        loadProperties();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadProperties() {
        // Load the bundled config.properties from the classpath
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                defaultProperties.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading bundled configuration properties", e);
        }

        // Attempt to load properties from the path specified by the system property
        String configFile = System.getProperty("config.file");
        if (configFile != null && Files.exists(Paths.get(configFile))) {
            try (InputStream input = new FileInputStream(configFile)) {
                overrideProperties.load(input);
            } catch (IOException e) {
                System.err.println("Warning: Specified config file could not be loaded: " + configFile);
            }
        }
    }

    public String getProperty(@NotNull String key, String defaultValue) {
        // First, check environment variables
        String value = System.getenv(key.replace('.', '_').toUpperCase());

        // Second, check the override properties
        if (value == null || value.isEmpty()) {
            value = overrideProperties.getProperty(key);
        }

        // Lastly, check the default properties
        if (value == null || value.isEmpty()) {
            value = defaultProperties.getProperty(key, defaultValue);
        }

        return value;
    }

    public String getProperty(String key) {
        return getProperty(key, null);
    }

    public List<String> getPropertyAsList(String key, String defaultValue) {
        String value = getProperty(key,defaultValue);
        if (null==value || value.isEmpty())
            return List.of();
        else
            return List.of(value.split(","));
    }

    public List<String> getPropertyAsList(String key) {
        return getPropertyAsList(key, null);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        return (value == null || value.isEmpty()) ? defaultValue : Integer.parseInt(value);
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return (value == null || value.isEmpty()) ? defaultValue : Boolean.parseBoolean(value);
    }
}