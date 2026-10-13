package Config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Membaca db.properties dan menyediakan koneksi JDBC.
 */
public class Koneksi {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Koneksi.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("File db.properties tidak ditemukan di resources.");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Gagal membaca db.properties: " + e.getMessage(), e);
        }
    }

    private Koneksi() { }

    public static String getProperty(String key, String defaultValue) {
        return PROPS.getProperty(key, defaultValue);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPS.getProperty("db.url"),
                PROPS.getProperty("db.user"),
                PROPS.getProperty("db.password", ""));
    }
}
