import db.DBHelper;
import db.FileDAOImpl;
import db.UserDAOImpl;
import model.File;
import model.User;

import java.sql.Connection;
import java.sql.SQLException;

public class CloudServer {
    public static void main(String[] args) {
        try {
            Connection connection = DBHelper.getInstance().openDb();
            DBHelper.getInstance().createTables();

            User user = new User.Builder()
                    .setUsername("test1")
                    .setPassword("pass")
                    .setFirstName("first name")
                    .setLastName("last name")
                    .setEmail("email")
                    .setRootDir("/home/sdfsd")
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
            UserDAOImpl.getInstance().create(connection, user);
            FileDAOImpl.getInstance().create(connection, file);
            // UPDATE
            user.setEmail("android@mail.ru");
            UserDAOImpl.getInstance().update(connection, user);
            file.setFilePath("/root/home/evgeny/aaa.txt");
            FileDAOImpl.getInstance().update(connection, file);

            // CREATE
            user.setUsername("Thomas");
            user.setRootDir("/root/home/thomas");
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
            DBHelper.getInstance().clearTable(UserDAOImpl.TABLE_NAME);
            DBHelper.getInstance().clearTable(FileDAOImpl.TABLE_NAME);

            DBHelper.getInstance().closeDb();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
