package papb.com.presensicafe;

public class User {
    private String id;
    private String fullName;
    private String email;
    private long jamJaga;
    private String role;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getJamJaga() {
        return jamJaga;
    }

    public void setJamJaga(long jamJaga) {
        this.jamJaga = jamJaga;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
