import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SignInPresenter {

    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static ObservableList<Object> clientList;
    private static Thread readFromServer;

    public static void onSignInClick() {

    }

    private static void connect() {
        try {
            socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            clientList = FXCollections.observableArrayList();
            readFromServer = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        String s = in.readUTF();
                        parseMsg(s);
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                    showAlert("Сервер перестал отвечать");
                    setAuthorized(false);
                    disconnect();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Не удалось закрыть сокет: ");
                        e.printStackTrace();
                    }
                }
            });
            readFromServer.setDaemon(true);
            readFromServer.start();
        } catch (IOException e) {
//            e.printStackTrace();
            showAlert("Не удалось подключиться к серверу. Проверьте сетевое соединение.");
        }
    }

    private static void setAuthorized(boolean b) {
    }

    private static void showAlert(String s) {
    }

    private static void parseMsg(String s) {
    }

    private static void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        readFromServer.interrupt();
    }
}
