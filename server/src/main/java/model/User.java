package model;

import db.UserDAOImpl;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    private long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String rootDir;

    private User(long id, String username, String password, String firstName, String lastName, String email, String rootDir) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.rootDir = rootDir;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRootDir() {
        return rootDir;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", rootDir='" + rootDir + '\'' +
                '}';
    }

    public static class Builder {
        private long id;
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String email;
        private String rootDir;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setRootDir(String rootDir) {
            this.rootDir = rootDir;
            return this;
        }

        public User create() {
            return new User(id, username, password, firstName, lastName, email, rootDir);
        }
    }

    public static User map(ResultSet rs) throws SQLException {
        return new Builder()
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
