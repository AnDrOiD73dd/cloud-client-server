package db;

import model.File;
import model.User;

import java.sql.SQLException;

public interface Cloud {

    void createTableUsers() throws SQLException;
    void createTableFiles() throws SQLException;

    void addUser(User user) throws SQLException;
    void getUser(long id);
    void updateUser(User user);
    void deleteUser(long id);

    void addFile(File file) throws SQLException;
    void getFile(long id);
    void updateFile(File file);
    void deleteFile(long id);
}
