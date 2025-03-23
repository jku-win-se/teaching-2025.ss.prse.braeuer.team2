package jku.se;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import static jku.se.Database.insertRechnung;

public class Main {
    public static void main(String[] args) {
        // Verbindung aufbauen
        try (Connection connection = Database.getConnection()) {
            System.out.println("Connected to the database!");

            // Rechnung einfügen
            Database.insertRechnung(connection, "User1", 19.99, "2024-03-19", invoice_typ.Restaurant, false);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
