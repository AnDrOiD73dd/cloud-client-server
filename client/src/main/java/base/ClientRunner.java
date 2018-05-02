package base;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientRunner extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ui/layout/layout_sign_in.fxml"));
        primaryStage.setTitle(Constants.APP_NAME + ": Вход в систему");
//        primaryStage.setScene(new Scene(root, 500, 400));
        ClientUtils.setupIcon(primaryStage, getClass());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}