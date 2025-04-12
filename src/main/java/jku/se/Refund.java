package jku.se;

import java.sql.*;
import java.time.LocalDate;

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


    public static double getOldRefundSupermarket() throws SQLException {
        String query = "SELECT old_amount FROM refund WHERE type = 'SUPERMARKET'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("old_amount");
            }
        }
        return 0.0;
    }

    public static void setRefundSupermarket(double amount, LocalDate date) throws SQLException {
        String query = "UPDATE refund SET amount = ?, change_date = ?, old_amount = ? WHERE type = 'SUPERMARKET'";

        double old_amount = getRefundSupermarket();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             stmt.setDouble(1, amount);
             stmt.setDate(2, Date.valueOf(date));
             stmt.setDouble(3, old_amount);
             stmt.executeUpdate();
        }
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

    public static double getOldRefundRestaurant() throws SQLException {
        String query = "SELECT old_amount FROM refund WHERE type = 'RESTAURANT'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("old_amount");
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


    public static void setRefundRestaurant(double amount, LocalDate date) throws SQLException {
        String query = "UPDATE refund SET amount = ?, change_date = ?, old_amount = ? WHERE type = 'RESTAURANT'";

        double old_amount = getRefundRestaurant();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, amount);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setDouble(3, old_amount);
            stmt.executeUpdate();
        }
    }

    public static LocalDate getChangeDateRestaurant() throws SQLException {
        String query = "SELECT change_date FROM refund WHERE type = 'RESTAURANT'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                java.sql.Date changeDateSql = rs.getDate("change_date");
                LocalDate changeDate = changeDateSql.toLocalDate();
                return changeDate;
            }
        }
        return null;
    }

    public static LocalDate getChangeDateSupermarket() throws SQLException{
        String query = "SELECT change_date FROM refund WHERE type = 'SUPERMARKET'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                java.sql.Date changeDateSql = rs.getDate("change_date");
                LocalDate changeDate = changeDateSql.toLocalDate();
                return changeDate;
            }
        }
        return null;
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




    public static double refundCalculation(Double sum, InvoiceType type, LocalDate invoice_date) throws SQLException {
        System.out.println("Refundcalculation");

        // Zuerst die passenden Rückerstattungsdaten holen (je nach Typ: Supermarkt oder Restaurant)
        if (type == InvoiceType.SUPERMARKET) {
            // Holen des Änderungsdatums für den Supermarkt
            LocalDate changeDate = getChangeDateSupermarket();

            if (changeDate != null) {
                // Wenn das Änderungsdatum heute oder in der Zukunft liegt, benutze den aktuellen Betrag
                if (!changeDate.isAfter(invoice_date)) { // "Heute oder später"
                    // Änderungsdatum ist heute oder später => Verwende den aktuellen Betrag (amount)
                    if (getRefundSupermarket() >= sum) {
                        return sum;
                    } else {
                        return getRefundSupermarket();
                    }
                } else {
                    // Wenn das Änderungsdatum in der Vergangenheit liegt, benutze den alten Betrag (old_amount)
                    if (getOldRefundSupermarket() >= sum) {
                        return sum;
                    } else {
                        return getOldRefundSupermarket();
                    }
                }
            }
        }

        if (type == InvoiceType.RESTAURANT) {
            // Holen des Änderungsdatums für das Restaurant
            LocalDate changeDate = getChangeDateRestaurant();

            if (changeDate != null) {
                // Wenn das Änderungsdatum heute oder in der Zukunft liegt, benutze den aktuellen Betrag
                if (!changeDate.isAfter(invoice_date)) { // "Heute oder später"
                    // Änderungsdatum ist heute oder später => Verwende den aktuellen Betrag (amount)
                    if (getRefundRestaurant() >= sum) {
                        return sum;
                    } else {
                        return getRefundRestaurant();
                    }
                } else {
                    // Wenn das Änderungsdatum in der Vergangenheit liegt, benutze den alten Betrag (old_amount)
                    if (getOldRefundRestaurant() >= sum) {
                        return sum;
                    } else {
                        return getOldRefundRestaurant();
                    }
                }
            }
        }

        return 0.0; // Rückerstattung ist 0, falls kein passender Betrag gefunden wurde
    }




}
