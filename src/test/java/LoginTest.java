import jku.se.Database;
import jku.se.Login;
import jku.se.Role;
import jku.se.Status;
import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;


public class LoginTest { //Tests mit AI generiert
    // Test constants
    private static final String ADMIN_EMAIL = "testadmin@jku.at";
    private static final String ADMIN_USERNAME = "test_admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_FIRST_NAME = "Admin";
    private static final String ADMIN_LAST_NAME = "User";

    private static final String USER_EMAIL = "testuser@jku.at";
    private static final String USER_USERNAME = "test_user";
    private static final String USER_PASSWORD = "user123";
    private static final String USER_FIRST_NAME = "Regular";
    private static final String USER_LAST_NAME = "User";

    private static final String BLOCKED_USER_EMAIL = "blocked@jku.at";
    private static final String BLOCKED_USERNAME = "blocked_user";
    private static final String BLOCKED_PASSWORD = "blocked123";
    private static final String BLOCKED_FIRST_NAME = "Blocked";
    private static final String BLOCKED_LAST_NAME = "User";

    private static final String INVALID_EMAIL = "invalid@jku.at";
    private static final String INVALID_PASSWORD = "wrongpass";

    @BeforeEach
    void setUp() throws SQLException {
        createTestAccounts();
    }

    @AfterEach
    void tearDown() throws SQLException {
        cleanupTestAccounts();
        Login.logout();
    }

    private void createTestAccounts() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            // Create admin account
            createAccount(conn, ADMIN_EMAIL, ADMIN_USERNAME, ADMIN_PASSWORD,
                    Role.ADMIN, Status.ACTIVE, 0, ADMIN_FIRST_NAME, ADMIN_LAST_NAME);

            // Create regular user account
            createAccount(conn, USER_EMAIL, USER_USERNAME, USER_PASSWORD,
                    Role.USER, Status.ACTIVE, 0, USER_FIRST_NAME, USER_LAST_NAME);

            // Create blocked user account
            createAccount(conn, BLOCKED_USER_EMAIL, BLOCKED_USERNAME, BLOCKED_PASSWORD,
                    Role.USER, Status.BLOCKED, 10, BLOCKED_FIRST_NAME, BLOCKED_LAST_NAME);

            conn.commit();
        }
    }

    private void createAccount(Connection conn, String email, String username, String password,
                               Role role, Status status, int failedAttempts,
                               String firstName, String lastName) throws SQLException {
        // Delete if exists first
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM accounts WHERE email = ? OR username = ?")) {
            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }

        // Insert new account with all required fields
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO accounts (email, username, password, role, status, failed_attempts, first_name, last_name) " +
                        "VALUES (?, ?, ?, ?::account_type, ?::account_status, ?, ?, ?)")) {
            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, role.name());
            stmt.setString(5, status.name());
            stmt.setInt(6, failedAttempts);
            stmt.setString(7, firstName);
            stmt.setString(8, lastName);
            stmt.executeUpdate();
        }
    }

    private void cleanupTestAccounts() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM accounts WHERE email = ANY(?) OR username = ANY(?)")) {
                stmt.setArray(1, conn.createArrayOf("varchar",
                        new String[]{ADMIN_EMAIL, USER_EMAIL, BLOCKED_USER_EMAIL}));
                stmt.setArray(2, conn.createArrayOf("varchar",
                        new String[]{ADMIN_USERNAME, USER_USERNAME, BLOCKED_USERNAME}));
                stmt.executeUpdate();
            }
        }
    }

    @Test
    void testSuccessfulAdminLogin() {
        StringBuilder role = new StringBuilder();
        StringBuilder status = new StringBuilder();

        boolean result = Login.validateLogin(ADMIN_EMAIL, ADMIN_PASSWORD, role, status);

        assertAll(
                () -> assertTrue(result, "Admin login should succeed"),
                () -> assertEquals(Role.ADMIN.name(), role.toString(), "Should return ADMIN role"),
                () -> assertEquals(Status.ACTIVE.name(), status.toString(), "Should return ACTIVE status"),
                () -> assertEquals(ADMIN_USERNAME, Login.getCurrentUsername(), "Should set current username"),
                () -> assertEquals(ADMIN_EMAIL, Login.getCurrentUserEmail(), "Should set current email"),
                () -> assertEquals(Role.ADMIN, Login.getCurrentUserRole(), "Should set current role"),
                () -> assertEquals(Status.ACTIVE, Login.getCurrentUserStatus(), "Should set current status")
        );
    }

    @Test
    void testSuccessfulUserLogin() {
        StringBuilder role = new StringBuilder();
        StringBuilder status = new StringBuilder();

        boolean result = Login.validateLogin(USER_EMAIL, USER_PASSWORD, role, status);

        assertAll(
                () -> assertTrue(result, "User login should succeed"),
                () -> assertEquals(Role.USER.name(), role.toString(), "Should return USER role"),
                () -> assertEquals(Status.ACTIVE.name(), status.toString(), "Should return ACTIVE status"),
                () -> assertEquals(USER_USERNAME, Login.getCurrentUsername(), "Should set current username"),
                () -> assertEquals(USER_EMAIL, Login.getCurrentUserEmail(), "Should set current email"),
                () -> assertEquals(Role.USER, Login.getCurrentUserRole(), "Should set current role"),
                () -> assertEquals(Status.ACTIVE, Login.getCurrentUserStatus(), "Should set current status")
        );
    }

    @Test
    void testInvalidPassword() throws SQLException {
        StringBuilder role = new StringBuilder();
        StringBuilder status = new StringBuilder();

        boolean result = Login.validateLogin(USER_EMAIL, INVALID_PASSWORD, role, status);

        assertAll(
                () -> assertFalse(result, "Login should fail with wrong password"),
                () -> assertEquals(Role.USER.name(), role.toString(), "Should still return role"),
                () -> assertEquals(Status.ACTIVE.name(), status.toString(), "Status should remain ACTIVE"),
                () -> assertNull(Login.getCurrentUsername(), "No user should be logged in"),
                () -> assertEquals(1, getFailedAttempts(USER_EMAIL), "Failed attempts should increment")
        );
    }

    @Test
    void testNonexistentUser() {
        StringBuilder role = new StringBuilder();
        StringBuilder status = new StringBuilder();

        boolean result = Login.validateLogin(INVALID_EMAIL, USER_PASSWORD, role, status);

        assertAll(
                () -> assertFalse(result, "Login should fail with non-existent user"),
                () -> assertTrue(role.toString().isEmpty(), "Role should be empty"),
                () -> assertTrue(status.toString().isEmpty(), "Status should be empty"),
                () -> assertNull(Login.getCurrentUsername(), "No user should be logged in")
        );
    }

    @Test
    void testBlockedAccount() {
        StringBuilder role = new StringBuilder();
        StringBuilder status = new StringBuilder();

        boolean result = Login.validateLogin(BLOCKED_USER_EMAIL, BLOCKED_PASSWORD, role, status);

        assertAll(
                () -> assertFalse(result, "Login should fail with blocked account"),
                () -> assertEquals(Role.USER.name(), role.toString(), "Should return USER role"),
                () -> assertEquals(Status.BLOCKED.name(), status.toString(), "Should return BLOCKED status"),
                () -> assertNull(Login.getCurrentUsername(), "No user should be logged in")
        );
    }

    @Test
    void testAccountGetsBlockedAfterMaxAttempts() throws SQLException {
        // Set account to one attempt before blocking
        setFailedAttempts(USER_EMAIL, Login.getMaxFailedAttempts() - 1);

        StringBuilder role = new StringBuilder();
        StringBuilder status = new StringBuilder();

        // This attempt should trigger blocking
        boolean result = Login.validateLogin(USER_EMAIL, INVALID_PASSWORD, role, status);

        assertAll(
                () -> assertFalse(result, "Login should fail"),
                () -> assertEquals(Status.BLOCKED.name(), getAccountStatus(USER_EMAIL),
                        "Account should be blocked after max attempts"),
                () -> assertEquals(Login.getMaxFailedAttempts(), getFailedAttempts(USER_EMAIL),
                        "Failed attempts should be at max")
        );
    }

    @Test
    void testFailedAttemptsResetAfterSuccessfulLogin() throws SQLException {
        // Simulate some failed attempts
        setFailedAttempts(USER_EMAIL, 3);

        StringBuilder role = new StringBuilder();
        StringBuilder status = new StringBuilder();

        boolean result = Login.validateLogin(USER_EMAIL, USER_PASSWORD, role, status);

        assertAll(
                () -> assertTrue(result, "Login should succeed"),
                () -> assertEquals(0, getFailedAttempts(USER_EMAIL),
                        "Failed attempts should reset after successful login")
        );
    }

    @Test
    void testLogout() {
        // First login
        Login.validateLogin(USER_EMAIL, USER_PASSWORD, new StringBuilder(), new StringBuilder());

        // Then logout
        Login.logout();

        assertAll(
                () -> assertNull(Login.getCurrentUsername(), "Username should be cleared"),
                () -> assertNull(Login.getCurrentUserEmail(), "Email should be cleared"),
                () -> assertNull(Login.getCurrentUserRole(), "Role should be cleared"),
                () -> assertNull(Login.getCurrentUserStatus(), "Status should be cleared")
        );
    }

    // Helper methods
    private int getFailedAttempts(String email) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT failed_attempts FROM accounts WHERE email = ?")) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getInt("failed_attempts") : -1;
                }
            }
        }
    }

    private String getAccountStatus(String email) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT status FROM accounts WHERE email = ?")) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getString("status") : null;
                }
            }
        }
    }

    private void setFailedAttempts(String email, int attempts) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE accounts SET failed_attempts = ? WHERE email = ?")) {
                stmt.setInt(1, attempts);
                stmt.setString(2, email);
                stmt.executeUpdate();
            }
        }
    }
}