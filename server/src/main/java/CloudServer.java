import db.DBHelper;
import db.FileDAOImpl;
import db.UserDAOImpl;
import model.File;
import model.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

public class CloudServer {

    /* TODO:
    create tables if not exists,
    clean files table: remove files, which transferring was broken
    check free space before load file
    drag&drop files
    сортировка файлов
    выбор файла
    TODO v2: add size limit, current size, check free space before load file
    */

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
            User res = UserDAOImpl.getInstance().create(connection, user);
            FileDAOImpl.getInstance().create(connection, file);
            // UPDATE
            res.setEmail("android@mail.ru");
            UserDAOImpl.getInstance().update(connection, res);
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
            DBHelper.getInstance().clearTable(connection, UserDAOImpl.TABLE_NAME);
            DBHelper.getInstance().clearTable(connection, FileDAOImpl.TABLE_NAME);

            DBHelper.getInstance().closeDb(connection);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
