import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ClientHandler {

    private static final long AUTH_TIMEOUT = 120 * 1000L;

    private Socket socket;
    private ConnectionHandler connectionHandler;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread messageListener;
    private Thread waitAuth;
    private String nick;
    private volatile boolean authorized;

    public ClientHandler(ConnectionHandler server, InetAddress clientIp, int clientPort, int clientLocalPort) {
        init(server);
    }

    private void init(ConnectionHandler server) {
        authorized = false;
        try {
            this.connectionHandler = server;
            this.socket = server.getServerSocket();
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            waitAuth = new Thread(() -> {
                try {
                    Thread.sleep(AUTH_TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!authorized)
                    disconnect();
            });
            waitAuth.setDaemon(true);
            waitAuth.start();
            messageListener = new Thread(() -> {
                try {
                    while (!authorized && !Thread.currentThread().isInterrupted()) {
                        String msg = in.readUTF();
//                        System.out.println("msg = " + msg);
//                        parseServerCommand(msg);
                    }
                    while (!Thread.currentThread().isInterrupted()) {
                        String msg = in.readUTF();
//                        parseMsg(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
//                    nick = null;
//                    server.unsubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            messageListener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
//        connectionHandler.unsubscribe(this);
        messageListener.interrupt();
        waitAuth.interrupt();
        try {
            in.close();
            in = null;
            out.close();
            out = null;
            socket.close();
            socket = null;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        authorized = false;
//        nick = null;
    }
}
