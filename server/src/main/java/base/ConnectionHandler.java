package base;

import adapter.File;
import adapter.User;
import db.DBHelper;
import db.FileDAOImpl;
import db.UserDAOImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static base.ClientHandler.CLOUD_DIR_NAME;

public class ConnectionHandler {
    private static ConnectionHandler instance;
    private List<ClientHandler> clients;

    private ConnectionHandler() {
    }

    static synchronized ConnectionHandler getInstance() {
        if (instance == null)
            instance = new ConnectionHandler();
        return instance;
    }

    public List<ClientHandler> getClients() {
        return clients;
    }

    void prepareDb() {
        Connection connection;
        try {
            connection = DBHelper.getInstance().openDb();
            DBHelper.getInstance().createTables(connection);
            deleteBrokenFiles(connection);
            DBHelper.getInstance().closeDb(connection);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void deleteBrokenFiles(Connection connection) {
        ArrayList<File> files = FileDAOImpl.getInstance().getBrokenFiles(connection);
        for (File file : files) {
            User user = UserDAOImpl.getInstance().get(connection, file.getUserId());
            Path userDir = FileHelper.getUserDirectory(CLOUD_DIR_NAME, user.getUsername());
            String filePath = Paths.get(userDir.toAbsolutePath().toString(), file.getServerFileName()).toAbsolutePath().toString();
            FileHelper.deleteLocalFile(filePath);
            FileDAOImpl.getInstance().delete(connection, file.getId());
        }
    }

    void listenConnections() {
        Connection connection = null;
        try {
            connection = DBHelper.getInstance().openDb();
            try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) {
                clients = Collections.synchronizedList(new ArrayList<>());
                System.out.println("Server started... Waiting clients...");
                while (true) {
                    Socket socket = serverSocket.accept();
                    new ClientHandler(this, socket, connection).startListen();
                }
            }
            catch (IOException e) {
                System.out.println("Не удалось запустить сервер: " + e.getMessage());
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Произошла ошибка при открытии БД: " + e);
        } finally {
            if (connection != null) {
                try {
                    DBHelper.getInstance().closeDb(connection);
                } catch (SQLException e) {
                    System.out.println("Произошла ошибка при закрытии БД: " + e);
                }
            }
        }
    }

    void subscribe(ClientHandler clientHandler) {
        if (!clients.contains(clientHandler))
            clients.add(clientHandler);
    }

    void unSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}


