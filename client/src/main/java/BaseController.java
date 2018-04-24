import java.io.*;
import java.net.Socket;

public abstract class BaseController {

    protected Socket socket;
    protected DataInputStream in;
    protected DataOutputStream out;
    protected Thread listenerThread;

    public BaseController() {
    }

    protected void connect(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            out = new DataOutputStream(this.socket.getOutputStream());
            in = new DataInputStream(this.socket.getInputStream());
            listenerThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        String s = in.readUTF();
                        parseCommand(s);
//                    Object request;
//                    request = this.in.readObject();
//                    if (request instanceof File) {
//                        File requestFile = (File) request;
//                        obtainFile(requestFile);
//                    }
//                    else if (request instanceof String) {
//                        String question = request.toString();
////                        String msg = in.readUTF();
//                        System.out.println("msg = " + question);
//                        parseCommand(question);
//                    }
                    } catch (IOException e) {
//                    e.printStackTrace();
                        Utils.showAlert("Сервер перестал отвечать");
                        disconnect();
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
//            e.printStackTrace();
            Utils.showAlert("Не удалось подключиться к серверу. Проверьте сетевое соединение.");
        }
    }

    protected abstract void obtainFile(File requestFile);

    protected abstract void parseCommand(String message);

    protected void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listenerThread.interrupt();
    }
}
