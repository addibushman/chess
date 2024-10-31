package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to process db.properties. " + ex.getMessage());
        }
    }

    public static void createDatabase() throws DataAccessException {
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            String createDbStatement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            try (var stmt = conn.createStatement()) {
                stmt.executeUpdate(createDbStatement);
            }

            conn.setCatalog(DATABASE_NAME);
            createTables(conn);

        } catch (SQLException e) {
            throw new DataAccessException("Error creating database: " + e.getMessage());
        }
    }

    static Connection getConnection() throws DataAccessException {
        try {
            createDatabase();
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException("Error connecting to database: " + e.getMessage());
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
               username VARCHAR(50) PRIMARY KEY NOT NULL,
               hashed_password VARCHAR(100) NOT NULL,
               email VARCHAR(100),
               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;
        executePreparedStatement(conn, createUsersTable);

        String createGamesTable = """
            CREATE TABLE IF NOT EXISTS games (
               game_id INT AUTO_INCREMENT PRIMARY KEY,
               game_name VARCHAR(100) NOT NULL,
               white_player_id VARCHAR(256),
               black_player_id VARCHAR(256),
               game_state JSON,
               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;
        executePreparedStatement(conn, createGamesTable);

        String createAuthTokensTable = """
            CREATE TABLE IF NOT EXISTS auth_tokens (
                auth_token_id INT AUTO_INCREMENT PRIMARY KEY,
                auth_token VARCHAR(100) NOT NULL UNIQUE,
                username VARCHAR(50),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
        """;
        executePreparedStatement(conn, createAuthTokensTable);
    }

    private static void executePreparedStatement(Connection conn, String sql) throws SQLException {
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }
    public static void clearAllData() throws DataAccessException {
        try (Connection conn = getConnection()) {
            String[] clearStatements = {
                    "DELETE FROM auth_tokens",
                    "DELETE FROM games",
                    "DELETE FROM users"
            };
            for (String sql : clearStatements) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing database tables: " + e.getMessage());
        }
    }
}
