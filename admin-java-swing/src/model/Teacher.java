package model;

import java.time.LocalDateTime;

public class Teacher {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private TeacherStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Relations (IDs only)
    private Integer filiereId; // nullable if teacher not assigned yet

    // Constructors
    public Teacher() {}

    public Teacher(int id, String firstName, String lastName, String email, String password,
                   TeacherStatus status, LocalDateTime createdAt, LocalDateTime updatedAt,
                   Integer filiereId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.filiereId = filiereId;
    }

    // Getters & Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public TeacherStatus getStatus() { return status; }
    public void setStatus(TeacherStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getFiliereId() { return filiereId; }
    public void setFiliereId(Integer filiereId) { this.filiereId = filiereId; }
}