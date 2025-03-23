package jku.se;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {

    //123
    // Konstanten für die Datenbankverbindung
    private static final String URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.pwltfjlqripcrhenhgnk";
    private static final String PASSWORD = "ujCpo7WdTPUzWpss"; // Ersetze mit deinem Passwort

    // Methode, um eine Verbindung zur Datenbank zu erhalten
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean insertRechnung(Connection connection, String username, double betrag, String datum, invoice_typ typ,boolean proved) {
        String sql = "INSERT INTO rechnungen (username,betrag, datum, typ,proved) VALUES (?, ?, ?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setDouble(2, betrag);
            pstmt.setDate(3, java.sql.Date.valueOf(datum)); // Wandelt den String in ein SQL-Datum um
            pstmt.setObject(4, typ, java.sql.Types.OTHER);
            pstmt.setBoolean(5, proved);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Rechnung erfolgreich eingefügt!");
                return true;
            } else {
                System.out.println("Keine Rechnung eingefügt.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
        }
        return false;
    }

}
