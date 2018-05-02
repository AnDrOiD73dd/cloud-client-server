package presenter;

import base.ClientUtils;
import base.ConnectionService;
import controller.SignInController;
import javafx.application.Platform;
import javafx.event.Event;
import adapter.TransferringFile;
import listener.ConnectionStateListener;
import listener.ResponseListener;
import presenter.BasePresenter;
import protocol.*;
import protocol.handler.ResponseHandler;
import protocol.request.RequestMessage;

import java.io.IOException;

public class SignInPresenter extends BasePresenter implements ResponseListener, ResponseHandler {

    private SignInController controller;
    private ConnectionService connectionService;
    private RequestMessage lastRequest;
    private ConnectionStateListener connectionStateListener;
    private String username;
    private String password;

    public SignInPresenter(SignInController controller) {
        this.controller = controller;
        connectionStateListener = new ConnectionStateListener() {
            @Override
            public void onConnected() {
                signIn();
            }

            @Override
            public void onDisconnected() {
//                controller.showAlert("Подключение с сервером разорвано");
                controller.updateUI(true);
            }

            @Override
            public void onError(String error) {
                controller.showAlert(error);
                controller.updateUI(true);
            }
        };
    }

    private void signIn() {
        try {
            RequestMessage newRequest;
            do {
                newRequest = (RequestMessage) RequestMessageFactory.getLoginMessage(MessageUtil.getId(), username, password);
            } while (lastRequest != null && lastRequest.getId() == newRequest.getId());
            lastRequest = newRequest;
            connectionService.getOut().writeObject(lastRequest.toString());
//            connectionService.getOut().flush();
            controller.setUsername("");
            controller.setPassword("");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            controller.showAlert("Произошла ошибка при отправке данных на сервер");
        }
    }

    public void onSignUpClick(Event event) {
        controller.showSignUp(event);
    }

    public void onSignInClick(String username, String password, String serverAddress, String serverPort) {
        serverAddress = serverAddress.trim();
        serverPort = serverPort.trim();
        if (!isValidServerAddress(serverAddress, serverPort)) {
            controller.showAlert("Указаны невалидные данные сервера");
            return;
        }
        this.username = username.trim();
        this.password = password.trim();
        if (!isValidCredentials(this.username, this.password)) {
            controller.showAlert("Указаны неполные данные авторизации");
            return;
        }
        initConnection(serverAddress, Integer.valueOf(serverPort));
    }

    @Override
    protected void initConnection(String host, Integer port) {
        connectionService = ConnectionService.getInstance();
        connectionService.setResponseListener(this);
        connectionService.addConnectionStateListener(connectionStateListener);
        if (connectionService.isConnected())
            signIn();
        else connectionService.connect(host, port);

    }

    private boolean isValidCredentials(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    @Override
    public void onNewFile(TransferringFile file) {

    }

    @Override
    public void onNewMessage(String message) {
        System.out.println("parseCommand: " + message);
        MessageParser.parse(message, lastRequest, null, this, null);
    }

    @Override
    public void handleResponse(ResponseMessage responseMessage, String command) {
        if (lastRequest.getId() != responseMessage.getId()) {
            System.out.println("Unknown response id: " + responseMessage);
            return;
        }
        switch (lastRequest.getCmd()) {
            case CommandList.SIGN_IN:
                switch (responseMessage.getResponseCode()) {
                    case 0:
                        // TODO: show progress
                        controller.updateUI(false);
                        break;
                    case 1:
                        connectionService.removeConnectionStateListener(connectionStateListener);
                        Platform.runLater(() -> controller.showCloudClient());
                        break;
                    case 2:
                        // TODO: hide progress
                        controller.updateUI(true);
                        ClientUtils.showAlert("Неверый формат данных. Обратитесь к разработчику.");
                        break;
                    case 3:
                        // TODO: hide progress
                        controller.updateUI(true);
                        ClientUtils.showAlert("Неверный логин и/или пароль");
                        break;
                    case 4:
                        // TODO: hide progress
                        controller.updateUI(true);
                        ClientUtils.showAlert("Произошла внутрення ошибка на сервере");
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
