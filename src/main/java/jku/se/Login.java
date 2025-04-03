package jku.se;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {
    private static final int MAX_FAILED_ATTEMPTS = 10;

    public static boolean validateLogin(String username, String password,
                                        StringBuilder userRole, StringBuilder accountStatus) {
        String query = "SELECT password, role, status, failed_attempts FROM accounts WHERE email = ?";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        String role = rs.getString("role");
                        String statusStr = rs.getString("status");
                        Status status = Status.valueOf(statusStr.toUpperCase());
                        int failedAttempts = rs.getInt("failed_attempts");

                        userRole.append(role);
                        accountStatus.append(status.name());

                        if (status == Status.BLOCKED) {
                            conn.commit();
                            return false;
                        }

                        if (password.equals(storedPassword)) {
                            resetFailedAttempts(conn, username);
                            conn.commit();
                            return true;
                        } else {
                            incrementFailedAttempts(conn, username, failedAttempts);
                            conn.commit();
                            return false;
                        }
                    } else {
                        conn.commit();
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Database query error", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }

    private static void resetFailedAttempts(Connection conn, String username) throws SQLException {
        String update = "UPDATE accounts SET failed_attempts = 0, status = 'ACTIVE'::account_status WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    private static void incrementFailedAttempts(Connection conn, String username, int currentAttempts) throws SQLException {
        String update;
        if (currentAttempts + 1 >= MAX_FAILED_ATTEMPTS) {
            update = "UPDATE accounts SET failed_attempts = failed_attempts + 1, status = 'BLOCKED'::account_status WHERE email = ?";
        } else {
            update = "UPDATE accounts SET failed_attempts = failed_attempts + 1 WHERE email = ?";
        }

        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }
}