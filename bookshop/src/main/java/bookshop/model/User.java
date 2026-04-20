package bookshop.model;

/**
 * Represents a system user (staff or manager).
 */
public class User {

    public enum Role { STAFF, MANAGER }

    private String username;
    private String passwordHash; // Store hashed passwords — never plain text
    private Role role;

    public User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getUsername()     { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole()           { return role; }

    public boolean isManager() { return role == Role.MANAGER; }

    /** CSV format: username,passwordHash,role */
    public String toCsvRow() {
        return String.join(",", username, passwordHash, role.name());
    }

    public static User fromCsvRow(String csvRow) {
        String[] p = csvRow.split(",", -1);
        if (p.length < 3) throw new IllegalArgumentException("Invalid User CSV row: " + csvRow);
        return new User(p[0].trim(), p[1].trim(), Role.valueOf(p[2].trim()));
    }
}