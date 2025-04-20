package jku.se;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class Refund {

    private LocalDate changeDate;
    private double restaurant;
    private double supermarket;
    private String admin;

    //constructor
    public Refund(LocalDate changeDate, double restaurant, double supermarket, String admin) {
        this.changeDate = changeDate;
        this.restaurant = restaurant;
        this.supermarket = supermarket;
        this.admin = admin;
    }

    //get-methods
    public double getRestaurant() {
        return this.restaurant;
    }
    public double getSupermarket() {
        return this.supermarket;
    }
    public LocalDate getChangeDate() {
        return this.changeDate;
    }
    public String getAdmin() {
        return this.admin;
    }
    public static double getRefundSupermarket() throws SQLException {
        return getRefundForDate(LocalDate.now(), "supermarket");
    }
    public static double getRefundRestaurant() throws SQLException {
        return getRefundForDate(LocalDate.now(), "restaurant");
    }
    public static double getRefundForDate(LocalDate date, String column) throws SQLException {
        String query = "SELECT " + column + " FROM refunds WHERE change_date <= ? " +
                "ORDER BY change_date DESC LIMIT 1";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getDouble(column) : 0.0;
        }
    }


    // Inserts or updates the refund rates for a specific date (AI)
    public static void setDailyRefunds(double supermarket, double restaurant, LocalDate date, String currentUsername) throws SQLException {
        // SQL query using UPSERT: inserts new values or updates existing ones if the date already exists
        String query = "INSERT INTO refunds (change_date, supermarket, restaurant, admin) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (change_date) DO UPDATE SET " + // Handles conflict on change_date
                "supermarket = EXCLUDED.supermarket, " +
                "restaurant = EXCLUDED.restaurant, " +
                "admin = EXCLUDED.admin";

        // Establish connection and prepare the SQL statement
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setDouble(2, supermarket);
            stmt.setDouble(3, restaurant);
            stmt.setString(4, currentUsername);
            stmt.executeUpdate();
        }
    }

    // Calculates the refund amount for a given invoice (AI)
    public static double refundCalculation(Double sum, InvoiceType type, LocalDate invoiceDate) throws SQLException {
        if (sum == null || sum <= 0 || type == null || invoiceDate == null) {
            return 0.0;
        }

        // Get the refund rate based on the invoice type and date
        double refundRate = type == InvoiceType.SUPERMARKET
                ? getRefundForDate(invoiceDate, "supermarket")
                : getRefundForDate(invoiceDate, "restaurant");

        // Refund cannot exceed the invoice total
        return Math.min(sum, refundRate);
    }

    // Retrieves all refund entries from the database for displaying in a TableView
    public static ObservableList<Refund> getAllRefunds() throws SQLException {
        ObservableList<Refund> refunds = FXCollections.observableArrayList();
        String query = "SELECT * FROM refunds ORDER BY change_date DESC"; // Newest entries firs

        // Open connection and execute query
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Loop through result set and build Refund objects
            while (rs.next()) {
                LocalDate date = rs.getDate("change_date").toLocalDate();
                double restaurant = rs.getDouble("restaurant");
                double supermarket = rs.getDouble("supermarket");
                String admin = rs.getString("admin");

                refunds.add(new Refund(date, restaurant, supermarket, admin)); // Add to list
            }
        }
        return refunds; // Return the full list of refunds
    }
}
