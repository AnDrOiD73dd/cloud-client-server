package db;

import model.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FileDAOImpl implements FileDAO {

    public static final String TABLE_NAME = "files";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user";
    public static final String COLUMN_FILE_PATH = "filepath";
    public static final String COLUMN_FILE_SIZE = "filesize";
    public static final String COLUMN_FILE_DATE = "filedate";
    public static final String COLUMN_SYNCED = "synced";
    public static final String COLUMN_LAST_ACTION = "lastaction";

    private static FileDAOImpl instance;

    private FileDAOImpl() {
    }

    public static synchronized FileDAOImpl getInstance() {
        if (instance == null)
            instance = new FileDAOImpl();
        return instance;
    }

    public File create(Connection connection, File file) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?);",
                    TABLE_NAME, COLUMN_USER_ID, COLUMN_FILE_PATH, COLUMN_FILE_SIZE, COLUMN_FILE_DATE, COLUMN_SYNCED, COLUMN_LAST_ACTION));
            ps.setLong(1, file.getUserId());
            ps.setString(2, file.getFilePath());
            ps.setLong(3, file.getFileSize());
            ps.setLong(4, file.getFileDate());
            ps.setBoolean(5, file.isSynced());
            ps.setString(6, file.getLastAction());
            int count = ps.executeUpdate();
            if (count <= 0)
                return null;
            file.setId(get(connection, file.getUserId(), file.getFilePath()).getId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return file;
    }

    public File get(Connection connection, long id) {
        PreparedStatement ps = null;
        File user = null;
        String selectSQL = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = ?;",
                COLUMN_ID, COLUMN_USER_ID, COLUMN_FILE_PATH, COLUMN_FILE_SIZE, COLUMN_FILE_DATE, COLUMN_SYNCED,
                COLUMN_LAST_ACTION, TABLE_NAME, COLUMN_ID);
        try {
            ps = connection.prepareStatement(selectSQL);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user = File.map(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return user;
    }

    public File get(Connection connection, long userId, String filepath) {
        PreparedStatement ps = null;
        File user = null;
        String selectSQL = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = ? AND %s = ?;",
                COLUMN_ID, COLUMN_USER_ID, COLUMN_FILE_PATH, COLUMN_FILE_SIZE, COLUMN_FILE_DATE, COLUMN_SYNCED,
                COLUMN_LAST_ACTION, TABLE_NAME, COLUMN_USER_ID, COLUMN_FILE_PATH);
        try {
            ps = connection.prepareStatement(selectSQL);
            ps.setLong(1, userId);
            ps.setString(2, filepath);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user = File.map(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return user;
    }

    public boolean update(Connection connection, File file) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, " +
                            "%s = ?, %s = ?, %s = ? WHERE %s = ?;",
                    TABLE_NAME, COLUMN_USER_ID, COLUMN_FILE_PATH, COLUMN_FILE_SIZE, COLUMN_FILE_DATE, COLUMN_SYNCED,
                    COLUMN_LAST_ACTION, COLUMN_ID));
            ps.setLong(1, file.getUserId());
            ps.setString(2, file.getFilePath());
            ps.setLong(3, file.getFileSize());
            ps.setLong(4, file.getFileDate());
            ps.setBoolean(5, file.isSynced());
            ps.setString(6, file.getLastAction());
            ps.setLong(7, file.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return true;
    }

    public boolean delete(Connection connection, long id) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(String.format("DELETE FROM %s WHERE %s = ?;", TABLE_NAME, COLUMN_ID));
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return true;
    }

    public List<File> getAll(Connection connection) {
        return null;
    }

    public List<File> getAll(Connection connection, long userId) {
        return null;
    }
}
