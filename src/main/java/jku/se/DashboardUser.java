package jku.se;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardUser {

    String currentUsername = Login.getCurrentUsername();


    public static int getEingereichteRechnungen() throws SQLException {
        String sql = "SELECT COUNT(*) FROM rechnungen WHERE username = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, Login.getCurrentUsername());  // hier wird 'user' als Variable gesetzt

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }


    public static int getGenehmigteErstattungen() throws SQLException {
        String sql = "SELECT COUNT(*) FROM rechnungen WHERE username = ? AND status = 'ACCEPTED'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, Login.getCurrentUsername());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public static int getOffeneErstattungen() throws SQLException {
        String sql = "SELECT COUNT(*) FROM rechnungen WHERE username = ? AND status = 'PENDING'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, Login.getCurrentUsername());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public static double getGesamterstattungen() throws SQLException {
        String sql = "SELECT SUM(refund) FROM rechnungen WHERE username = ? AND status = 'ACCEPTED'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, Login.getCurrentUsername());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}
