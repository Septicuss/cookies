package fi.septicuss.cookies.data;

import fi.septicuss.cookies.data.cookie.CookieData;
import fi.septicuss.cookies.utils.CookieSerializationUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implements a simple key-value store, which saves cookie data as Json using {@link CookieSerializationUtils}
 */
public class SQLiteCookieDatabase implements CookieDatabase {

    private static final String DRIVER_URL = "jdbc:sqlite:%s";
    private static final String CREATE_TABLE_SQL;
    private static final String PUT_SQL;
    private static final String GET_SQL;
    private static final String DELETE_SQL;

    static {
        CREATE_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS CookieData (
                    key TEXT PRIMARY KEY,
                    data TEXT NOT NULL
                );
                """;
        PUT_SQL = """
                INSERT INTO CookieData (key, data) VALUES (?, ?)
                    ON CONFLICT (key) DO UPDATE SET data = excluded.data
                """;
        GET_SQL = """
                SELECT data FROM CookieData WHERE key = ?
                """;
        DELETE_SQL = """
                DELETE FROM CookieData WHERE key = ?
                """;
    }

    private final String filePath;
    private Connection connection;

    public SQLiteCookieDatabase(final String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void initialize() {
        try {
            this.connection = DriverManager.getConnection(String.format(DRIVER_URL, filePath));
            this.createTable();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public CookieData get(String key) {
        try (PreparedStatement statement = connection.prepareStatement(GET_SQL)) {
            statement.setString(1, key);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                final String data = result.getString("data");
                return CookieSerializationUtils.deserializeFromJson(data);
            }

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void put(String key, CookieData cookieData) {
        final String json = CookieSerializationUtils.serializeToJson(cookieData);
        try(PreparedStatement statement = connection.prepareStatement(PUT_SQL)) {
            statement.setString(1, key);
            statement.setString(2, json);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String key) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, key);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean connected() {
        return (this.connection != null);
    }

    private void createTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
