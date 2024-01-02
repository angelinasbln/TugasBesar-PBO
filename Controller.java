package menuutamaform;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.stage.Stage;
import koneksi.Koneksi;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Controller {
    // Referensi untuk TableView dan TableColumn
    @FXML private TableView<Buku> tbbuku;
    @FXML private TableColumn<Buku, String> kolomId;
    @FXML private TableColumn<Buku, String> kolomJudul;
    @FXML private TableColumn<Buku, String> kolomPenulis;
    @FXML private TableColumn<Buku, String> kolomISBN;
    @FXML private TableColumn<Buku, String> kolomtahunterbit;
    @FXML private Button btntambah;

    private ObservableList<Buku> bukuList = FXCollections.observableArrayList();

    // Referensi untuk tab Peminjaman
    @FXML private TextField tfnpmpinjam;
    @FXML private TextField tfnamapinjam;
    @FXML private TextField tfidbukupinjam;
    @FXML private TextField tfjudulpinjam;
    @FXML private TextField tfstatuspinjam;
    @FXML private DatePicker dppeminjaman;
    @FXML private Button btnsimpan1;
    @FXML private Button btnbatal1;

    // Referensi untuk tab Pengembalian
    @FXML private TextField tfnpmkembali;
    @FXML private TextField tfnamakembali;
    @FXML private TextField tfidbukukembali;
    @FXML private TextField tfjudulkembali;
    @FXML private DatePicker dppengembalian;
    @FXML private Button btnsimpan2;
    @FXML private Button btnbatal2;

    @FXML
    private void initialize() {
        // Inisialisasi atau setup awal
        tfjudulpinjam.textProperty().addListener((obs, oldText, newText) -> {
            tfidbukupinjam.setText(generateIdBuku(newText));
            tfstatuspinjam.setText(getStatusBuku(tfidbukupinjam.getText()));
        });
        tfjudulkembali.textProperty().addListener((obs, oldText, newText) -> {
            tfidbukukembali.setText(generateIdBuku(newText));
        });

        // Setup TableColumn dengan property dari class Buku
        kolomId.setCellValueFactory(new PropertyValueFactory<>("idBuku"));
        kolomJudul.setCellValueFactory(new PropertyValueFactory<>("judul"));
        kolomPenulis.setCellValueFactory(new PropertyValueFactory<>("penulis"));
        kolomISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        kolomtahunterbit.setCellValueFactory(new PropertyValueFactory<>("tahunTerbit"));

        // Panggil method untuk memuat data buku
        loadBukuData();
    }

    private void loadBukuData() {
        Connection conn = Koneksi.getKoneksi();
        String sql = "SELECT * FROM db_buku";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String idBuku = rs.getString("ID_Buku");
                String judul = rs.getString("Judul");
                String penulis = rs.getString("Penulis");
                String ISBN = rs.getString("ISBN");
                String tahunterbit = rs.getString("Tahun_Terbit");

                bukuList.add(new Buku(idBuku, judul, penulis, ISBN, tahunterbit));
            }
        } catch (SQLException e) {
            showAlert("Error", "Error saat memuat data buku: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        tbbuku.setItems(bukuList);
    }

    public static class Buku {
        private final SimpleStringProperty idBuku;
        private final SimpleStringProperty judul;
        private final SimpleStringProperty penulis;
        private final SimpleStringProperty ISBN;
        private final SimpleStringProperty tahunterbit;

        public Buku(String idBuku, String judul, String penulis, String ISBN, String tahunterbit) {
            this.idBuku = new SimpleStringProperty(idBuku);
            this.judul = new SimpleStringProperty(judul);
            this.penulis = new SimpleStringProperty(penulis);
            this.ISBN = new SimpleStringProperty(ISBN);
            this.tahunterbit = new SimpleStringProperty(tahunterbit);
        }

        public String getIdBuku() {
            return idBuku.get();
        }
        public String getJudul() {
            return judul.get();
        }
        public String getPenulis() {
            return penulis.get();
        }
        public String getISBN() {
            return ISBN.get();
        }
        public String getTahunTerbit() {
            return tahunterbit.get();
        }
    }

    @FXML
    private void handleButtonTambahAction(ActionEvent event) throws IOException {
        loadScene("/databuku/databuku.fxml");
        Stage currentStage = (Stage) btntambah.getScene().getWindow();
        currentStage.close();
    }


    private void loadScene(String fxmlPath) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setTitle("Data Buku");
        stage.setScene(scene);
        stage.show();
    }

    // Handler untuk Peminjaman
    @FXML
    private void handleButtonSimpanPinjamAction() {
        String npm = tfnpmpinjam.getText();
        String nama = tfnamapinjam.getText();
        String idBuku = tfidbukupinjam.getText();
        String judul = tfjudulpinjam.getText();
        LocalDate tanggalPeminjaman = dppeminjaman.getValue();

        if (!npm.isEmpty() && !nama.isEmpty() && !idBuku.isEmpty() && !judul.isEmpty() && tanggalPeminjaman != null) {
            String statusBuku = getStatusBuku(idBuku);
            if ("Dipinjam".equals(statusBuku)) {
                showAlert("Gagal", "Buku sedang dipinjam, silahkan tunggu sampai tersedia kembali", Alert.AlertType.WARNING);
                return;
            }

            Connection conn = Koneksi.getKoneksi();
            if (conn != null) {
                String sql = "INSERT INTO db_peminjaman (NPM, Nama_Peminjam, ID_Buku, Judul_Buku, Tanggal_Peminjaman) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, npm);
                    pstmt.setString(2, nama);
                    pstmt.setString(3, idBuku);
                    pstmt.setString(4, judul);
                    pstmt.setDate(5, Date.valueOf(tanggalPeminjaman));

                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        updateStatusBuku(idBuku, "Dipinjam");
                        showAlert("Sukses", "Data berhasil disimpan", Alert.AlertType.INFORMATION);
                        clearPeminjamanFields(); // Clear fields setelah berhasil menyimpan
                        if (isBukuDikembalikan(idBuku)) {
                            updateStatusBuku(idBuku, "Tersedia");
                        }
                    } else {
                        showAlert("Gagal", "Data gagal disimpan", Alert.AlertType.ERROR);
                    }
                } catch (SQLException e) {
                    showAlert("Error", "Error saat menyimpan data: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Error", "Koneksi ke database gagal", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Validasi", "Semua field harus diisi", Alert.AlertType.WARNING);
        }
    }


    private void updateStatusBuku(String idBuku, String status) {
        try (PreparedStatement pstmt = Koneksi.getKoneksi().prepareStatement("UPDATE db_buku SET Status = ? WHERE ID_Buku = ?")) {
            pstmt.setString(1, status);
            pstmt.setString(2, idBuku);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showAlert("Error", "Error saat mengupdate status buku: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String getStatusBuku(String idBuku) {
        String status = "Tidak Diketahui";
        try (PreparedStatement pstmt = Koneksi.getKoneksi().prepareStatement("SELECT Status FROM db_buku WHERE ID_Buku = ?")) {
            pstmt.setString(1, idBuku);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    status = rs.getString("Status");
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Error saat mengambil status buku: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        return status;
    }

    private String generateIdBuku(String judulBuku) {
        String idBuku = "";
        Connection conn = Koneksi.getKoneksi();
        if (conn != null) {
            String sql = "SELECT ID_Buku FROM db_buku WHERE Judul = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, judulBuku);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    idBuku = rs.getString("ID_Buku");
                }
            } catch (SQLException e) {
                showAlert("Error", "Error saat mencari ID buku: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
        return idBuku;
    }


    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleButtonBatalPinjamAction() {
        // Clear fields pada tab Peminjaman
        clearPeminjamanFields();
    }

    // Handler untuk Pengembalian
    @FXML
    private void handleButtonSimpanKembaliAction() {
        String npm = tfnpmkembali.getText();
        String nama = tfnamakembali.getText();
        String idBuku = tfidbukukembali.getText();
        String judul = tfjudulkembali.getText();
        LocalDate tanggalPengembalian = dppengembalian.getValue();

        if (!npm.isEmpty() && !nama.isEmpty() && !idBuku.isEmpty() && !judul.isEmpty() && tanggalPengembalian != null) {
            String statusBuku = getStatusBuku(idBuku);
            if (!"Dipinjam".equals(statusBuku)) {
                showAlert("Gagal", "Buku ini tidak sedang dipinjam", Alert.AlertType.WARNING);
                return;
            }

            Connection conn = Koneksi.getKoneksi();
            if (conn != null) {
                String sql = "INSERT INTO db_pengembalian (NPM, ID_Buku, Judul_Buku, Nama_Peminjam, Tanggal_Pengembalian) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, npm);
                    pstmt.setString(2, nama);
                    pstmt.setString(3, idBuku);
                    pstmt.setString(4, judul);
                    pstmt.setDate(5, Date.valueOf(tanggalPengembalian));

                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        updateStatusBuku(idBuku, "Tersedia");
                        showAlert("Sukses", "Data pengembalian berhasil disimpan", Alert.AlertType.INFORMATION);
                        clearPengembalianFields(); // Bersihkan field setelah data disimpan
                    } else {
                        showAlert("Gagal", "Data pengembalian gagal disimpan", Alert.AlertType.ERROR);
                    }
                } catch (SQLException e) {
                    showAlert("Error", "Error saat menyimpan data pengembalian: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Error", "Koneksi ke database gagal", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Validasi", "Semua field harus diisi", Alert.AlertType.WARNING);
        }
    }

        // Metode tambahan untuk memeriksa apakah buku telah dikembalikan
        private boolean isBukuDikembalikan(String idBuku) {
            try (PreparedStatement pstmt = Koneksi.getKoneksi().prepareStatement("SELECT COUNT(*) FROM db_pengembalian WHERE ID_Buku = ?")) {
                pstmt.setString(1, idBuku);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            } catch (SQLException e) {
                showAlert("Error", "Error saat memeriksa status pengembalian buku: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            return false;
        }


    @FXML
    private void handleButtonBatalKembaliAction() {
        // Clear fields pada tab Pengembalian
        clearPengembalianFields();
    }

    // Metode untuk membersihkan field pada tab Peminjaman
    private void clearPeminjamanFields() {
        tfnpmpinjam.clear();
        tfnamapinjam.clear();
        tfidbukupinjam.clear();
        tfjudulpinjam.clear();
        dppeminjaman.setValue(null);
    }

    // Metode untuk membersihkan field pada tab Pengembalian
    private void clearPengembalianFields() {
        tfnpmkembali.clear();
        tfnamakembali.clear();
        tfidbukukembali.clear();
        tfjudulkembali.clear();
        dppengembalian.setValue(null);
    }
}
