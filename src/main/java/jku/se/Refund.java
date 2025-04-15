package jku.se;

import java.sql.*;
import java.time.LocalDate;

public class Refund {

    public static double getCurrentSupermarketRefund() throws SQLException {
        return getRefundForDate(LocalDate.now(), "supermarket");
    }

    public static double getCurrentRestaurantRefund() throws SQLException {
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

    public static boolean hasEntryForDate(LocalDate date) throws SQLException {
        String query = "SELECT COUNT(*) FROM refunds WHERE change_date = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public static void setDailyRefunds(double supermarket, double restaurant, LocalDate date) throws SQLException {
        String query = "INSERT INTO refunds (change_date, supermarket, restaurant) VALUES (?, ?, ?) " +
                "ON CONFLICT (change_date) DO UPDATE SET " +
                "supermarket = EXCLUDED.supermarket, " +
                "restaurant = EXCLUDED.restaurant";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setDouble(2, supermarket);
            stmt.setDouble(3, restaurant);
            stmt.executeUpdate();
        }
    }

    public static double refundCalculation(Double sum, InvoiceType type, LocalDate invoiceDate) throws SQLException {
        if (sum == null || sum <= 0 || type == null || invoiceDate == null) {
            return 0.0;
        }

        double refundRate = type == InvoiceType.SUPERMARKET
                ? getRefundForDate(invoiceDate, "supermarket")
                : getRefundForDate(invoiceDate, "restaurant");

        return Math.min(sum, refundRate);
    }
}
