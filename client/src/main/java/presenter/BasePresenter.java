package presenter;

public abstract class BasePresenter {

    protected boolean isValidServerAddress(String serverAddress, String serverPort) {
        boolean res = true;
        int port = -1;
        try {
            port = Integer.valueOf(serverPort);
        } catch (NumberFormatException ex) {
            res = false;
        }
        if (port < 0 || port > 65535)
            res = false;
        if (serverAddress.isEmpty())
            res = false;
        return res;
    }

    protected abstract void initConnection(String host, Integer port);
}
