package jku.se;

import java.sql.*;
import java.text.SimpleDateFormat;

public class UserManagement {

    public static class User {
        public String firstName;
        public String lastName;
        public String username;
        public String email;
        public String role;
        public String status;
        public int failedAttempts;
        public String createdAt;
    }

    public static User getUser(String username) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT first_name, last_name, username, email, " +
                    "role::text, status::text, failed_attempts, \"createdAt\" " +
                    "FROM accounts WHERE username = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.firstName = rs.getString("first_name");
                user.lastName = rs.getString("last_name");
                user.username = rs.getString("username");
                user.email = rs.getString("email");
                user.role = rs.getString("role");
                user.status = rs.getString("status");
                user.failedAttempts = rs.getInt("failed_attempts");

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                user.createdAt = dateFormat.format(rs.getTimestamp("createdAt"));

                return user;
            }
            return null;
        }
    }

    public static boolean updateUser(User user) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE accounts SET " +
                    "first_name = ?, last_name = ?, email = ?, " +
                    "role = ?::account_type, status = ?::account_status, " +
                    "failed_attempts = ? WHERE username = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user.firstName);
            stmt.setString(2, user.lastName);
            stmt.setString(3, user.email);
            stmt.setString(4, user.role);
            stmt.setString(5, user.status);
            stmt.setInt(6, user.failedAttempts);
            stmt.setString(7, user.username);

            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean deleteUser(String username) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            String query = "DELETE FROM accounts WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean createUser(String firstName, String lastName, String username,
                                     String email, String password, String role) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            String query = "INSERT INTO accounts (first_name, last_name, username, email, " +
                    "password, role, status, failed_attempts) " +
                    "VALUES (?, ?, ?, ?, ?, ?::account_type, 'ACTIVE'::account_status, 0)";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, username);
            stmt.setString(4, email);
            stmt.setString(5, password);
            stmt.setString(6, role);

            return stmt.executeUpdate() > 0;
        }
    }
}