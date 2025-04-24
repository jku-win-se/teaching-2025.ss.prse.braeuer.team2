package jku.se;

public class AccountData {
    private String username;
    private String email;
    private String password;
    private String role;
    private Status status;
    private int failedAttempts;

    public AccountData(String username, String email, String password, String role, Status status, int failedAttempts) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.failedAttempts = failedAttempts;
    }

    // Getter methods
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public Status getStatus() {
        return status;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    // Setter methods
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }
}