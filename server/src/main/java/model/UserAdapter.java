package model;

import db.UserDAOImpl;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAdapter {

    public static User map(ResultSet rs) throws SQLException {
        return new User.Builder()
                .setId(rs.getLong(UserDAOImpl.COLUMN_ID))
                .setUsername(rs.getString(UserDAOImpl.COLUMN_USERNAME))
                .setPassword(rs.getString(UserDAOImpl.COLUMN_PASSWORD))
                .setFirstName(rs.getString(UserDAOImpl.COLUMN_FIRST_NAME))
                .setLastName(rs.getString(UserDAOImpl.COLUMN_LAST_NAME))
                .setEmail(rs.getString(UserDAOImpl.COLUMN_EMAIL))
                .setRootDir(rs.getString(UserDAOImpl.COLUMN_ROOT_DIR))
                .create();
    }
}
