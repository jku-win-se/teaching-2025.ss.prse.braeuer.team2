package jku.se;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
        String user = "postgres.pwltfjlqripcrhenhgnk";
        String password = "ujCpo7WdTPUzWpss"; // Replace with your actual password

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to the database!");

            insertRechnung(connection, "User1",19.99, "2024-03-19", invoice_typ.Restaurant, false);
            // Perform database operations here
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public static void insertRechnung(Connection connection, String username, double betrag, String datum, invoice_typ typ,boolean proved) {
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
            } else {
                System.out.println("Keine Rechnung eingefügt.");
            }
        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
        }
    }

}