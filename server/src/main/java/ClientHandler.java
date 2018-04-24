import db.UserDAOImpl;
import model.User;
import protocol.*;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;

public class ClientHandler implements RequestHandler, ResponseHandler {

    private static final long AUTH_TIMEOUT = 120 * 1000L;

    private ConnectionHandler connectionHandler;
    private Socket socket;
    private Connection dbConnection;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread messageListener;
    private Thread authTimeoutThread;
    private boolean authorized;
    private RequestMessage lastRequest;

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
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        messageListener = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String s = in.readUTF();
                    parseCommand(s);
//                    Object request;
//                    request = this.in.readObject();
//                    if (request instanceof File) {
//                        File requestFile = (File) request;
//                    } else if (request instanceof String) {
//                        String question = request.toString();
////                        String msg = in.readUTF();
//                        System.out.println("msg = " + question);
//                        parseCommand(question);
//                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                disconnect();
            }
        });
//        messageListener.setDaemon(true);
        messageListener.start();
    }

    private void parseCommand(String message) {
        MessageParser.parse(message, lastRequest, this, this);
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

    @Override
    public void handleRequest(RequestMessage requestMessage) {
        System.out.println("handleRequest: RequestMessage=" + requestMessage.toString());
        int id = requestMessage.getId();
        String cmd = requestMessage.getCmd();
        switch (cmd) {
            case CommandList.SIGN_IN:
                HashMap<String, String> body = requestMessage.getRequest();
                if (body.containsKey(RequestMessageFactory.KEY_LOGIN) && body.containsKey(RequestMessageFactory.KEY_PASSWORD)) {
                    String username = body.get(RequestMessageFactory.KEY_LOGIN);
                    String password = body.get(RequestMessageFactory.KEY_PASSWORD);
                    if (username.isEmpty() || password.isEmpty()) {
                        // TODO: send invalid data
                        return;
                    }
                    User user = UserDAOImpl.getInstance().get(dbConnection, username);
                    if (user != null && user.getPassword().equals(password)) {
                        sendMessage(new ResponseMessage(requestMessage.getId(), 1).toString());
                        authorized = true;
                    }
                    else {
                        sendMessage(new ResponseMessage(requestMessage.getId(), 3).toString());
                    }

                } else {
                    // TODO: send invalid data
                    return;
                }
                break;
        }
    }

    @Override
    public void handleResponse(ResponseMessage responseMessage, String command) {

    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
