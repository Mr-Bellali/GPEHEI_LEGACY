package model;

import java.time.LocalDateTime;

public class Student {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private StudentStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Relations (by ID only → no object loading here)
    private int filiereId;
    private Integer groupeId; // nullable

    // Constructors
    public Student() {}

    public Student(int id, String firstName, String lastName, String email, String password,
                   StudentStatus status, LocalDateTime createdAt, LocalDateTime updatedAt,
                   int filiereId, Integer groupeId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.filiereId = filiereId;
        this.groupeId = groupeId;
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

    public StudentStatus getStatus() { return status; }
    public void setStatus(StudentStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getFiliereId() { return filiereId; }
    public void setFiliereId(int filiereId) { this.filiereId = filiereId; }

    public Integer getGroupeId() { return groupeId; }
    public void setGroupeId(Integer groupeId) { this.groupeId = groupeId; }
}