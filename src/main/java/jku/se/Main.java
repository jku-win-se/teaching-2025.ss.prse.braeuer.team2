package jku.se;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static jku.se.Database.uploadImage;

public class Main {
    public static void main(String[] args) {
        // Verbindung aufbauen
        try (Connection connection = Database.getConnection()) {
            System.out.println("Connected to the database!");



            File imageFile = new File("C:/Users/Lukas/Desktop/Rechnungen/5.pdf"); // Pfad zur Datei

            // Rechnung einfügen
            Database.insertInvoice(connection, "User1", 19.99, "2024-02-20", invoice_typ.Restaurant, false, imageFile);

        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }

    }
}
