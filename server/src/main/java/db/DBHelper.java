package db;

import java.sql.*;

public class DBHelper implements Database {

    private Connection connection;
    private static DBHelper instance;

    private DBHelper() {
    }

    public static synchronized DBHelper getInstance() {
        if (instance == null)
            instance = new DBHelper();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void createTableUsers(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY UNIQUE NOT NULL, " +
                        "%s TEXT UNIQUE NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT UNIQUE NOT NULL, " +
                        "%s TEXT UNIQUE NOT NULL);",
                UserDAOImpl.TABLE_NAME, UserDAOImpl.COLUMN_ID, UserDAOImpl.COLUMN_USERNAME,
                UserDAOImpl.COLUMN_PASSWORD, UserDAOImpl.COLUMN_FIRST_NAME, UserDAOImpl.COLUMN_LAST_NAME,
                UserDAOImpl.COLUMN_EMAIL, UserDAOImpl.COLUMN_ROOT_DIR));
        ps.executeUpdate();
        ps.close();
    }

    public void createTableFiles(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY UNIQUE NOT NULL, " +
                        "%s INTEGER REFERENCES %s (%s) ON DELETE CASCADE NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s BIGINT, " +
                        "%s BIGINT, " +
                        "%s BOOLEAN NOT NULL DEFAULT (0), " +
                        "%s TEXT);",
                FileDAOImpl.TABLE_NAME, FileDAOImpl.COLUMN_ID, FileDAOImpl.COLUMN_USER_ID, UserDAOImpl.TABLE_NAME,
                UserDAOImpl.COLUMN_ID, FileDAOImpl.COLUMN_FILE_PATH, FileDAOImpl.COLUMN_FILE_SIZE,
                FileDAOImpl.COLUMN_FILE_DATE, FileDAOImpl.COLUMN_SYNCED, FileDAOImpl.COLUMN_LAST_ACTION));
        ps.executeUpdate();
        ps.close();
    }

    public Connection openDb() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:server/cloud.db");
        return connection;
    }

    public void closeDb(Connection connection) throws SQLException {
        connection.close();
        this.connection = null;
    }

    public void createTables(Connection connection) throws SQLException {
        createTableUsers(connection);
        createTableFiles(connection);
    }

    public void dropTable(Connection connection, String tableName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(String.format("DROP TABLE_NAME IF EXISTS %s", tableName));
        ps.executeUpdate();
        ps.close();
    }

    public void clearTable(Connection connection, String tableName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(String.format("DELETE FROM %s", tableName));
        ps.executeUpdate();
        ps.close();
    }
}
