package com.campussphere.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe configuration manager for CampusSphere.
 * <p>
 * Configuration values are resolved with the following precedence:
 * <ol>
 *   <li>System Environment Variables (e.g. {@code CAMPUSSPHERE_DB_PASSWORD})</li>
 *   <li>Custom properties file specified by {@code CAMPUSSPHERE_CONFIG}</li>
 *   <li>Default external file: {@code ${user.home}/.campussphere/campussphere.properties}</li>
 * </ol>
 * Placeholder values beginning with {@code "PUT_"} are treated as unconfigured.
 */
public final class AppConfig {

    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
    private static volatile AppConfig instance;

    // Configuration keys
    private static final String KEY_DB_HOST = "db.host";
    private static final String KEY_DB_PORT = "db.port";
    private static final String KEY_DB_NAME = "db.name";
    private static final String KEY_DB_USER = "db.user";
    private static final String KEY_DB_PASSWORD = "db.password";
    private static final String KEY_APP_TIMEZONE = "app.timezone";
    private static final String KEY_SETUP_KEY = "setup.key";

    // Defaults
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB_NAME = "campussphere";
    private static final String DEFAULT_DB_USER = "campussphere_app";
    private static final String DEFAULT_TIMEZONE = "Asia/Kolkata";

    private final String dbHost;
    private final String dbPort;
    private final String dbName;
    private final String dbUser;
    private final String dbPassword;
    private final String timezone;
    private final String setupKey;
    private final ZoneId zoneId;

    private AppConfig() {
        Properties fileProps = loadPropertiesFile();

        this.dbHost = resolveValue("CAMPUSSPHERE_DB_HOST", fileProps, KEY_DB_HOST, DEFAULT_HOST);
        this.dbPort = resolveValue("CAMPUSSPHERE_DB_PORT", fileProps, KEY_DB_PORT, DEFAULT_PORT);
        this.dbName = resolveValue("CAMPUSSPHERE_DB_NAME", fileProps, KEY_DB_NAME, DEFAULT_DB_NAME);
        this.dbUser = resolveValue("CAMPUSSPHERE_DB_USER", fileProps, KEY_DB_USER, DEFAULT_DB_USER);
        this.dbPassword = resolveValue("CAMPUSSPHERE_DB_PASSWORD", fileProps, KEY_DB_PASSWORD, "");
        this.timezone = resolveValue("CAMPUSSPHERE_TIMEZONE", fileProps, KEY_APP_TIMEZONE, DEFAULT_TIMEZONE);
        this.setupKey = resolveValue("CAMPUSSPHERE_SETUP_KEY", fileProps, KEY_SETUP_KEY, "");

        ZoneId parsedZone;
        try {
            parsedZone = ZoneId.of(this.timezone);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Invalid timezone configured: {0}. Falling back to {1}",
                    new Object[]{this.timezone, DEFAULT_TIMEZONE});
            parsedZone = ZoneId.of(DEFAULT_TIMEZONE);
        }
        this.zoneId = parsedZone;
    }

    /**
     * Retrieves the singleton instance of AppConfig.
     */
    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    private Properties loadPropertiesFile() {
        Properties props = new Properties();

        // 1. Check custom path in environment
        String customPath = System.getenv("CAMPUSSPHERE_CONFIG");
        File targetFile = null;

        if (customPath != null && !customPath.isBlank()) {
            targetFile = new File(customPath.trim());
        } else {
            // 2. Default: ${user.home}/.campussphere/campussphere.properties
            String userHome = System.getProperty("user.home", "");
            targetFile = new File(userHome + File.separator + ".campussphere" + File.separator + "campussphere.properties");
        }

        if (targetFile.exists() && targetFile.isFile()) {
            try (InputStream in = new FileInputStream(targetFile)) {
                props.load(in);
                LOGGER.log(Level.INFO, "Loaded CampusSphere configuration from external file: {0}", targetFile.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Could not read configuration file: {0}", targetFile.getAbsolutePath());
            }
        } else {
            LOGGER.log(Level.FINE, "External configuration file not found at: {0}", targetFile.getAbsolutePath());
        }

        return props;
    }

    private String resolveValue(String envVar, Properties fileProps, String propKey, String defaultValue) {
        // Priority 1: Environment variable
        String envVal = System.getenv(envVar);
        if (envVal != null && !envVal.isBlank()) {
            return envVal.trim();
        }

        // Priority 2: Properties file
        String fileVal = fileProps.getProperty(propKey);
        if (fileVal != null && !fileVal.isBlank()) {
            return fileVal.trim();
        }

        // Priority 3: Default fallback
        return defaultValue;
    }

    private boolean isConfiguredValue(String value) {
        return value != null && !value.isBlank() && !value.startsWith("PUT_");
    }

    /**
     * Determines whether database connection credentials have been adequately supplied.
     */
    public boolean isDatabaseConfigured() {
        return isConfiguredValue(dbHost)
                && isConfiguredValue(dbPort)
                && isConfiguredValue(dbName)
                && isConfiguredValue(dbUser)
                && isConfiguredValue(dbPassword);
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbPort() {
        return dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    /**
     * Returns the database password. Protected against accidental logging.
     */
    public String getDbPassword() {
        return dbPassword;
    }

    public String getTimezone() {
        return timezone;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    /**
     * Returns the one-time admin setup key. Protected against accidental logging.
     */
    public String getSetupKey() {
        return setupKey;
    }

    public boolean isSetupKeyConfigured() {
        return isConfiguredValue(setupKey);
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "dbHost='" + dbHost + '\'' +
                ", dbPort='" + dbPort + '\'' +
                ", dbName='" + dbName + '\'' +
                ", dbUser='" + dbUser + '\'' +
                ", dbPassword=[PROTECTED]" +
                ", timezone='" + timezone + '\'' +
                ", setupKey=[PROTECTED]" +
                '}';
    }
}
