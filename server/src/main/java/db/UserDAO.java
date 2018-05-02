package db;

import adapter.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {

    User create(Connection connection, User user) throws SQLException;
    User get(Connection connection, long id);
    User get(Connection connection, String username);
    boolean update(Connection connection, User user);
    boolean delete(Connection connection, long id);
    List<User> getAll(Connection connection);
}
