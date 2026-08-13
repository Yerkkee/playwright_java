package com.yerkkee.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads test configuration from config.properties.
 * System properties (-Dkey=value) override values from the file.
 *
 * Usage:
 *   String url = TestConfig.get("base.url");
 *   int timeout = TestConfig.getInt("default.timeout");
 */
public final class TestConfig {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();

    // Static initializer - runs ONCE when class is first loaded
    static {
        try (InputStream input = TestConfig.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if (input == null) {
                throw new IllegalStateException(
                        "Cannot find " + CONFIG_FILE + " in classpath");
            }
            properties.load(input);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load " + CONFIG_FILE, e);
        }
    }

    // Private constructor - prevents instantiation (utility class)
    private TestConfig() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Get config value as String.
     * Priority: system property > config.properties.
     */
    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isEmpty()) {
            return systemValue;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalStateException(
                    "Missing config key: " + key);
        }
        return value;
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}