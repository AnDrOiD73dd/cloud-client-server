package ui.presenter;

import adapter.TransferringFile;
import base.FileHelper;
import connection.ConnectionService;
import connection.listener.ConnectionStateListener;
import connection.listener.ResponseListener;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import model.ClientFile;
import protocol.*;
import protocol.handler.FilesRequestHandler;
import protocol.handler.RequestHandler;
import protocol.handler.ResponseHandler;
import protocol.request.RequestFilesList;
import protocol.request.RequestMessage;
import ui.controller.BaseController;
import ui.controller.CloudController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class CloudPresenter implements RequestHandler, ResponseHandler, ResponseListener, FilesRequestHandler {

    private CloudController controller;
    private ConnectionService connectionService;
    private ObservableList<ClientFile> filesList;
    private RequestMessage lastRequest;
    private HashMap<Integer, RequestMessage> requestMap;

    public CloudPresenter(CloudController controller) {
        this.controller = controller;
        requestMap = new HashMap<>();
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
                Platform.runLater(() -> controller.showSignIn());
//                controller.showAlert("Соединение с сервером разорвано");
                controller.showNotification(BaseController.NotificationType.ERROR, "Ошибка", "Соединение с сервером потеряно");
            }

            @Override
            public void onError(String error) {

            }
        });
        requestFilesList();
    }

    public void onClickAdd(Event event) {
        File file = controller.showFileOpenDialog();
        if (file != null) {
            onFileSelected(file);
        }
    }

    public void onFileSelected(File file) {
        try {
            RequestMessage newRequest;
            String path = file.getAbsolutePath();
            long size = FileHelper.getSize(path);
            long date = FileHelper.getDate(path);
            do {
                newRequest = (RequestMessage) RequestMessageFactory.getFileAddRequest(MessageUtil.getId(), path, date, size);
            } while (lastRequest != null && lastRequest.getId() == newRequest.getId());
            requestMap.put(newRequest.getId(), newRequest);
            lastRequest = newRequest;
            connectionService.getOut().writeObject(lastRequest.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
//            controller.showAlert("Произошла ошибка при отправке данных на сервер");
            controller.showNotification(BaseController.NotificationType.ERROR, "Ошибка",
                    "Произошла ошибка при обращении к файлу " + file.getAbsolutePath());
        }
    }

    public void onClickDelete(Event event, ClientFile selectedItem) {
        if (selectedItem == null) {
//            controller.showAlert("Укажите файл из списка");
            controller.showNotification(BaseController.NotificationType.INFO, "Файл не выбран", "Укажите файл из списка");
            return;
        }
        requestDeleteFile(selectedItem.getFilePath());
    }

    private void requestDeleteFile(String filePath) {
        try {
            RequestMessage newRequest;
            do {
                newRequest = (RequestMessage) RequestMessageFactory.getFileDeleteRequest(MessageUtil.getId(), filePath);
            } while (lastRequest != null && lastRequest.getId() == newRequest.getId());
            requestMap.put(newRequest.getId(), newRequest);
            lastRequest = newRequest;
            connectionService.getOut().writeObject(lastRequest.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
//            controller.showAlert("Произошла ошибка при удалении файла, возможно файл не найден.");
            controller.showNotification(BaseController.NotificationType.ERROR, "Ошибка",
                    "Произошла ошибка при удалении файла, возможно файл не найден: " + filePath);
        }
    }

    public void onClickDeleteAll(Event event, ClientFile selectedItem) {
        if (selectedItem == null) {
//            controller.showAlert("Укажите файл из списка");
            controller.showNotification(BaseController.NotificationType.INFO, "Файл не выбран", "Укажите файл из списка");
            return;
        }
        if (FileHelper.isExists(selectedItem.getFilePath())) {
            if (!FileHelper.deleteLocalFile(selectedItem.getFilePath())) {
//                controller.showAlert("Не удалось удалить локальный файл: " + selectedItem.getFilePath() + "\nФайл не найден, либо недостаточно прав");
                controller.showNotification(BaseController.NotificationType.ERROR, "Файл не удален",
                        "Не удалось удалить локальный файл: " + selectedItem.getFilePath()
                                + "\nФайл не найден, либо недостаточно прав");
            }
        }
        requestDeleteFile(selectedItem.getFilePath());
    }

    public void onClickUpdate(Event event, ClientFile selectedItem) {
        if (selectedItem == null) {
//            controller.showAlert("Укажите файл из списка");
            controller.showNotification(BaseController.NotificationType.INFO, "Файл не выбран", "Укажите файл из списка");
            return;
        }
        String filePath = selectedItem.getFilePath();
        if (FileHelper.isExists(filePath)) {
            requestDeleteFile(selectedItem.getFilePath());
            onFileSelected(new File(filePath));
        }
        else {
//            controller.showAlert("Файл не найден: " + filePath);
            controller.showNotification(BaseController.NotificationType.WARNING, "Файл отсутствует", "Файл не найден: " + filePath);
        }
    }

    public void onClickDownload(Event event, ClientFile selectedItem) {
        if (selectedItem == null) {
//            controller.showAlert("Укажите файл из списка");
            controller.showNotification(BaseController.NotificationType.INFO, "Файл не выбран", "Укажите файл из списка");
            return;
        }
        String filePath = selectedItem.getFilePath();
        if (FileHelper.isExists(filePath)) {
            controller.showReplaceDialog(filePath);
        } else {
            requestDownloadFile(filePath, filePath);
        }
    }

    private void requestDownloadFile(String filePath, String destinationFilePath) {
        try {
            RequestMessage newRequest;
            do {
                newRequest = (RequestMessage) RequestMessageFactory.getFileDownloadRequest(MessageUtil.getId(), filePath, destinationFilePath);
            } while (lastRequest != null && lastRequest.getId() == newRequest.getId());
            requestMap.put(newRequest.getId(), newRequest);
            lastRequest = newRequest;
            connectionService.getOut().writeObject(lastRequest.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
//            controller.showAlert("Произошла ошибка при запросе на загрузку файла");
            controller.showNotification(BaseController.NotificationType.ERROR, "Ошибка",
                    "Произошла ошибка при запросе на загрузку файла " + filePath);
        }
    }

    public void onDownloadDialogResult(String filePath, String destinationFilePath) {
        requestDownloadFile(filePath, destinationFilePath);
    }

    private void requestFilesList() {
        try {
            RequestFilesList newRequest;
            do {
                newRequest = (RequestFilesList) RequestMessageFactory.getEmptyFilesListRequest(MessageUtil.getId());
            } while (lastRequest != null && lastRequest.getId() == newRequest.getId());
            requestMap.put(newRequest.getId(), newRequest);
            lastRequest = newRequest;
            connectionService.getOut().writeObject(lastRequest.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
//            controller.showAlert("Произошла ошибка при отправке данных на сервер");
            controller.showNotification(BaseController.NotificationType.ERROR, "Ошибка", "Не удалось получить список файлов");
        }
    }

    @Override
    public void onNewFile(TransferringFile file) {
        FileOutputStream stream = null;
        try {
            Path dirPath = Paths.get(file.getFilePath());
            Files.createDirectories(dirPath.getParent());
            stream = new FileOutputStream(Paths.get(file.getFilePath()).toAbsolutePath().toString());
            stream.write(file.getFile());
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении файла: " + e.getMessage());
//            controller.showAlert("Не удалось сохранить файл " + file.getFilePath());
            controller.showNotification(BaseController.NotificationType.ERROR, "Файл не сохранен", "Не удалось сохранить файл " + file.getFilePath());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    System.out.println("Не могу закрыть поток записи в файл: " + file.getFilePath());
                }
            }
        }
    }

    @Override
    public void onNewMessage(String message) {
        System.out.println("parseCommand: " + message);
        MessageParser.parse(message, lastRequest, this, this, this);
    }

    @Override
    public void handleRequest(RequestMessage requestMessage) {
        switch (requestMessage.getCmd()) {
            default:
                System.out.println("Unknown command=" + requestMessage.getCmd());
                break;
        }
    }

    @Override
    public void handleResponse(ResponseMessage responseMessage, String command) {
        if (!requestMap.containsKey(responseMessage.getId())) {
            System.out.println("Unknown response id: " + responseMessage);
            return;
        }
        switch (requestMap.get(responseMessage.getId()).getCmd()) {
            case CommandList.FILES_LIST:
                handleResponseFilesList(responseMessage);
                break;
            case CommandList.FILE_ADD:
                handleResponseFileAdd(responseMessage);
                break;
            case CommandList.FILE_DELETE:
                handleResponseFileDelete(responseMessage);
                break;
            case CommandList.FILE_DOWNLOAD:
                handleResponseFileDownload(responseMessage);
                break;
            default:
                System.out.println(("Unknown command=" + command));
                break;
        }
    }

    private void handleResponseFilesList(ResponseMessage responseMessage) {
        switch (responseMessage.getResponseCode()) {
            case 0:
                break;
            case 1:
//                controller.showAlert("Произошла ошибка при запросе файлов, обновите приложение или обратитесь к системному администратору");
                controller.showNotification(BaseController.NotificationType.ERROR, "Ошибка",
                        "Произошла ошибка при запросе файлов, обновите приложение или обратитесь к системному администратору");
                break;
            default:
                System.out.println("Unknown responseCode=" + responseMessage.getResponseCode()
                        + ", cmd=" + CommandList.SIGN_IN);
                break;
        }
    }

    private void handleResponseFileAdd(ResponseMessage responseMessage) {
        switch (responseMessage.getResponseCode()) {
            case 0:
                // Upload in progress
                break;
            case 1:
                String filePath = lastRequest.getRequest().getOrDefault(RequestFilesList.KEY_FILE_PATH, "");
                if (!filePath.isEmpty()) {
                    sendFile(filePath);
                }
                requestMap.remove(responseMessage.getId());
                break;
            case 2:
                onAuthError(responseMessage);
                break;
            case 3:
//                controller.showAlert("При добавлении файла произошла ошибка, попробуйте еще раз");
                controller.showNotification(BaseController.NotificationType.INFO, "Файл не добавлен",
                        "Файл не удалось загрузить, попробуйте еще раз");
                requestMap.remove(responseMessage.getId());
                break;
            case 4:
//                controller.showAlert("Файл не добавлен: такой файл уже существует");
                controller.showNotification(BaseController.NotificationType.INFO, "Файл не добавлен",
                        "Файл не добавлен: такой файл уже существует");
                requestMap.remove(responseMessage.getId());
                break;
            case 5:
//                controller.showAlert("Файл не добавлен: отсутствует свободное место в облаке");
                controller.showNotification(BaseController.NotificationType.WARNING, "Файл не добавлен",
                        "Файл не добавлен: отсутствует свободное место в облаке");
                requestMap.remove(responseMessage.getId());
                break;
            case 6:
//                controller.showAlert("Сервер сообщил о неверном формате данных. Обновите приложение.");
                controller.showNotification(BaseController.NotificationType.ERROR, "Файл не добавлен",
                        "Файл не добавлен: неверный формат данных, обновите приложение");
                requestMap.remove(responseMessage.getId());
                break;
            default:
                System.out.println("Unknown responseCode=" + responseMessage.getResponseCode()
                        + ", cmd=" + CommandList.SIGN_IN);
                break;
        }
    }

    private void handleResponseFileDelete(ResponseMessage responseMessage) {
        switch (responseMessage.getResponseCode()) {
            case 0:
                break;
            case 1:
//                controller.showAlert("Файл успешно удален");
                requestMap.remove(responseMessage.getId());
                break;
            case 2:
                onAuthError(responseMessage);
                break;
            case 3:
//                controller.showAlert("Сервер сообщил о неверном формате данных. Обновите приложение.");
                controller.showNotification(BaseController.NotificationType.WARNING, "Файл не удален",
                        "Файл не удален: неверный формат данных, обновите приложение");
                requestMap.remove(responseMessage.getId());
                break;
            case 4:
//                controller.showAlert("При удалении файла произошла ошибка, попробуйте еще раз");
                controller.showNotification(BaseController.NotificationType.INFO, "Файл не удален",
                        "Файл не удалось удалить, попробуйте еще раз");
                requestMap.remove(responseMessage.getId());
                break;
            default:
                System.out.println("Unknown responseCode=" + responseMessage.getResponseCode()
                        + ", cmd=" + CommandList.SIGN_IN);
                break;
        }
    }

    private void handleResponseFileDownload(ResponseMessage responseMessage) {
        switch (responseMessage.getResponseCode()) {
            case 0:
                break;
            case 1:
//                controller.showAlert("К сожалению, файл не найден");
                controller.showNotification(BaseController.NotificationType.ERROR, "Файл не найден", "В хранилище отсутствует файл");
                requestMap.remove(responseMessage.getId());
                break;
            case 2:
                onAuthError(responseMessage);
                break;
            case 3:
//                controller.showAlert("Сервер сообщил о неверном формате данных. Обновите приложение.");
                controller.showNotification(BaseController.NotificationType.ERROR, "Файл не загружен",
                        "Файл не загружен: неверный формат данных, обновите приложение");
                requestMap.remove(responseMessage.getId());
                break;
            case 4:
//                controller.showAlert("При загрузке файла произошла ошибка, попробуйте еще раз");
                controller.showNotification(BaseController.NotificationType.INFO, "Файл не загружен",
                        "Файл не удалось загрузить, попробуйте еще раз");
                requestMap.remove(responseMessage.getId());
                break;
            default:
                System.out.println("Unknown responseCode=" + responseMessage.getResponseCode()
                        + ", cmd=" + CommandList.SIGN_IN);
                break;
        }
    }

    private void sendFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                byte[] fileArray = FileHelper.convertToByteArray(filePath);
                if (fileArray == null) {
                    onSendFileError(filePath);
                    return;
                }
                connectionService.getOut().writeObject(new TransferringFile(filePath, fileArray));
            } catch (IOException e) {
                onSendFileError(filePath);
            }
        }
    }

    private void onSendFileError(String filePath) {
//        controller.showAlert("Произошла ошибка при отправке файла на сервер. Попробуйте еще раз");
        controller.showNotification(BaseController.NotificationType.ERROR, "Файл не отправлен",
                "Произошла ошибка при отправке файла на сервер. Попробуйте еще раз");
        requestDeleteFile(filePath);
    }

    @Override
    public void handleFilesListRequest(RequestFilesList requestFilesList) {
        ObservableList<ClientFile> newList = ClientFile.map(requestFilesList.getFilesList());
        if (this.filesList == null || this.filesList.size() > newList.size()) {
            this.filesList = newList;
            controller.updateTableData(filesList);
        } else {
            for (ClientFile clientFile : newList) {
                if (!this.filesList.contains(clientFile)) {
                    this.filesList.add(clientFile);
                    // TODO: get new file from queue
                }
            }
        }
//        controller.setClientFiles(filesList);
//        System.out.println(filesList);
//        controller.onFileListChanged(this.filesList);
    }

    private void onAuthError(ResponseMessage responseMessage) {
//        controller.showAlert("Ошибка аутентификации");
        controller.showNotification(BaseController.NotificationType.ERROR, "Ошибка", "Вы не аутентифицированы на сервере");
        controller.showSignIn();
        requestMap.remove(responseMessage.getId());
    }
}
