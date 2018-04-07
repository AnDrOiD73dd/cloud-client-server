package db;

import model.File;
import model.User;

import java.sql.*;

public class DBHelper implements Database, Cloud {

    public static final String TABLE_USERS = "users";
    public static final String USERS_COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_ROOT_DIR = "rootdir";

    public static final String TABLE_FILES = "files";
    public static final String FILES_COLUMN_ID = "id";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_FILE_PATH = "filepath";
    public static final String COLUMN_FILE_SIZE = "filesize";
    public static final String COLUMN_FILE_DATE = "filedate";
    public static final String COLUMN_SYNCED = "synced";
    public static final String COLUMN_LAST_ACTION = "lastaction";

    private Connection connection;
    private Statement stmt;
    private PreparedStatement psUser;
    private PreparedStatement psFiles;

    public void createTableUsers() throws SQLException {
        stmt.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY UNIQUE NOT NULL, " +
                        "%s TEXT UNIQUE NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT, " +
                        "%s TEXT UNIQUE NOT NULL);",
                TABLE_USERS, USERS_COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_FIRST_NAME, COLUMN_LAST_NAME,
                COLUMN_EMAIL, COLUMN_ROOT_DIR));
    }

    public void createTableFiles() throws SQLException {
        stmt.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY UNIQUE NOT NULL, " +
                        "%s INTEGER REFERENCES %s (%s) ON DELETE CASCADE NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s BIGINT, " +
                        "%s DATETIME, " +
                        "%s BOOLEAN NOT NULL DEFAULT (0), " +
                        "%s TEXT);",
                TABLE_FILES, FILES_COLUMN_ID, COLUMN_USER, TABLE_USERS, USERS_COLUMN_ID, COLUMN_FILE_PATH,
                COLUMN_FILE_SIZE, COLUMN_FILE_DATE, COLUMN_SYNCED, COLUMN_LAST_ACTION));
    }

    public void addUser(User user) throws SQLException {
        psUser = connection.prepareStatement(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?);",
                TABLE_USERS, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_EMAIL, COLUMN_ROOT_DIR));
        psUser.setString(1, user.username);
        psUser.setString(2, user.password);
        psUser.setString(3, user.firstName);
        psUser.setString(4, user.lastName);
        psUser.setString(5, user.email);
        psUser.setString(6, user.rootDir);
        ResultSet rs = psUser.executeQuery();
    }

    public void getUser(long id) {

    }

    public void updateUser(User user) {

    }

    public void deleteUser(long id) {

    }

    public void addFile(File file) throws SQLException {
        psFiles = connection.prepareStatement(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?);",
                TABLE_FILES, COLUMN_USER, COLUMN_FILE_PATH, COLUMN_FILE_SIZE, COLUMN_FILE_DATE, COLUMN_SYNCED, COLUMN_LAST_ACTION));
        psUser.setLong(1, file.user);
        psUser.setString(2, file.filePath);
        psUser.setString(3, file.fileSize);
        psUser.setString(4, file.fileDate);
        psUser.setBoolean(5, file.synced);
        psUser.setString(6, file.lastAction);
        ResultSet rs = psUser.executeQuery();
    }

    public void getFile(long id) {

    }

    public void updateFile(File file) {

    }

    public void deleteFile(long id) {

    }

    public void openDb() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:server/cloud.db");
        stmt = connection.createStatement();
    }

    public void closeDb() throws SQLException {
        connection.close();
    }

    public void createTables() throws SQLException {
        createTableUsers();
        createTableFiles();
    }

    public void dropTable(String tableName) throws SQLException {
        stmt.executeUpdate(String.format("DROP TABLE IF EXISTS %s", tableName));
    }

    public void clearTable(String tableName) throws SQLException {
        stmt.executeUpdate(String.format("DELETE FROM %s", tableName));
    }
}
