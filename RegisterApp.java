package registerform;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegisterApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Memuat file FXML
            Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));

            // Membuat scene baru
            Scene scene = new Scene(root);

            // Mengatur stage dengan scene yang baru dibuat
            primaryStage.setScene(scene);
            primaryStage.setTitle("Registrasi Manajemen Perpustakaan Sekolah");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
