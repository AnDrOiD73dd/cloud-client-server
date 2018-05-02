import base.FileHelper;
import base.Utils;
import db.FileDAOImpl;
import db.UserDAOImpl;
import adapter.File;
import adapter.TransferringFile;
import adapter.User;
import protocol.*;
import protocol.handler.FilesRequestHandler;
import protocol.handler.RequestHandler;
import protocol.handler.ResponseHandler;
import protocol.request.RequestFilesList;
import protocol.request.RequestMessage;

import java.io.*;
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
                    if (request instanceof TransferringFile) {
                        TransferringFile requestFile = (TransferringFile) request;
                        obtainNewFile(requestFile);
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

    private void obtainNewFile(TransferringFile requestFile) {
        System.out.println("NEW FILE: " + requestFile.getFilePath());
        adapter.File checkedFile = FileDAOImpl.getInstance().get(dbConnection, currentUser.getId(), requestFile.getFilePath());
        if (checkedFile != null) {
            Path userDir = FileHelper.getUserDirectory(CLOUD_DIR_NAME, currentUser.getUsername());
            String serverFileName = checkedFile.getServerFileName();
            FileOutputStream stream = null;
            try {
                Files.createDirectories(userDir);
                stream = new FileOutputStream(Paths.get(userDir.toAbsolutePath().toString(), serverFileName).toAbsolutePath().toString());
                stream.write(requestFile.getFile());
                checkedFile.setSynced(true);
                FileDAOImpl.getInstance().update(dbConnection, checkedFile);
                sendFilesList();
            } catch (FileNotFoundException e) {
                System.out.println("Файл не найден: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Не удалось записать файл: " + e.getMessage());
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    System.out.println("Не удалось закрыть файл: " + e.getMessage());
                }
            }
        }
    }

    private void parseCommand(String message) {
        MessageParser.parse(message, lastRequest, this, this, this);
    }

    private void disconnect() {
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
                sendMessage(new ResponseMessage(requestMessage.getId(), 0).toString());
                parseSignIn(requestMessage);
                break;
            case CommandList.SIGN_UP:
                sendMessage(new ResponseMessage(requestMessage.getId(), 0).toString());
                parseSignUp(requestMessage);
                break;
            case CommandList.FILE_ADD:
                sendMessage(new ResponseMessage(requestMessage.getId(), 0).toString());
                parseFileAdd(requestMessage);
                break;
            case CommandList.FILE_DELETE:
                sendMessage(new ResponseMessage(requestMessage.getId(), 0).toString());
                parseFileDelete(requestMessage);
                break;
            case CommandList.FILE_DOWNLOAD:
                sendMessage(new ResponseMessage(requestMessage.getId(), 0).toString());
                parseFileDownload(requestMessage);
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
        // add user to DB
        user = new User.Builder()
                .setUsername(username)
                .setPassword(password)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
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
        List<adapter.File> filesList = FileDAOImpl.getInstance().getAll(dbConnection, currentUser.getId());
        sendMessage(new RequestFilesList(MessageUtil.getId(), new ArrayList<>(filesList)).toString());
    }

    private void parseFileAdd(RequestMessage requestMessage) {
        if (!authorized) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 2).toString());
            return;
        }
        HashMap<String, String> body = requestMessage.getRequest();
        String filePath = body.getOrDefault(RequestFilesList.KEY_FILE_PATH, "");
        long fileDate = Long.valueOf(body.getOrDefault(RequestFilesList.KEY_FILE_DATE, ""));
        long fileSize = Long.valueOf(body.getOrDefault(RequestFilesList.KEY_FILE_SIZE, ""));
        if (filePath.isEmpty() || fileDate == 0) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 6).toString());
            return;
        }
        String serverFileName = FileHelper.generateServerFileName(filePath);
        adapter.File file = new adapter.File.Builder()
                .setUserId(currentUser.getId())
                .setServerFileName(serverFileName)
                .setFilePath(filePath)
                .setFileDate(fileDate)
                .setFileSize(fileSize)
                .setSynced(false)
                .create();
        try {
            adapter.File checkedFile = FileDAOImpl.getInstance().get(dbConnection, currentUser.getId(), filePath);
            if (checkedFile != null) {
                sendMessage(new ResponseMessage(requestMessage.getId(), 4).toString());
                return;
            }
            if (fileSize > Utils.getFreeSpace()) {
                sendMessage(new ResponseMessage(requestMessage.getId(), 5).toString());
                return;
            }
            FileDAOImpl.getInstance().create(dbConnection, file);
            sendMessage(new ResponseMessage(requestMessage.getId(), 1).toString());
        } catch (SQLException e) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 3).toString());
        }
    }

    private void parseFileDelete(RequestMessage requestMessage) {
        if (!authorized) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 2).toString());
            return;
        }
        HashMap<String, String> body = requestMessage.getRequest();
        String filePath = body.getOrDefault(RequestFilesList.KEY_FILE_PATH, "");
        if (filePath.isEmpty()) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 3).toString());
            return;
        }
        File file = FileDAOImpl.getInstance().get(dbConnection, currentUser.getId(), filePath);
        if (file == null) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 4).toString());
            sendFilesList();
            return;
        }
        String serverFilePath = Paths.get(FileHelper.getUserDirectory(CLOUD_DIR_NAME, currentUser.getUsername())
                        .toAbsolutePath().toString(), file.getServerFileName()).toAbsolutePath().toString();
        if (FileHelper.deleteLocalFile(serverFilePath)) {
            if (FileDAOImpl.getInstance().delete(dbConnection, file.getId())) {
                sendMessage(new ResponseMessage(requestMessage.getId(), 1).toString());
                sendFilesList();
            } else {
                sendMessage(new ResponseMessage(requestMessage.getId(), 4).toString());
                sendFilesList();
            }
        } else {
            sendMessage(new ResponseMessage(requestMessage.getId(), 4).toString());
        }
    }

    private void parseFileDownload(RequestMessage requestMessage) {
        if (!authorized) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 2).toString());
            return;
        }
        HashMap<String, String> body = requestMessage.getRequest();
        String filePath = body.getOrDefault(RequestFilesList.KEY_FILE_PATH, "");
        String destinationFilePath = body.getOrDefault(RequestFilesList.KEY_DESTINATION_FILE_PATH, "");
        if (filePath.isEmpty() || destinationFilePath.isEmpty()) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 3).toString());
            return;
        }
        File file = FileDAOImpl.getInstance().get(dbConnection, currentUser.getId(), filePath);
        if (file == null) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 1).toString());
            sendFilesList();
            return;
        }
        String serverFilePath = FileHelper.getUserDirectory(CLOUD_DIR_NAME, currentUser.getUsername()).toAbsolutePath().toString();
        serverFilePath = Paths.get(serverFilePath, file.getServerFileName()).toAbsolutePath().toString();
        if (!FileHelper.isExists(serverFilePath)) {
            sendMessage(new ResponseMessage(requestMessage.getId(), 1).toString());
            sendFilesList();
            return;
        }
        TransferringFile transferringFile = new TransferringFile(destinationFilePath, FileHelper.convertToByteArray(serverFilePath));
        sendFile(transferringFile);
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

    private void sendFile(TransferringFile file) {
        try {
            out.writeObject(file);
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
