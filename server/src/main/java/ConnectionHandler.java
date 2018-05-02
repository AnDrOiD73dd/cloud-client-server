import base.Constants;
import db.DBHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            // TODO: delete broken files from DB table
            DBHelper.getInstance().closeDb(connection);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
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


