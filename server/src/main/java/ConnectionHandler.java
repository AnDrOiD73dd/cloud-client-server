import db.DBHelper;
import model.File;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

public class ConnectionHandler {
    private static ConnectionHandler instance;
    private Vector<ClientHandler> clients;

    private ConnectionHandler() {
    }

    public static synchronized ConnectionHandler getInstance() {
        if (instance == null)
            instance = new ConnectionHandler();
        return instance;
    }

    public Vector<ClientHandler> getClients() {
        return clients;
    }

    public void prepareDb() {
        Connection connection;
        try {
            connection = DBHelper.getInstance().openDb();
            DBHelper.getInstance().createTables(connection);
            // TODO: delete broken files from DB table
            DBHelper.getInstance().closeDb(connection);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void listenConnections() {
        Connection connection = null;
        try {
            connection = DBHelper.getInstance().openDb();
            try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) {
                clients = new Vector<>();
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
}


