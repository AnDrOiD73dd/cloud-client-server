import java.io.File;

public interface ResponseListener {
    void onNewFile(File requestFile);
    void onNewMessage(String message);
}
