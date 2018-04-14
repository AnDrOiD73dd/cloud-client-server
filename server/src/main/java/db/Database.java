package db;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {

    Connection openDb() throws ClassNotFoundException, SQLException;
    void closeDb() throws SQLException;
    void createTables() throws SQLException;
    void dropTable(String tableName) throws SQLException, ClassNotFoundException;
    void clearTable(String tableName) throws SQLException, ClassNotFoundException;

    void createTableUsers() throws SQLException;
    void createTableFiles() throws SQLException;
}
