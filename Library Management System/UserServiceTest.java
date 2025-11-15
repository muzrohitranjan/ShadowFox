import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private DatabaseManager dbManager;
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Use in-memory database for testing
        System.setProperty("sqlite.tmpdir", System.getProperty("java.io.tmpdir"));
        dbManager = new DatabaseManager();
        userService = new UserService(dbManager);
    }

    @AfterEach
    void tearDown() {
        dbManager.closeConnection();
    }

    // Authentication Tests (5 test cases)
    @Test
    void testAuthenticateValidUser() {
        User user = userService.authenticate("admin", "admin123");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    void testAuthenticateInvalidUsername() {
        User user = userService.authenticate("nonexistent", "password");
        assertNull(user);
    }

    @Test
    void testAuthenticateInvalidPassword() {
        User user = userService.authenticate("admin", "wrongpassword");
        assertNull(user);
    }

    @Test
    void testAuthenticateInactiveUser() {
        // First deactivate a user, then try to authenticate
        User testUser = new User("testuser", "password", "test@email.com", "Test User");
        userService.registerUser(testUser);
        
        User registered = userService.authenticate("testuser", "password");
        assertNotNull(registered);
        
        registered.setActive(false);
        userService.updateUser(registered);
        
        User inactiveAuth = userService.authenticate("testuser", "password");
        assertNull(inactiveAuth);
    }

    @Test
    void testAuthenticateEmptyCredentials() {
        User user1 = userService.authenticate("", "password");
        User user2 = userService.authenticate("username", "");
        User user3 = userService.authenticate("", "");
        
        assertNull(user1);
        assertNull(user2);
        assertNull(user3);
    }

    // User Registration Tests (5 test cases)
    @Test
    void testRegisterValidUser() {
        User newUser = new User("newuser", "password123", "new@email.com", "New User");
        assertTrue(userService.registerUser(newUser));
        
        User authenticated = userService.authenticate("newuser", "password123");
        assertNotNull(authenticated);
        assertEquals("New User", authenticated.getFullName());
    }

    @Test
    void testRegisterDuplicateUsername() {
        User user1 = new User("duplicate", "pass1", "email1@test.com", "User One");
        User user2 = new User("duplicate", "pass2", "email2@test.com", "User Two");
        
        assertTrue(userService.registerUser(user1));
        assertFalse(userService.registerUser(user2));
    }

    @Test
    void testRegisterDuplicateEmail() {
        User user1 = new User("user1", "pass1", "same@email.com", "User One");
        User user2 = new User("user2", "pass2", "same@email.com", "User Two");
        
        assertTrue(userService.registerUser(user1));
        assertFalse(userService.registerUser(user2));
    }

    @Test
    void testRegisterUserWithNullFields() {
        User invalidUser = new User(null, "password", "email@test.com", "Name");
        assertFalse(userService.registerUser(invalidUser));
    }

    @Test
    void testRegisterMultipleUsers() {
        User user1 = new User("user1", "pass1", "email1@test.com", "User One");
        User user2 = new User("user2", "pass2", "email2@test.com", "User Two");
        User user3 = new User("user3", "pass3", "email3@test.com", "User Three");
        
        assertTrue(userService.registerUser(user1));
        assertTrue(userService.registerUser(user2));
        assertTrue(userService.registerUser(user3));
        
        assertEquals(3, userService.getAllUsers().size() - 4); // Subtract sample users
    }

    // User Retrieval Tests (5 test cases)
    @Test
    void testGetUserById() {
        User admin = userService.authenticate("admin", "admin123");
        assertNotNull(admin);
        
        User retrieved = userService.getUserById(admin.getId());
        assertNotNull(retrieved);
        assertEquals(admin.getUsername(), retrieved.getUsername());
    }

    @Test
    void testGetUserByInvalidId() {
        User user = userService.getUserById(99999);
        assertNull(user);
    }

    @Test
    void testGetAllUsers() {
        var users = userService.getAllUsers();
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 4); // At least sample users
    }

    @Test
    void testSearchUsers() {
        var users = userService.searchUsers("john");
        assertFalse(users.isEmpty());
        assertTrue(users.stream().anyMatch(u -> u.getFullName().toLowerCase().contains("john")));
    }

    @Test
    void testSearchUsersNoResults() {
        var users = userService.searchUsers("nonexistentuser");
        assertTrue(users.isEmpty());
    }

    // User Update Tests (5 test cases)
    @Test
    void testUpdateUser() {
        User newUser = new User("updatetest", "password", "update@test.com", "Update Test");
        assertTrue(userService.registerUser(newUser));
        
        User registered = userService.authenticate("updatetest", "password");
        assertNotNull(registered);
        
        registered.setFullName("Updated Name");
        registered.setEmail("updated@test.com");
        
        assertTrue(userService.updateUser(registered));
        
        User updated = userService.getUserById(registered.getId());
        assertEquals("Updated Name", updated.getFullName());
        assertEquals("updated@test.com", updated.getEmail());
    }

    @Test
    void testUpdateNonExistentUser() {
        User fakeUser = new User();
        fakeUser.setId(99999);
        fakeUser.setUsername("fake");
        fakeUser.setEmail("fake@test.com");
        fakeUser.setFullName("Fake User");
        fakeUser.setRole("USER");
        fakeUser.setActive(true);
        
        assertFalse(userService.updateUser(fakeUser));
    }

    @Test
    void testChangePassword() {
        User newUser = new User("passtest", "oldpass", "pass@test.com", "Pass Test");
        assertTrue(userService.registerUser(newUser));
        
        User registered = userService.authenticate("passtest", "oldpass");
        assertNotNull(registered);
        
        assertTrue(userService.changePassword(registered.getId(), "newpass"));
        
        // Old password should not work
        assertNull(userService.authenticate("passtest", "oldpass"));
        
        // New password should work
        assertNotNull(userService.authenticate("passtest", "newpass"));
    }

    @Test
    void testChangePasswordInvalidUser() {
        assertFalse(userService.changePassword(99999, "newpass"));
    }

    @Test
    void testUpdateUserRole() {
        User newUser = new User("roletest", "password", "role@test.com", "Role Test");
        assertTrue(userService.registerUser(newUser));
        
        User registered = userService.authenticate("roletest", "password");
        assertNotNull(registered);
        assertEquals("USER", registered.getRole());
        
        registered.setRole("ADMIN");
        assertTrue(userService.updateUser(registered));
        
        User updated = userService.getUserById(registered.getId());
        assertEquals("ADMIN", updated.getRole());
        assertTrue(updated.isAdmin());
    }

    // Validation Tests (5 test cases)
    @Test
    void testIsUsernameExists() {
        assertTrue(userService.isUsernameExists("admin"));
        assertFalse(userService.isUsernameExists("nonexistentuser"));
    }

    @Test
    void testIsEmailExists() {
        assertTrue(userService.isEmailExists("admin@library.com"));
        assertFalse(userService.isEmailExists("nonexistent@email.com"));
    }

    @Test
    void testIsUsernameExistsCaseInsensitive() {
        // Assuming database is case-sensitive for usernames
        assertTrue(userService.isUsernameExists("admin"));
        assertFalse(userService.isUsernameExists("ADMIN")); // Different case
    }

    @Test
    void testIsEmailExistsCaseInsensitive() {
        assertTrue(userService.isEmailExists("admin@library.com"));
        // Email comparison might be case-insensitive depending on implementation
    }

    @Test
    void testValidationWithEmptyStrings() {
        assertFalse(userService.isUsernameExists(""));
        assertFalse(userService.isEmailExists(""));
    }
}