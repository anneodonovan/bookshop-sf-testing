package bookshop;

import bookshop.model.User;
import bookshop.service.AuthService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {
    @TempDir
    Path tempDir;
    private AuthService service;//create instance of the object you're testing

    @BeforeEach
    public void setUp(){
        String csvPath = tempDir.resolve("users.csv").toString();
        service = new AuthService(csvPath);
        //seed test users
        try {
            service.addUser("staffuser", "pass123", User.Role.STAFF);
            service.addUser("manageruser", "securepass", User.Role.MANAGER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //staff tests
    @Test
    public void testStaffLogin_ValidCredentials_ReturnUser() throws IOException {
        //validate staff/admin logins
        Optional<User> result = service.login("staffuser", "pass123");
        assertTrue(result.isPresent());
        assertEquals("staffuser", result.get().getUsername());
    }

    @Test
    public void testStaffLogin_ValidCredentials_HasStaffRole() throws IOException {
        User user = service.login("staffuser", "pass123").orElseThrow();
        assertEquals(User.Role.STAFF, user.getRole());
    }

    @Test
    public void testStaffLogin_WrongPassword_ReturnsEmpty() throws IOException {
        Optional<User> result = service.login("staffuser", "wrongpass");
        assertFalse(result.isPresent());
    }

    @Test
    public void testStaffUser_IsNotManager() throws IOException {
        User user = service.login("staffuser", "pass123").orElseThrow();
        assertFalse(service.isManager(user));
    }

    @Test
    public void testStaffUser_CanProcessSales() throws IOException {
        User user = service.login("staffuser", "pass123").orElseThrow();
        assertTrue(service.canProcessSales(user));
    }

    // manager tests
    @Test
    public void testManagerLogin_ValidCredentials_ReturnsUser() throws IOException {
        Optional<User> result = service.login("manageruser", "securepass");
        assertTrue(result.isPresent());
    }

    @Test
    public void testManagerLogin_HasManagerRole() throws IOException {
        User user = service.login("manageruser", "securepass").orElseThrow();
        assertEquals(User.Role.MANAGER, user.getRole());
    }

    @Test
    public void testManagerUser_IsManager_ReturnsTrue() throws IOException {
        User user = service.login("manageruser", "securepass").orElseThrow();
        assertTrue(service.isManager(user));
    }

}
