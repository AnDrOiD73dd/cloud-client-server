package db;

import model.File;

import java.util.List;

public interface FileDAO {

    File create(File file);
    File get(long id);
    File get(long userId, String filepath);
    void update(File file);
    void delete(long id);
    List<File> getAll();
    List<File> getAll(long userId);
}
