package jku.se;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {
    private static final int MAX_FAILED_ATTEMPTS = 10;

    private static String currentUsername;
    private static String currentUserEmail;
    private static Role currentUserRole;
    private static Status currentUserStatus;

    public static boolean validateLogin(String email, String password, StringBuilder userRole, StringBuilder accountStatus) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                AccountData account = findAccountByEmail(conn, email);

                if (account == null) {
                    conn.commit();
                    return false;
                }

                userRole.append(account.role);
                accountStatus.append(account.status.name());

                if (account.status == Status.BLOCKED) {
                    conn.commit();
                    return false;
                }

                if (password.equals(account.password)) {
                    currentUsername = account.username;
                    currentUserEmail = email;
                    currentUserRole = Role.valueOf(account.role);
                    currentUserStatus = account.status;

                    resetFailedAttempts(conn, email);
                    conn.commit();
                    return true;
                } else {
                    incrementFailedAttempts(conn, email, account.failedAttempts);
                    conn.commit();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Database error", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Connection error", e);
        }
    }

    private static AccountData findAccountByEmail(Connection conn, String email) throws SQLException {
        String query = "SELECT username, email, password, role, status, failed_attempts FROM accounts WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AccountData(
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role"),
                            Status.valueOf(rs.getString("status")),
                            rs.getInt("failed_attempts")
                    );
                }
            }
        }
        return null;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public static Role getCurrentUserRole() {
        return currentUserRole;
    }

    public static Status getCurrentUserStatus() {
        return currentUserStatus;
    }

    public static void logout() {
        currentUsername = null;
        currentUserEmail = null;
        currentUserRole = null;
        currentUserStatus = null;
    }

    private static void resetFailedAttempts(Connection conn, String email) throws SQLException {
        updateAccountStatus(conn, email, "SET failed_attempts = 0, status = 'ACTIVE'::account_status");
    }

    //Chat GPT Anfang
    private static void incrementFailedAttempts(Connection conn, String email, int currentAttempts) throws SQLException {
        String action = currentAttempts + 1 >= MAX_FAILED_ATTEMPTS
                ? "SET failed_attempts = failed_attempts + 1, status = 'BLOCKED'::account_status"
                : "SET failed_attempts = failed_attempts + 1";
        updateAccountStatus(conn, email, action);
    }

    private static void updateAccountStatus(Connection conn, String email, String updateClause) throws SQLException {
        String sql = "UPDATE accounts " + updateClause + " WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        }
    }
    //Chat GPT Ende

    private static class AccountData {
        final String username;
        final String email;
        final String password;
        final String role;
        final Status status;
        final int failedAttempts;

        AccountData(String username, String email, String password, String role, Status status, int failedAttempts) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.role = role;
            this.status = status;
            this.failedAttempts = failedAttempts;
        }
    }
}