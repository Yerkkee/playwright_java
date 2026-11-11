package com.yerkkee.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * Typed access to test configuration.
 *
 * <p>Resolution order for every key (first non-blank value wins):
 * <ol>
 *   <li>JVM system property: {@code -Dbase.url=...}</li>
 *   <li>Environment variable: {@code BASE_URL=...} (dots become underscores, upper case) - handy in CI</li>
 *   <li>{@code config.properties} on the classpath</li>
 * </ol>
 *
 * <p>Callers use typed methods ({@link #headless()}, {@link #browser()}) instead of raw string keys,
 * so a typo in a key name is a compile error, not a runtime surprise.
 */
public final class TestConfig {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties FILE_PROPERTIES = loadFile();

    private TestConfig() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ---------- Application ----------

    public static String baseUrl() {
        return get("base.url");
    }

    /** Shared password of SauceDemo demo users. Override in CI via env USER_PASSWORD. */
    public static String userPassword() {
        return get("user.password");
    }

    // ---------- Browser ----------

    public static BrowserName browser() {
        return BrowserName.from(get("browser"));
    }

    public static boolean headless() {
        return getBoolean("headless");
    }

    /** Delay in ms added to every Playwright action. 0 for CI, ~500 for demos. */
    public static double slowMo() {
        return getInt("slow.mo");
    }

    public static int viewportWidth() {
        return getInt("viewport.width");
    }

    public static int viewportHeight() {
        return getInt("viewport.height");
    }

    // ---------- Timeouts (ms) ----------

    public static double defaultTimeout() {
        return getInt("default.timeout");
    }

    public static double navigationTimeout() {
        return getInt("navigation.timeout");
    }

    // ---------- Failure artifacts ----------

    public static boolean screenshotOnFailure() {
        return getBoolean("screenshot.on.failure");
    }

    public static boolean traceOnFailure() {
        return getBoolean("trace.on.failure");
    }

    public static boolean videoOnFailure() {
        return getBoolean("video.on.failure");
    }

    // ---------- Generic resolution ----------

    /**
     * Resolves a raw value: system property > environment variable > config file.
     *
     * @throws IllegalStateException if the key is not defined anywhere
     */
    public static String get(String key) {
        String value = System.getProperty(key);
        if (isBlank(value)) {
            value = System.getenv(toEnvName(key));
        }
        if (isBlank(value)) {
            value = FILE_PROPERTIES.getProperty(key);
        }
        if (isBlank(value)) {
            throw new IllegalStateException("Missing config key '" + key
                    + "': set -D" + key + ", env " + toEnvName(key) + " or add it to " + CONFIG_FILE);
        }
        return value.trim();
    }

    private static int getInt(String key) {
        String value = get(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    "Config key '" + key + "' must be an integer, but was '" + value + "'", e);
        }
    }

    /** Strict parsing: Boolean.parseBoolean("yes") silently returns false, which hides typos. */
    private static boolean getBoolean(String key) {
        String value = get(key).toLowerCase(Locale.ROOT);
        return switch (value) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new IllegalStateException(
                    "Config key '" + key + "' must be true/false, but was '" + value + "'");
        };
    }

    private static String toEnvName(String key) {
        return key.replace('.', '_').toUpperCase(Locale.ROOT);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static Properties loadFile() {
        Properties properties = new Properties();
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Cannot find " + CONFIG_FILE + " on the classpath");
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + CONFIG_FILE, e);
        }
    }
}
