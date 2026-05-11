package model;

public class Admin {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String hashedPassword;
    private AdminRole role;
    private String phone;
    private AdminStatus status;

    // les constractor----by defult and withe parame (id or no)
    public Admin() {}

    public Admin(int id, String firstName, String lastName, String email,
                 String hashedPassword, AdminRole role,
                 String phone, AdminStatus status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.phone = phone;
        this.status = status;
    }

    public Admin(String firstName, String lastName, String email,
                 String hashedPassword, AdminRole role,
                 String phone, AdminStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.phone = phone;
        this.status = status;
    }

    // getre & setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public AdminRole getRole() {
        return role;
    }

    public void setRole(AdminRole role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AdminStatus getStatus() {
        return status;
    }

    public void setStatus(AdminStatus status) {
        this.status = status;
    }

    // 🔹 toString (no password for security)
    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                '}';
    }

    public String getPassword() {
        return this.hashedPassword;
    }
}