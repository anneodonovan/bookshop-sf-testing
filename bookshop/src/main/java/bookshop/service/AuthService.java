package bookshop.service;

import bookshop.model.User;
import bookshop.util.CsvUtil;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles user authentication and access control.
 * Reads/writes to users.csv.
 *
 * users.csv header: username,passwordHash,role
 * Passwords are stored as SHA-256 hashes.
 */
public class AuthService {

    static final String HEADER = "username,passwordHash,role";
    private final String csvPath;

    public AuthService(String csvPath) {
        this.csvPath = csvPath;
    }

    /**
     * Attempts to log in with the given credentials.
     * @return the User if credentials are valid, empty otherwise.
     */
    public Optional<User> login(String username, String password) throws IOException {
        String hash = hashPassword(password);
        for (String line : CsvUtil.readLines(csvPath)) {
            User user = User.fromCsvRow(line);
            if (user.getUsername().equalsIgnoreCase(username)
                    && user.getPasswordHash().equals(hash)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns true if the user has manager-level access.
     */
    public boolean isManager(User user) {
        return user != null && user.isManager();
    }

    /**
     * Returns true if the user has staff or manager access (i.e. any valid role).
     */
    public boolean canProcessSales(User user) {
        return user != null;
    }

    /**
     * Adds a new user. Only a manager can do this in a real system.
     */
    public void addUser(String username, String password, User.Role role) throws IOException {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                throw new IllegalArgumentException("Username already exists: " + username);
            }
        }
        User newUser = new User(username, hashPassword(password), role);
        CsvUtil.appendLine(csvPath, HEADER, newUser.toCsvRow());
    }

    public List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        for (String line : CsvUtil.readLines(csvPath)) {
            users.add(User.fromCsvRow(line));
        }
        return users;
    }

    /** SHA-256 password hashing. */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}