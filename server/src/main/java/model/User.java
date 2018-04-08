package model;

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
}
