import javafx.application.Platform;
import javafx.event.Event;
import protocol.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CloudPresenter implements RequestHandler, ResponseHandler, ResponseListener {

    private CloudController controller;
    private ConnectionService connectionService;
    private RequestMessage lastRequest;

    public CloudPresenter(CloudController controller) {
        this.controller = controller;
    }

    public void initialize() {
        connectionService = ConnectionService.getInstance();
        connectionService.setResponseListener(this);
        connectionService.addConnectionStateListener(new ConnectionStateListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {
                controller.showAlert("Соединение с сервером разорвано");
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void onClickAdd(Event event) {
    }

    public void onClickDelete(Event event) {
    }

    public void onClickDownload(Event event) {
    }

    private void requestFilesList() {
        try {
            RequestMessage newRequest;
            do {
                newRequest = (RequestMessage) RequestMessageFactory.getFilesListRequest(MessageUtil.getId());
            } while (lastRequest != null && lastRequest.getId() == newRequest.getId());
            lastRequest = newRequest;
            connectionService.getOut().writeObject(lastRequest.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            controller.showAlert("Произошла ошибка при отправке данных на сервер");
        }
    }

    @Override
    public void onNewFile(File requestFile) {

    }

    @Override
    public void onNewMessage(String message) {
        System.out.println("parseCommand: " + message);
        MessageParser.parse(message, lastRequest, this, this);
    }

    @Override
    public void handleRequest(RequestMessage requestMessage) {
        switch (requestMessage.getCmd()) {
            case CommandList.FILES_LIST:
                HashMap<String, String> request = requestMessage.getRequest();
                break;
            default:
                System.out.println("Unknown command=" + requestMessage.getCmd());
                break;
        }
    }

    @Override
    public void handleResponse(ResponseMessage responseMessage, String command) {
        if (lastRequest.getId() != responseMessage.getId()) {
            System.out.println("Unknown response id: " + responseMessage);
            return;
        }
        switch (lastRequest.getCmd()) {
            case CommandList.FILES_LIST:
                switch (responseMessage.getResponseCode()) {
                    case 0:
                        break;
                    case 1:
                        controller.showAlert("Произошла ошибка при запросе файлов, обратитесь к системному администратору");
                        break;
                    default:
                        System.out.println("Unknown responseCode=" + responseMessage.getResponseCode()
                                + ", cmd=" + CommandList.SIGN_IN);
                        break;
                }
                break;
            default:
                System.out.println(("Unknown command=" + command));
                break;
        }
    }
}
