package model;

import java.time.LocalDateTime;

public class Module {

    private int id;
    private String name;
    private ModuleType type;
    private Integer parentModuleId;
    private int filiereId;
    private ModuleStatus status;
    private Integer semester;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Module() {}

    public Module(int id, String name, ModuleType type, Integer parentModuleId, int filiereId, ModuleStatus status, Integer semester, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentModuleId = parentModuleId;
        this.filiereId = filiereId;
        this.status = status;
        this.semester = semester;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ModuleType getType() { return type; }
    public void setType(ModuleType type) { this.type = type; }

    public Integer getParentModuleId() { return parentModuleId; }
    public void setParentModuleId(Integer parentModuleId) { this.parentModuleId = parentModuleId; }

    public int getFiliereId() { return filiereId; }
    public void setFiliereId(int filiereId) { this.filiereId = filiereId; }

    public ModuleStatus getStatus() { return status; }
    public void setStatus(ModuleStatus status) { this.status = status; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
