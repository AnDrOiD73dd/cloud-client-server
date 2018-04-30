import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionService {

    private static ConnectionService instance;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread listenerThread;
    private boolean isConnected;
    private ResponseListener responseListener;
    private ArrayList<ConnectionStateListener> connectionStateListeners;

    private ConnectionService() {
        isConnected = false;
        connectionStateListeners = new ArrayList<>();
    }

    static synchronized ConnectionService getInstance() {
        if (instance == null)
            instance = new ConnectionService();
        return instance;
    }

    DataOutputStream getOut() {
        return out;
    }

    public boolean isConnected() {
        return isConnected;
    }

    void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    void connect(String host, int port) {
        if (isConnected)
            return;
        try {
            this.socket = new Socket(host, port);
            out = new DataOutputStream(this.socket.getOutputStream());
            in = new DataInputStream(this.socket.getInputStream());
            onConnectionStateChanged(true);
            listenerThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        String s = in.readUTF();
                        responseListener.onNewMessage(s);
//                    Object request;
//                    request = this.in.readObject();
//                    if (request instanceof File) {
//                        File requestFile = (File) request;
//                        responseListener.onNewFile(requestFile);
//                    }
//                    else if (request instanceof String) {
//                        String question = request.toString();
////                        String msg = in.readUTF();
//                        System.out.println("msg = " + question);
//                        responseListener.onNewMessage(question);
//                    }
                    } catch (IOException e) {
//                        System.out.println(e.getMessage());
                        onConnectionError("Сервер перестал отвечать");
//                } catch (ClassNotFoundException e) {
//                    System.out.println("Class of a serialized object cannot be found: " + e.getMessage());
                    } finally {
                        disconnect();
//                    try {
//                        socket.close();
//                    } catch (IOException e) {
//                        System.err.println("Не удалось закрыть сокет: " + e.getMessage());
//                    }
                    }
                }
            });
            listenerThread.setDaemon(true);
            listenerThread.start();
        } catch (IOException e) {
//            System.out.println(e.getMessage());
            onConnectionError("Не удалось подключиться к серверу. Проверьте сетевое соединение.");
//            isConnected = false;
        }
    }

    void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        listenerThread.interrupt();
        onConnectionStateChanged(false);
    }

    private void onConnectionStateChanged(boolean isConnected) {
        this.isConnected = isConnected;
        if (isConnected) {
            for (ConnectionStateListener listener: connectionStateListeners) {
                listener.onConnected();
            }
        } else {
            for (ConnectionStateListener listener: connectionStateListeners) {
                listener.onDisconnected();
            }
        }
    }

    private void onConnectionError(String message) {
        for (ConnectionStateListener listener: connectionStateListeners) {
            listener.onError(message);
        }
    }

    void addConnectionStateListener(ConnectionStateListener listener) {
        if (!connectionStateListeners.contains(listener))
            connectionStateListeners.add(listener);
    }

    void removeConnectionStateListener(ConnectionStateListener listener) {
        connectionStateListeners.remove(listener);
    }
}
