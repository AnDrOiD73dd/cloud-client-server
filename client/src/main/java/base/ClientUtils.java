package base;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ClientUtils {

    public static void showAlert(String msg){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Возникли проблемы");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    public static void setupIcon(Stage stage, Class cl) {
        stage.getIcons().add(new Image(cl.getResourceAsStream("/images/happy-cloud-480.png")));
    }
}
