package db;

import model.File;

import java.sql.Connection;
import java.util.List;

public interface FileDAO {

    File create(Connection connection, File file);
    File get(Connection connection, long id);
    File get(Connection connection, long userId, String filepath);
    void update(Connection connection, File file);
    void delete(Connection connection, long id);
    List<File> getAll(Connection connection);
    List<File> getAll(Connection connection, long userId);
}
