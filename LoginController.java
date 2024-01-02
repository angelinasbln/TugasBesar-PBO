package loginform;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import koneksi.Koneksi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private ImageView imgicon;

    @FXML
    private TextField tfusername;

    @FXML
    private PasswordField pfpassword;

    @FXML
    private Button btnmasuk;

    @FXML
    private Button btnbatal;

    @FXML
    private Button btndaftar;

    @FXML
    private Label loginStatus;

    @FXML
    private void initialize() {
        // Inisialisasi controller, jika diperlukan
    }

    @FXML
    private void handleButtonMasukAction(ActionEvent event) {
        String username = tfusername.getText();
        String password = pfpassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Gagal", "Username atau password tidak boleh kosong", Alert.AlertType.ERROR);
            return;
        }

        if (authenticate(username, password)) {
            showAlert("Login Berhasil", "Anda berhasil masuk", Alert.AlertType.INFORMATION);
            bersihkanForm();
            try {
                openMainMenu();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Kesalahan", "Tidak dapat membuka menu utama", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Login Gagal", "Username atau password salah", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleButtonBatalAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handleButtonDaftarAction(ActionEvent event) throws IOException {
        loadScene("/registerform/register.fxml");
        Stage currentStage = (Stage) btndaftar.getScene().getWindow();
        currentStage.close();
    }

    private void loadScene(String fxmlPath) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setTitle("Register Manajemen Perpustakaan Sekolah");
        stage.setScene(scene);
        stage.show();
    }

    private boolean authenticate(String username, String password) {
        String sql = "SELECT * FROM tb_user WHERE username = ? AND password = ?";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error saat autentikasi: " + e.getMessage());
        }
        return false;
    }

    private void bersihkanForm() {
        tfusername.setText("");
        pfpassword.setText("");
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void openMainMenu() throws IOException {
        Parent menuUtama = FXMLLoader.load(getClass().getResource("/menuutamaform/menuutama.fxml"));
        Scene menuUtamaScene = new Scene(menuUtama);
        Stage stage = new Stage();
        stage.setTitle("Menu Utama");
        stage.setScene(menuUtamaScene);
        stage.show();

        Stage currentStage = (Stage) btnmasuk.getScene().getWindow();
        currentStage.close();
    }
}