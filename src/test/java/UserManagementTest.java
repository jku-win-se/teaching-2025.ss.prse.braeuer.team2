import jku.se.UserManagement;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserManagementTest {
    private static final String TEST_USERNAME = "testuser_junit";
    private static final String TEST_EMAIL = "testuser_junit@example.com";
    private static final String TEST_PASSWORD = "testpass";

    @BeforeEach
    void setUp() throws SQLException {
        UserManagement.createUser(
                "Test",
                "User",
                TEST_USERNAME,
                TEST_EMAIL,
                TEST_PASSWORD,
                "USER"
        );
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (UserManagement.getUser(TEST_USERNAME) != null) {
            UserManagement.deleteUser(TEST_USERNAME);
        }
    }

    // ========== EXISTING TESTS ==========
    @Test
    void testGetUser() throws SQLException {
        UserManagement.User user = UserManagement.getUser(TEST_USERNAME);
        assertUserFields(user, "Test", "User", TEST_EMAIL, "USER", "ACTIVE", 0);
    }

    @Test
    void testGetNonExistentUser() throws SQLException {
        assertNull(UserManagement.getUser("nonexistentuser"));
    }

    @Test
    void testUpdateUser() throws SQLException {
        UserManagement.User user = UserManagement.getUser(TEST_USERNAME);
        updateUserFields(user, "Updated", "Name", "updated@test.com", "ADMIN", "BLOCKED", 3, "newpass");

        assertTrue(UserManagement.updateUser(user));
        UserManagement.User updatedUser = UserManagement.getUser(TEST_USERNAME);
        assertUserFields(updatedUser, "Updated", "Name", "updated@test.com", "ADMIN", "BLOCKED", 3);
    }

    @Test
    void testCreateAndDeleteUser() throws SQLException {
        String newUsername = "newtestuser_junit";
        try {
            assertTrue(UserManagement.createUser(
                    "New", "User", newUsername, "new@test.com", "pass", "USER"));
            assertNotNull(UserManagement.getUser(newUsername));
        } finally {
            UserManagement.deleteUser(newUsername);
        }
    }

    @Test
    void testDeleteNonExistentUser() throws SQLException {
        assertFalse(UserManagement.deleteUser("nonexistentuser"));
    }

    @Test
    void testCreateDuplicateUser() throws SQLException {
        assertFalse(UserManagement.createUser(
                "Duplicate", "User", TEST_USERNAME, "dup@test.com", "pass", "USER"));
    }

    // ========== NEW TESTS FOR HIGHER COVERAGE ==========

    @Test
    void testGetUserCreatedAt() throws SQLException, ParseException {
        UserManagement.User user = UserManagement.getUser(TEST_USERNAME);
        assertNotNull(user.createdAt);

        // Verify date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date parsedDate = dateFormat.parse(user.createdAt);
        assertNotNull(parsedDate);
    }

    @Test
    void testCreateUserWithNullFields() {
        assertThrows(SQLException.class, () -> {
            UserManagement.createUser(null, null, null, null, null, null);
        });
    }

    @Test
    void testCreateUserWithInvalidRole() {
        assertThrows(SQLException.class, () ->
                UserManagement.createUser("Invalid", "Role", "invalidrole", "role@test.com", "pass", "INVALID_ROLE"));
    }

    @Test
    void testUpdateUserWithInvalidStatus() throws SQLException {
        UserManagement.User user = UserManagement.getUser(TEST_USERNAME);
        user.status = "INVALID_STATUS";
        assertThrows(SQLException.class, () -> UserManagement.updateUser(user));
    }

    @Test
    void testCaseSensitiveUsername() throws SQLException {
        String mixedCaseUsername = TEST_USERNAME.toUpperCase();
        UserManagement.User user = UserManagement.getUser(mixedCaseUsername);
        assertNull(user, "Username should be case sensitive");
    }

    @Test
    void testMassUserOperations() throws SQLException {
        // Test handling multiple operations
        for (int i = 0; i < 5; i++) {
            String tempUser = "tempuser_" + i;
            try {
                assertTrue(UserManagement.createUser("Temp", "User", tempUser, tempUser + "@test.com", "pass", "USER"));
                assertNotNull(UserManagement.getUser(tempUser));
            } finally {
                UserManagement.deleteUser(tempUser);
            }
        }
    }

    // Helper methods
    private void assertUserFields(UserManagement.User user, String firstName, String lastName,
                                  String email, String role, String status, int failedAttempts) {
        assertNotNull(user);
        assertEquals(firstName, user.firstName);
        assertEquals(lastName, user.lastName);
        assertEquals(email, user.email);
        assertEquals(role, user.role);
        assertEquals(status, user.status);
        assertEquals(failedAttempts, user.failedAttempts);
    }

    private void updateUserFields(UserManagement.User user, String firstName, String lastName,
                                  String email, String role, String status, int failedAttempts, String password) {
        user.firstName = firstName;
        user.lastName = lastName;
        user.email = email;
        user.role = role;
        user.status = status;
        user.failedAttempts = failedAttempts;
        user.password = password;
    }
}