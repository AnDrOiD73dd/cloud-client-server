package db;

import java.sql.*;

public class DBHelper implements Database {

    private Statement stmt;

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

    public void createTableUsers() throws SQLException {
        if (stmt == null)
            stmt = connection.createStatement();
        stmt.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY UNIQUE NOT NULL, " +
                        "%s TEXT UNIQUE NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT, " +
                        "%s TEXT UNIQUE NOT NULL);",
                UserDAOImpl.TABLE_NAME, UserDAOImpl.COLUMN_ID, UserDAOImpl.COLUMN_USERNAME,
                UserDAOImpl.COLUMN_PASSWORD, UserDAOImpl.COLUMN_FIRST_NAME, UserDAOImpl.COLUMN_LAST_NAME,
                UserDAOImpl.COLUMN_EMAIL, UserDAOImpl.COLUMN_ROOT_DIR));
    }

    public void createTableFiles() throws SQLException {
        if (stmt == null)
            stmt = connection.createStatement();
        stmt.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %s (" +
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
    }

    public Connection openDb() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:server/cloud.db");
        return connection;
    }

    public void closeDb() throws SQLException {
        connection.close();
        connection = null;
    }

    public void createTables() throws SQLException {
        createTableUsers();
        createTableFiles();
    }

    public void dropTable(String tableName) throws SQLException, ClassNotFoundException {
        if (connection == null)
            openDb();
        if (stmt == null)
            stmt = connection.createStatement();
        stmt.executeUpdate(String.format("DROP TABLE_NAME IF EXISTS %s", tableName));
    }

    public void clearTable(String tableName) throws SQLException, ClassNotFoundException {
        if (connection == null)
            openDb();
        if (stmt == null)
            stmt = connection.createStatement();
        stmt.executeUpdate(String.format("DELETE FROM %s", tableName));
    }
}
