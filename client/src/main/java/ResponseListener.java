import model.TransferringFile;

public interface ResponseListener {
    void onNewFile(TransferringFile file);
    void onNewMessage(String message);
}
