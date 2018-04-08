package db;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_ROOT_DIR = "rootdir";

    private PreparedStatement psUser;

    public User create(User user) {
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = DBHelper.getInstance().openDb();
            psUser = connection.prepareStatement(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?);",
                    TABLE_USERS, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_EMAIL, COLUMN_ROOT_DIR));
            psUser.setString(1, user.getUsername());
            psUser.setString(2, user.getPassword());
            psUser.setString(3, user.getFirstName());
            psUser.setString(4, user.getLastName());
            psUser.setString(5, user.getEmail());
            psUser.setString(6, user.getRootDir());
            rs = psUser.executeQuery();
            user.setId(rs.getLong(COLUMN_ID));
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
        return user;
    }

    public User get(long id) {
        return null;
    }

    public User get(String username) {
        return null;
    }

    public boolean update(User user) {
        return false;
    }

    public boolean delete(long id) {
        return false;
    }

    public List<User> getAll() {
        return null;
    }
}
