package ui.controller;

import base.ClientUtils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public abstract class BaseController {

    public BaseController() {
    }

    public void showAlert(String message) {
        ClientUtils.showAlert(message);
    }

    public enum NotificationType {
        COMMON,
        CONFIRM,
        WARNING,
        INFO,
        ERROR
    }

    public void showNotification(NotificationType type, String title, String message) {
        Notifications notification = Notifications.create()
                .title(title)
                .text(message)
//                .graphic(null)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT);
        switch (type) {
            case COMMON:
                Platform.runLater(() -> notification.show());
                break;
            case CONFIRM:
                Platform.runLater(() -> notification.showConfirm());
                break;
            case INFO:
                Platform.runLater(() -> notification.showInformation());
                break;
            case WARNING:
                Platform.runLater(() -> notification.showWarning());
                break;
            case ERROR:
                Platform.runLater(() -> notification.showError());
                break;
        }
    }
}
