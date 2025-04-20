import jku.se.UserManagement;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class UserManagementTest { //Tests mit AI generiert

    private static final String TEST_USERNAME = "testuser_junit";
    private static final String TEST_EMAIL = "testuser_junit@example.com";

    @BeforeEach
    void setUp() throws SQLException {
        // Create a test user before each test
        UserManagement.createUser(
                "Test",
                "User",
                TEST_USERNAME,
                TEST_EMAIL,
                "testpass",
                "USER"
        );
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up after each test
        if (UserManagement.getUser(TEST_USERNAME) != null) {
            UserManagement.deleteUser(TEST_USERNAME);
        }
    }

    @Test
    void testGetUser() throws SQLException {
        UserManagement.User user = UserManagement.getUser(TEST_USERNAME);

        assertNotNull(user, "User should exist");
        assertEquals("Test", user.firstName);
        assertEquals("User", user.lastName);
        assertEquals(TEST_EMAIL, user.email);
        assertEquals("USER", user.role);
        assertEquals("ACTIVE", user.status);
        assertEquals(0, user.failedAttempts);
    }

    @Test
    void testGetNonExistentUser() throws SQLException {
        UserManagement.User user = UserManagement.getUser("nonexistentuser");
        assertNull(user, "Should return null for non-existent user");
    }

    @Test
    void testUpdateUser() throws SQLException {
        UserManagement.User user = UserManagement.getUser(TEST_USERNAME);
        user.firstName = "Updated";
        user.lastName = "Name";
        user.role = "ADMIN";
        user.status = "BLOCKED";
        user.failedAttempts = 3;

        boolean result = UserManagement.updateUser(user);
        assertTrue(result, "Update should succeed");

        UserManagement.User updatedUser = UserManagement.getUser(TEST_USERNAME);
        assertEquals("Updated", updatedUser.firstName);
        assertEquals("Name", updatedUser.lastName);
        assertEquals("ADMIN", updatedUser.role);
        assertEquals("BLOCKED", updatedUser.status);
        assertEquals(3, updatedUser.failedAttempts);
    }

    @Test
    void testCreateUser() throws SQLException {
        String newUsername = "newtestuser_junit";
        try {
            boolean result = UserManagement.createUser(
                    "New",
                    "TestUser",
                    newUsername,
                    "newtest@example.com",
                    "password",
                    "USER"
            );

            assertTrue(result, "Creation should succeed");
            assertNotNull(UserManagement.getUser(newUsername), "User should exist after creation");
        } finally {
            // Clean up
            UserManagement.deleteUser(newUsername);
        }
    }

    @Test
    void testDeleteUser() throws SQLException {
        boolean result = UserManagement.deleteUser(TEST_USERNAME);
        assertTrue(result, "Deletion should succeed");
        assertNull(UserManagement.getUser(TEST_USERNAME), "User should not exist after deletion");
    }

    @Test
    void testDeleteNonExistentUser() throws SQLException {
        boolean result = UserManagement.deleteUser("nonexistentuser");
        assertFalse(result, "Should return false for non-existent user");
    }
}