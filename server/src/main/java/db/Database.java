package db;

import java.sql.SQLException;

public interface Database {

    void openDb() throws ClassNotFoundException, SQLException;
    void closeDb() throws SQLException;
    void createTables() throws SQLException;
    void dropTable(String tableName) throws SQLException;
    void clearTable(String tableName) throws SQLException;
}
