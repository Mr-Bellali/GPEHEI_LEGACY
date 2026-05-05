package model;

import java.time.LocalDateTime;

public class Module {

    private int id;
    private String name;
    private String code;
    private String description;

    private ModuleStatus status;

    // Relation → many modules belong to one filiere
    private int filiereId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Module() {}

    public Module(int id, String name, String code, String description,
                  ModuleStatus status, int filiereId,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.status = status;
        this.filiereId = filiereId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ModuleStatus getStatus() { return status; }
    public void setStatus(ModuleStatus status) { this.status = status; }

    public int getFiliereId() { return filiereId; }
    public void setFiliereId(int filiereId) { this.filiereId = filiereId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}