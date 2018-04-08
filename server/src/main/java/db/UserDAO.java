package db;

import model.User;

import java.util.List;

public interface UserDAO {

    User create(User user);
    User get(long id);
    User get(String username);
    boolean update(User user);
    boolean delete(long id);
    List<User> getAll();
}
