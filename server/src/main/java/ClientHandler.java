import protocol.RequestMessage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;

public class ClientHandler {

    private static final long AUTH_TIMEOUT = 120 * 1000L;

    private ConnectionHandler connectionHandler;
    private Socket socket;
    private Connection dbConnection;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Thread messageListener;
    private Thread authTimeoutThread;
    private boolean authorized;

    public ClientHandler(ConnectionHandler connectionHandler, Socket socket, Connection dbConnection) {
        System.out.println(String.format("Client connected: %s:%s:%s",
                socket.getInetAddress(), socket.getPort(), socket.getLocalPort()));
        this.connectionHandler = connectionHandler;
        this.socket = socket;
        this.dbConnection = dbConnection;
    }

    public void startListen() {
        authorized = false;

        authTimeoutThread = new Thread(() -> {
            try {
                Thread.sleep(AUTH_TIMEOUT);
            } catch (InterruptedException e) {
                System.out.println("Произошла ошибка во время ожидания аутентификации: " + e.getMessage());
            }
            if (!authorized)
                disconnect();
        });
        authTimeoutThread.setDaemon(true);
        authTimeoutThread.start();

        try {
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        messageListener = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Object request = new Object();
                    request = this.in.readObject();
                    if (request instanceof File) {
                        File requestFile = (File) request;
                    }
                    else if (request instanceof String) {
                        String question = request.toString();
//                        String msg = in.readUTF();
                        System.out.println("msg = " + question);
                        parseCommand(question);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            } finally {
                disconnect();
            }
        });
//        messageListener.setDaemon(true);
        messageListener.start();
    }

    private void parseCommand(String message) {
        RequestMessage request = RequestMessage.parse(message);
        MessageProcessor.getInstance().handleRequest(request);
    }

    private void disconnect() {
//        connectionHandler.unsubscribe(this);
        messageListener.interrupt();
        authTimeoutThread.interrupt();
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        onDisconnected();
    }

    private void onDisconnected() {
        in = null;
        out = null;
        socket = null;
        authorized = false;
    }
}
