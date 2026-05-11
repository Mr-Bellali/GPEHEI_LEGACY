package model;

import java.time.LocalDateTime;

public class Filiere {

    private int id;
    private String name;
    private String description;

    private FiliereStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Filiere() {}

    public Filiere(int id, String name, String description,
                   FiliereStatus status,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public FiliereStatus getStatus() { return status; }
    public void setStatus(FiliereStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}