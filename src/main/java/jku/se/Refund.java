package jku.se;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Refund {

    public static double getRefundSupermarket() throws SQLException {
        String query = "SELECT amount FROM refund WHERE type = 'SUPERMARKET'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("amount");
            }
        }
        return 0.0;
    }

    public static void setRefundSupermarket(double amount) throws SQLException {
        String query = "UPDATE refund SET amount = ? WHERE type = 'SUPERMARKET'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             stmt.setDouble(1, amount);
             stmt.executeUpdate();
        }
    }

    public static double getRefundRestaurant() throws SQLException {
        String query = "SELECT amount FROM refund WHERE type = 'RESTAURANT'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("amount");
            }
        }
        return 0.0;
    }

    public static void setRefundRestaurant(double amount) throws SQLException {
        String query = "UPDATE refund SET amount = ? WHERE type = 'RESTAURANT'";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             stmt.setDouble(1, amount);
             stmt.executeUpdate();
        }
    }

    //calculates the refund amount
    public static double refundCalculation(Double sum, InvoiceType type) throws SQLException {

        Refund refund;
        if(type == InvoiceType.SUPERMARKET){
            if(getRefundSupermarket()>=sum){
                return sum;
            }
            else return getRefundSupermarket() ;
        }
        if(type == InvoiceType.RESTAURANT){
            if(getRefundRestaurant()>=sum){
                return sum;
            }
            else return getRefundRestaurant();
        }
        return 0.0;
    }
}
