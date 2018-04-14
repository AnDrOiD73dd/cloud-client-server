package db;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    public static final String TABLE_NAME = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_ROOT_DIR = "rootdir";

    private static UserDAOImpl instance;

    private UserDAOImpl() {
    }

    public static synchronized UserDAOImpl getInstance() {
        if (instance == null)
            instance = new UserDAOImpl();
        return instance;
    }

    public User create(Connection connection, User user) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?);",
                    TABLE_NAME, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_EMAIL, COLUMN_ROOT_DIR));
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getRootDir());
            int count = ps.executeUpdate();
            if (count <= 0)
                return null;
            user.setId(get(connection, user.getUsername()).getId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return user;
    }

    public User get(Connection connection, long id) {
        PreparedStatement ps = null;
        User user = null;
        String selectSQL = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = ?;",
                COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_EMAIL,
                COLUMN_ROOT_DIR, TABLE_NAME, COLUMN_ID);
        try {
            ps = connection.prepareStatement(selectSQL);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user = User.map(rs);
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

    public User get(Connection connection, String username) {
        PreparedStatement ps = null;
        User user = null;
        String selectSQL = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = ?;",
                COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_EMAIL,
                COLUMN_ROOT_DIR, TABLE_NAME, COLUMN_USERNAME);
        try {
            ps = connection.prepareStatement(selectSQL);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user = User.map(rs);
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

    public boolean update(Connection connection, User user) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, " +
                            "%s = ?, %s = ?, %s = ? WHERE %s = ?;",
                    TABLE_NAME, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_EMAIL,
                    COLUMN_ROOT_DIR, COLUMN_ID));
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getRootDir());
            ps.setLong(7, user.getId());
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

    public List<User> getAll(Connection connection) {
        List<User> users = new ArrayList<User>();
        PreparedStatement ps = null;
        String selectSQL = String.format("SELECT * FROM %s;", TABLE_NAME);
        try {
            ps = connection.prepareStatement(selectSQL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = User.map(rs);
                users.add(user);
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
        return users;
    }
}
