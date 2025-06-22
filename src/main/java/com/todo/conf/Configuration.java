package com.todo.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static Configuration instance;

    private final Properties properties = new Properties();

    private Configuration() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("config.properties file not found in resources");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration properties", e);
        }
    }

    private static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public static String getProperty(String key) {
        return getInstance().properties.getProperty(key);
    }

    public static void setProperty(String key, String value) {
        getInstance().properties.put(key, value);
    }
}
