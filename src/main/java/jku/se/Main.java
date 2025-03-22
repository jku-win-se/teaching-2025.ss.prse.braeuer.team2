package jku.se;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static jku.se.Database.insertRechnung;

public class Main {
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
}
