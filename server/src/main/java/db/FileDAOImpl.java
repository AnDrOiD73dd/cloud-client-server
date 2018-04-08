package db;

import model.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FileDAOImpl implements FileDAO {

    public static final String TABLE_FILES = "files";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_FILE_PATH = "filepath";
    public static final String COLUMN_FILE_SIZE = "filesize";
    public static final String COLUMN_FILE_DATE = "filedate";
    public static final String COLUMN_SYNCED = "synced";
    public static final String COLUMN_LAST_ACTION = "lastaction";

    private PreparedStatement psFiles;

    public File create(File file) {
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = DBHelper.getInstance().openDb();
            psFiles = connection.prepareStatement(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?);",
                    TABLE_FILES, COLUMN_USER, COLUMN_FILE_PATH, COLUMN_FILE_SIZE, COLUMN_FILE_DATE, COLUMN_SYNCED, COLUMN_LAST_ACTION));
            psFiles.setLong(1, file.getUserId());
            psFiles.setString(2, file.getFilePath());
            psFiles.setLong(3, file.getFileSize());
            psFiles.setLong(4, file.getFileDate());
            psFiles.setBoolean(5, file.isSynced());
            psFiles.setString(6, file.getLastAction());
            rs = psFiles.executeQuery();
            file.setId(rs.getLong(COLUMN_ID));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                DBHelper.getInstance().closeDb();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (rs == null)
            return null;
        return file;
    }

    public File get(long id) {
        return null;
    }

    public File get(long userId, String filepath) {
        return null;
    }

    public void update(File file) {

    }

    public void delete(long id) {

    }

    public List<File> getAll() {
        return null;
    }

    public List<File> getAll(long userId) {
        return null;
    }
}
