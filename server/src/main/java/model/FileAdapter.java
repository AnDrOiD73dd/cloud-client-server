package model;

import db.FileDAOImpl;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FileAdapter {

    public static File map(ResultSet rs) throws SQLException {
        return new File.Builder()
                .setId(rs.getLong(FileDAOImpl.COLUMN_ID))
                .setUserId(rs.getLong(FileDAOImpl.COLUMN_USER_ID))
                .setServerPath(rs.getString(FileDAOImpl.COLUMN_SERVER_PATH))
                .setFilePath(rs.getString(FileDAOImpl.COLUMN_FILE_PATH))
                .setFileSize(rs.getLong(FileDAOImpl.COLUMN_FILE_SIZE))
                .setFileDate(rs.getLong(FileDAOImpl.COLUMN_FILE_DATE))
                .setSynced(rs.getBoolean(FileDAOImpl.COLUMN_SYNCED))
                .setLastAction(rs.getString(FileDAOImpl.COLUMN_LAST_ACTION))
                .create();
    }
}
