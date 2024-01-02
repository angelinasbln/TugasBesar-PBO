package databuku;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import koneksi.Koneksi;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DefaultStringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBukuController {

    @FXML
    private TextField tfidbuku;
    @FXML
    private TextField tfjudul;
    @FXML
    private TextField tfpenulis;
    @FXML
    private TextField tfISBN;
    @FXML
    private TextField tftahun;
    @FXML
    private ComboBox<String> cbstatus;
    @FXML
    private Button btnsimpan;
    @FXML
    private Button btnbatal;

    private Connection conn;

    public DataBukuController() {
        conn = Koneksi.getKoneksi();
    }

    @FXML
    private void initialize() {
        cbstatus.getItems().addAll("Tersedia", "Dipinjam");
        formatISBNField();
        tfidbuku.setText(generateNextId());
        tfidbuku.setEditable(false);
    }

    private void formatISBNField() {
        TextFormatter<String> isbnFormatter = new TextFormatter<>(new DefaultStringConverter(), "", change -> {
            String newText = change.getControlNewText().replaceAll("[^\\d]", "");
            StringBuilder formatted = new StringBuilder();
            int[] limits = {3, 6, 8, 12, 13}; // Indexes where to put hyphens

            for (int i = 0; i < newText.length(); i++) {
                for (int limit : limits) {
                    if (i == limit && i != 0) {
                        formatted.append("-");
                    }
                }
                formatted.append(newText.charAt(i));
            }

            change.setText(formatted.toString());
            change.setRange(0, change.getControlText().length());
            change.setCaretPosition(formatted.length());
            change.setAnchor(formatted.length());

            return change;
        });

        tfISBN.setTextFormatter(isbnFormatter);
    }

    private String generateNextId() {
        String lastId = null;
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT ID_Buku FROM db_buku ORDER BY ID_Buku DESC LIMIT 1");
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                lastId = rs.getString("ID_Buku");
            }
        } catch (SQLException e) {
            System.out.println("Error saat mengambil ID terakhir: " + e.getMessage());
        }

        if (lastId != null && !lastId.isEmpty()) {
            // Check if the length of lastId is at least 3 characters
            if (lastId.length() >= 3) {
                int numPart = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("PB%03d", numPart);
            } else {
                // Handle the situation when lastId is shorter than expected
                // You might want to log this or handle it appropriately
                System.out.println("Unexpected ID format: " + lastId);
            }
        }
        return "PB000";
    }


    @FXML
    private void handleBtnSimpanAction() {
        String newId = generateNextId();
        String sql = "INSERT INTO db_buku (ID_Buku, Judul, Penulis, ISBN, Tahun_Terbit, Status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newId);
            pstmt.setString(2, tfjudul.getText());
            pstmt.setString(3, tfpenulis.getText());
            pstmt.setString(4, tfISBN.getText());
            pstmt.setString(5, tftahun.getText());
            pstmt.setString(6, cbstatus.getValue());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Data berhasil disimpan dengan ID: " + newId);
                // Reset fields after saving
                resetFields();
                // Update the ID field to the next ID
                tfidbuku.setText(generateNextId());
            }
        } catch (SQLException e) {
            System.out.println("Error saat menyimpan data: " + e.getMessage());
        }
    }

    @FXML
    private void handleBtnBatalAction() {
        resetFields();
        // Update the ID field to the next ID
        tfidbuku.setText(generateNextId());
    }

    private void resetFields() {
        // Clear all text fields except for tfidbuku and reset ComboBox
        tfjudul.clear();
        tfpenulis.clear();
        tfISBN.clear();
        tftahun.clear();
        cbstatus.getSelectionModel().clearSelection();
    }
}
