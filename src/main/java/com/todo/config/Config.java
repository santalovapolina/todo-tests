package com.todo.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Config instance;
    private final Properties properties = new Properties();

    private Config() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
        if (input == null) {
            throw new RuntimeException("Не найден файл config.properties");
        }
        try {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении config.properties", e);
        }
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
