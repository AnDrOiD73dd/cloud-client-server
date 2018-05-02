package connection.listener;

public interface ConnectionStateListener {
    void onConnected();
    void onDisconnected();
    void onError(String error);
}
