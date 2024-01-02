package registerform;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import javafx.stage.Stage;
import koneksi.Koneksi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private ImageView imgicon1;

    @FXML
    private Label lblManajemenPerpustakaan;

    @FXML
    private TextField tfnama;

    @FXML
    private TextField tfnpm;

    @FXML
    private TextField tfusername;

    @FXML
    private PasswordField pfpassword;

    @FXML
    private Button btnmasuk;

    @FXML
    private Button btnBatal;

    @FXML
    private Button btnDaftar;

    @FXML
    private Label lblLanjutMasukAplikasi;

    // Metode inisialisasi jika diperlukan
    @FXML
    private void initialize() {
        // Inisialisasi awal untuk controller
    }

    @FXML
    private void handleButtonMasukAction(ActionEvent event) {
        try {
            // Load login form
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginform/login.fxml"));
            Parent root = loader.load();

            // Create a new stage for the login form
            Stage stage = new Stage();
            stage.setTitle("Log in Manajemen Perpustakaan Sekolah");
            stage.setScene(new Scene(root));

            // Show the login form
            stage.show();

            // Close the current (register) form
            Stage currentStage = (Stage) btnmasuk.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load the login form.", Alert.AlertType.ERROR);
        }
    }



    @FXML
    private void handleButtonBatalAction(ActionEvent event) {
        // Handle ketika tombol batal diklik
        Platform.exit(); // Menutup aplikasi
    }

    @FXML
    private void handleButtonDaftarAction() {
        String nama = tfnama.getText();
        String npm = tfnpm.getText();
        String username = tfusername.getText();
        String password = pfpassword.getText();

        // Memastikan semua field terisi
        if (nama.isEmpty() || npm.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Masih ada kolom yang belum terisi", Alert.AlertType.ERROR);
        } else if (cekDataTerdaftar(nama, npm, username)) {
            showAlert("Error", "Nama/NPM/Username sudah terdaftar", Alert.AlertType.ERROR);
        } else {
            simpanDataUser(nama, npm, username, password);
        }
    }

    private boolean cekDataTerdaftar(String nama, String npm, String username) {
        String sql = "SELECT COUNT(*) FROM tb_user WHERE Nama = ? OR NPM = ? OR Username = ?";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nama);
            pstmt.setString(2, npm);
            pstmt.setString(3, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saat memeriksa data: " + e.getMessage());
        }
        return false;
    }

    private void simpanDataUser(String nama, String npm, String username, String password) {
        String sql = "INSERT INTO tb_user (Nama, NPM, Username, Password) VALUES (?, ?, ?, ?)";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nama);
            pstmt.setString(2, npm);
            pstmt.setString(3, username);
            pstmt.setString(4, password);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Data berhasil disimpan.");
                showAlert("Data Tersimpan", "Data berhasil disimpan.", Alert.AlertType.INFORMATION);
                bersihkanForm(); // Membersihkan form setelah data berhasil disimpan
            } else {
                System.out.println("Data tidak berhasil disimpan.");
            }
        } catch (SQLException e) {
            System.out.println("Error saat menyimpan data: " + e.getMessage());
        }
    }

    private void bersihkanForm() {
        tfnama.setText("");
        tfnpm.setText("");
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
}