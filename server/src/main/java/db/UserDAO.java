package db;

import model.User;

import java.sql.Connection;
import java.util.List;

public interface UserDAO {

    User create(Connection connection, User user);
    User get(Connection connection, long id);
    User get(Connection connection, String username);
    boolean update(Connection connection, User user);
    boolean delete(Connection connection, long id);
    List<User> getAll(Connection connection);
}
