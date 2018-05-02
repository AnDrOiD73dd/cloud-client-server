package base;

import adapter.TransferringFile;
import listener.ConnectionStateListener;
import listener.ResponseListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionService {

    private static ConnectionService instance;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Thread listenerThread;
    private boolean isConnected;
    private ResponseListener responseListener;
    private ArrayList<ConnectionStateListener> connectionStateListeners;

    private ConnectionService() {
        isConnected = false;
        connectionStateListeners = new ArrayList<>();
    }

    public static synchronized ConnectionService getInstance() {
        if (instance == null)
            instance = new ConnectionService();
        return instance;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    public void connect(String host, int port) {
        if (isConnected)
            return;
        try {
            this.socket = new Socket(host, port);
            out = new ObjectOutputStream(this.socket.getOutputStream());
            in = new ObjectInputStream(this.socket.getInputStream());
            onConnectionStateChanged(true);
            listenerThread = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Object request = in.readObject();
                        if (request instanceof TransferringFile) {
                            TransferringFile requestFile = (TransferringFile) request;
                            responseListener.onNewFile(requestFile);
                        } else if (request instanceof String) {
                            String question = request.toString();
//                        String msg = in.readUTF();
                            responseListener.onNewMessage(question);
                        }
                    }
                } catch (IOException e) {
//                        System.out.println(e.getMessage());
                    onConnectionError("Сервер перестал отвечать");
                } catch (ClassNotFoundException e) {
                    onConnectionError("Произошла ошибка во время получения данных от сервера");
                } finally {
                    disconnect();
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

    public void addConnectionStateListener(ConnectionStateListener listener) {
        if (!connectionStateListeners.contains(listener))
            connectionStateListeners.add(listener);
    }

    public void removeConnectionStateListener(ConnectionStateListener listener) {
        connectionStateListeners.remove(listener);
    }
}
