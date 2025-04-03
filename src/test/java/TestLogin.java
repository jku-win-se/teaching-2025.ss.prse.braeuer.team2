import jku.se.Database;
import jku.se.Login;
import jku.se.Role;
import jku.se.Status;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class TestLogin {
    private static final String ADMIN_EMAIL = "a@";
    private static final String USER_EMAIL = "u@";
    private static final String ADMIN_PASSWORD = "a";
    private static final String USER_PASSWORD = "u";
    private static final String INVALID_EMAIL = "nonexistent@test.com";
    private static final String INVALID_PASSWORD = "wrongpassword";

    @BeforeEach
    public void setup() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            // Stelle sicher, dass der Zustand der Benutzerdaten vor jedem Test zurückgesetzt wird
            resetUserState(conn, ADMIN_EMAIL);
            resetUserState(conn, USER_EMAIL);

            conn.commit();
        }
    }

    private void resetUserState(Connection conn, String email) throws SQLException {
        String resetQuery = "UPDATE accounts SET failed_attempts = 0, status = ?::account_status WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(resetQuery)) {
            stmt.setString(1, Status.ACTIVE.name()); // Explizites Casting zu account_status
            stmt.setString(2, email);
            stmt.executeUpdate();
        }
    }

    @Test
    public void testValidAdminLogin() {
        StringBuilder userRole = new StringBuilder();
        StringBuilder accountStatus = new StringBuilder();

        boolean result = Login.validateLogin(ADMIN_EMAIL, ADMIN_PASSWORD, userRole, accountStatus);

        assertTrue(result, "Admin-Login sollte erfolgreich sein");
        assertEquals(Role.ADMIN.name(), userRole.toString());
        assertEquals(Status.ACTIVE.name(), accountStatus.toString());
    }

    @Test
    public void testValidUserLogin() {
        StringBuilder userRole = new StringBuilder();
        StringBuilder accountStatus = new StringBuilder();

        boolean result = Login.validateLogin(USER_EMAIL, USER_PASSWORD, userRole, accountStatus);

        assertTrue(result, "User-Login sollte erfolgreich sein");
        assertEquals(Role.USER.name(), userRole.toString());
        assertEquals(Status.ACTIVE.name(), accountStatus.toString());
    }

    @Test
    public void testInvalidPassword() {
        StringBuilder userRole = new StringBuilder();
        StringBuilder accountStatus = new StringBuilder();

        boolean result = Login.validateLogin(USER_EMAIL, INVALID_PASSWORD, userRole, accountStatus);

        assertFalse(result, "Login mit falschem Passwort sollte fehlschlagen");
        assertEquals(Status.ACTIVE.name(), accountStatus.toString());
    }

    @Test
    public void testNonexistentUser() {
        StringBuilder userRole = new StringBuilder();
        StringBuilder accountStatus = new StringBuilder();

        boolean result = Login.validateLogin(INVALID_EMAIL, USER_PASSWORD, userRole, accountStatus);

        assertFalse(result, "Login mit nicht existierendem Benutzer sollte fehlschlagen");
        assertTrue(userRole.toString().isEmpty());
        assertTrue(accountStatus.toString().isEmpty());
    }

    @Test
    public void testBlockedAccount() throws SQLException {
        // Blockierten Benutzer direkt in der Datenbank setzen
        try (Connection conn = Database.getConnection()) {
            String blockQuery = "UPDATE accounts SET status = 'BLOCKED'::account_status, failed_attempts = 10 WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(blockQuery)) {
                stmt.setString(1, "blocked@test.com");
                stmt.executeUpdate();
            }
        }

        StringBuilder userRole = new StringBuilder();
        StringBuilder accountStatus = new StringBuilder();

        boolean result = Login.validateLogin("blocked@test.com", "blocked123", userRole, accountStatus);

        assertFalse(result, "Login mit gesperrtem Account sollte fehlschlagen");
        assertEquals(Status.BLOCKED.name(), accountStatus.toString());
    }

    @Test
    public void testAccountLockAfterMaxAttempts() throws SQLException {
        // Benutzer mit 9 fehlgeschlagenen Versuchen setzen
        try (Connection conn = Database.getConnection()) {
            String setupQuery = "UPDATE accounts SET failed_attempts = 9, status = 'ACTIVE'::account_status WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(setupQuery)) {
                stmt.setString(1, "almostblocked@test.com");
                stmt.executeUpdate();
            }
        }

        StringBuilder userRole = new StringBuilder();
        StringBuilder accountStatus = new StringBuilder();

        // 10. fehlgeschlagener Versuch
        boolean result = Login.validateLogin("almostblocked@test.com", INVALID_PASSWORD, userRole, accountStatus);

        assertFalse(result, "Login sollte fehlschlagen");
        assertEquals(Status.BLOCKED.name(), accountStatus.toString());
    }
}