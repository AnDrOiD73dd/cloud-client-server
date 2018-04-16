import db.DBHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

public class ConnectionHandler {
    private static ConnectionHandler instance;
    private Vector<ClientHandler> clients;
    private Socket serverSocket;
    private boolean isRunning;

    private ConnectionHandler() {
        isRunning = false;
    }

    public static synchronized ConnectionHandler getInstance() {
        if (instance == null)
            instance = new ConnectionHandler();
        return instance;
    }

    public Vector<ClientHandler> getClients() {
        return clients;
    }

    public Socket getServerSocket() {
        return serverSocket;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void run() {
        if (isRunning)
            return;
        isRunning = true;
        Connection connection = DBHelper.getInstance().getConnection();
        try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) {
            clients = new Vector<>();
            if (connection == null)
                connection = DBHelper.getInstance().openDb();
            System.out.println("Server started... Waiting clients...");
            while (true) {
                this.serverSocket = serverSocket.accept();
                System.out.println(String.format("Client connected: %s:%s:%s",
                        this.serverSocket.getInetAddress(), this.serverSocket.getPort(), this.serverSocket.getLocalPort()));
                new ClientHandler(this, this.serverSocket.getInetAddress(), this.serverSocket.getPort(), this.serverSocket.getLocalPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Could not connect to DB: " + e.getMessage());
        } finally {
            try {
                DBHelper.getInstance().closeDb(connection);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}


