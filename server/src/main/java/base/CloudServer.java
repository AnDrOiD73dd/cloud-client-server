package base;

import adapter.File;
import adapter.User;
import db.DBHelper;
import db.FileDAOImpl;
import db.UserDAOImpl;

import java.sql.Connection;
import java.sql.SQLException;

public class CloudServer {

    public static void main(String[] args) {
//        test();
        ConnectionHandler.getInstance().prepareDb();
        ConnectionHandler.getInstance().listenConnections();
    }

    private static void test() {
        try {
            Connection connection = DBHelper.getInstance().openDb();
            DBHelper.getInstance().createTables(connection);

            User user = new User.Builder()
                    .setUsername("test1")
                    .setPassword("pass")
                    .setFirstName("first name")
                    .setLastName("last name")
                    .setEmail("email")
                    .create();

            File file = new File.Builder()
                    .setUserId(1)
                    .setFileDate(324234)
                    .setFilePath("/root/sdfsd/sdfs")
                    .setFileSize(1234564567)
                    .setSynced(false)
                    .setLastAction("last action")
                    .create();
            // CREATE
            User res = UserDAOImpl.getInstance().create(connection, user);
            FileDAOImpl.getInstance().create(connection, file);
            // UPDATE
            res.setEmail("android@mail.ru");
            UserDAOImpl.getInstance().update(connection, res);
            file.setFilePath("/root/home/evgeny/aaa.txt");
            FileDAOImpl.getInstance().update(connection, file);

            // CREATE
            user.setUsername("Thomas");
            UserDAOImpl.getInstance().create(connection, user);
            file.setFilePath("/thomas/path/to/file");
            FileDAOImpl.getInstance().create(connection, file);
            // GET ALL
            System.out.println(UserDAOImpl.getInstance().getAll(connection));
            System.out.println(FileDAOImpl.getInstance().getAll(connection));

            // DELETE
            UserDAOImpl.getInstance().delete(connection, user.getId());
            FileDAOImpl.getInstance().delete(connection, file.getId());

            // CLEAR TABLES
            DBHelper.getInstance().clearTable(connection, UserDAOImpl.TABLE_NAME);
            DBHelper.getInstance().clearTable(connection, FileDAOImpl.TABLE_NAME);

            DBHelper.getInstance().closeDb(connection);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
