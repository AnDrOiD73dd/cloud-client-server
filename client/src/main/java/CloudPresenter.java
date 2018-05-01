import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import protocol.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CloudPresenter implements RequestHandler, ResponseHandler, ResponseListener, FilesRequestHandler {

    private CloudController controller;
    private ConnectionService connectionService;
    private RequestMessage lastRequest;
    private ObservableList<ClientFile> filesList;

    CloudPresenter(CloudController controller) {
        this.controller = controller;
    }

    void initialize() {
        connectionService = ConnectionService.getInstance();
        connectionService.setResponseListener(this);
        connectionService.addConnectionStateListener(new ConnectionStateListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {
                Platform.runLater(() -> controller.showSignIn());
                controller.showAlert("Соединение с сервером разорвано");
            }

            @Override
            public void onError(String error) {

            }
        });
        requestFilesList();
    }

    void onClickAdd(Event event) {
        controller.showFileChooser();
    }

    public void onFileSelected(File file) {
        try {
            RequestMessage newRequest;
            String path = file.getAbsolutePath();
            long size = FileService.getSize(path);
            long date = FileService.getDate(path);
            do {
                newRequest = (RequestMessage) RequestMessageFactory.getFileAddRequest(MessageUtil.getId(), path, date, size);
            } while (lastRequest != null && lastRequest.getId() == newRequest.getId());
            lastRequest = newRequest;
            connectionService.getOut().writeObject(lastRequest.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            controller.showAlert("Произошла ошибка при отправке данных на сервер");
        }
    }

    void onClickDelete(Event event) {
    }

    public void onClickDeleteAll(Event event) {
    }

    void onClickDownload(Event event) {
    }

    public void onClickUpdate(Event event) {
    }

    private void requestFilesList() {
        try {
            RequestFilesList newRequest;
            do {
                newRequest = (RequestFilesList) RequestMessageFactory.getEmptyFilesListRequest(MessageUtil.getId());
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
        MessageParser.parse(message, lastRequest, this, this, this);
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
            case CommandList.FILE_ADD:
                switch (responseMessage.getResponseCode()) {
                    case 0:
                        break;
                    case 1:
                        // SUCCESS
                        break;
                    case 2:
                        controller.showAlert("Ошибка аутентификации");
                        controller.showSignIn();
                        break;
                    case 3:
                        controller.showAlert("При добавлении файла произошла ошибка, попробуйте еще раз");
                        break;
                    case 4:
                        controller.showAlert("Файл не добавлен: такой файл уже существует");
                        break;
                    case 5:
                        controller.showAlert("Файл не добавлен: отсутствует свободное место в облаке");
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

    @Override
    public void handleFilesListRequest(RequestFilesList requestFilesList) {
        this.filesList = ClientFile.map(requestFilesList.getFilesList());
//        controller.setClientFiles(filesList);
        controller.updateTableData(filesList);
//        System.out.println(filesList);
//        controller.onFileListChanged(this.filesList);
    }
}
