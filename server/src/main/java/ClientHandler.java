import db.FileDAOImpl;
import db.UserDAOImpl;
import model.User;
import protocol.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientHandler implements RequestHandler, ResponseHandler, FilesRequestHandler {

    private static final String CLOUD_DIR_NAME = "storage";

    private static final long AUTH_TIMEOUT = 120 * 1000L;

    private ConnectionHandler connectionHandler;
    private Socket socket;
    private Connection dbConnection;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Thread messageListener;
    private Thread authTimeoutThread;
    private boolean authorized;
    private RequestMessage lastRequest;
    private User currentUser;

    ClientHandler(ConnectionHandler connectionHandler, Socket socket, Connection dbConnection) {
        System.out.println(String.format("Client connected: %s:%s:%s",
                socket.getInetAddress(), socket.getPort(), socket.getLocalPort()));
        this.connectionHandler = connectionHandler;
        this.socket = socket;
        this.dbConnection = dbConnection;
    }

    void startListen() {
        authorized = false;

        authTimeoutThread = new Thread(() -> {
            try {
                Thread.sleep(AUTH_TIMEOUT);
                if (!authorized) {
                    disconnect();
                }
            } catch (InterruptedException e) {
                System.out.println("Ожидание аутентификации прервано");
            }
        });
        authTimeoutThread.setDaemon(true);
        authTimeoutThread.start();

        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        messageListener = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Object request = in.readObject();
                    if (request instanceof File) {
                        File requestFile = (File) request;
                        // TODO load file
                    } else if (request instanceof String) {
                        String question = request.toString();
                        parseCommand(question);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Exception in data listener: " + e + " " + e.getMessage());
            } finally {
                disconnect();
            }
        });
//        messageListener.setDaemon(true);
        messageListener.start();
    }

    private void parseCommand(String message) {
        MessageParser.parse(message, lastRequest, this, this, this);
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
        onAuthorized(false);
        in = null;
        out = null;
        socket = null;
        currentUser = null;
    }

    @Override
    public void handleRequest(RequestMessage requestMessage) {
        System.out.println("handleRequest: RequestMessage=" + requestMessage.toString());
        String cmd = requestMessage.getCmd();
        switch (cmd) {
            case CommandList.SIGN_IN:
                parseSignIn(requestMessage);
                break;
            case CommandList.SIGN_UP:
                parseSignUp(requestMessage);
                break;
            default:
                System.out.println("Unknown command = " + cmd);
                break;
        }
    }

    private void parseSignIn(RequestMessage requestMessage) {
        HashMap<String, String> body = requestMessage.getRequest();
        if (body.containsKey(RequestMessageFactory.KEY_LOGIN) && body.containsKey(RequestMessageFactory.KEY_PASSWORD)) {
            String username = body.get(RequestMessageFactory.KEY_LOGIN);
            String password = body.get(RequestMessageFactory.KEY_PASSWORD);
            if (username.isEmpty() || password.isEmpty()) {
                sendMessage(new ResponseMessage(requestMessage.getId(), 3).toString());
                return;
            }
            User user = UserDAOImpl.getInstance().get(dbConnection, username);
            if (user != null && user.getPassword().equals(password)) {
                onAuthorized(true);
                currentUser = user;
                sendMessage(new ResponseMessage(requestMessage.getId(), 1).toString());
            }
            else {
                sendMessage(new ResponseMessage(requestMessage.getId(), 3).toString());
            }
        } else {
            sendMessage(new ResponseMessage(requestMessage.getId(), 2).toString());
        }
    }

    private void parseSignUp(RequestMessage requestMessage) {
        HashMap<String, String> body = requestMessage.getRequest();
        String username = body.getOrDefault(RequestMessageFactory.KEY_LOGIN, "");
        String password = body.getOrDefault(RequestMessageFactory.KEY_PASSWORD, "");
        String firstName = body.getOrDefault(RequestMessageFactory.KEY_FIRST_NAME, "");
        String lastName = body.getOrDefault(RequestMessageFactory.KEY_LAST_NAME, "");
        String email = body.getOrDefault(RequestMessageFactory.KEY_EMAIL, "");
        // Some checks
        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 2).toString());
            return;
        }
        User user = UserDAOImpl.getInstance().get(dbConnection, username);
        if (user != null) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 4).toString());
            return;
        }
        user = UserDAOImpl.getInstance().getByEmail(dbConnection, email);
        if (user != null) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 5).toString());
            return;
        }
        // Create user directory
        Path userPath = Paths.get(Utils.getWorkingDirectory() + "/" + CLOUD_DIR_NAME + "/" + username).normalize();
        try {
            userPath = Files.createDirectories(userPath);
        } catch (IOException e) {
            System.out.println("Не могу создать директорию: " + userPath.toString());
            sendMessage(new ResponseMessage(requestMessage.getId(), 3).toString());
        }
        // add user to DB
        user = new User.Builder()
                .setUsername(username)
                .setPassword(password)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setRootDir(userPath.toString())
                .create();
        try {
            User newUser = UserDAOImpl.getInstance().create(dbConnection, user);
            if (newUser.getId() > 0) {
                onAuthorized(true);
                currentUser = newUser;
                sendMessage(new ResponseMessage(requestMessage.getId(), 1).toString());
            }
            else sendMessage(new ResponseMessage(requestMessage.getId(), 3).toString());
        } catch (SQLException e) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 3).toString());
        }
    }

    private void sendFilesList() {
        List<model.File> filesList = FileDAOImpl.getInstance().getAll(dbConnection, currentUser.getId());
        sendMessage(new RequestFilesList(MessageUtil.getId(), new ArrayList<>(filesList)).toString());
    }

    @Override
    public void handleResponse(ResponseMessage responseMessage, String command) {

    }

    private void sendMessage(String message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void onAuthorized(boolean isAuthorized) {
        authorized = isAuthorized;
        if (isAuthorized) {
            connectionHandler.subscribe(this);
            authTimeoutThread.interrupt();
        }
        else connectionHandler.unSubscribe(this);
    }

    @Override
    public void handleFilesListRequest(RequestFilesList requestFilesList) {
        sendMessage(new ResponseMessage(requestFilesList.getId(), 0).toString());
        sendFilesList();
    }
}
