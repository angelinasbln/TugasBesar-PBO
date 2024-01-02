package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {

    private static final String URL = "jdbc:mysql://localhost:3306/ManajemenPerpustakaanSekolah";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getKoneksi() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Berhasil Koneksi ke Database");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.out.println("Gagal Koneksi: Driver tidak ditemukan " + e);
        } catch (SQLException e) {
            System.out.println("Gagal Koneksi Database: " + e);
        }
        return null;
    }
}
