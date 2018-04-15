package db;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {

    Connection openDb() throws ClassNotFoundException, SQLException;
    void closeDb(Connection connection) throws SQLException;
    void createTables(Connection connection) throws SQLException;
    void dropTable(Connection connection, String tableName) throws SQLException, ClassNotFoundException;
    void clearTable(Connection connection, String tableName) throws SQLException, ClassNotFoundException;

    void createTableUsers(Connection connection) throws SQLException;
    void createTableFiles(Connection connection) throws SQLException;
}
