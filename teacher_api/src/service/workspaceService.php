<?php
class WorkspaceService
{
    private Database $db;
    public function __construct()
    {
        $this->db = Database::getInstance();
    }

    // Get all the modules from the database for the teacher id 
    public function getAllModulesForTeacherId(int $id): array
    {
        $this->db->query('SELECT distinct m.id, tm.teacher_id, g.year_academic_level, m.module_name, m.filiere_id from teacher_module tm
            INNER JOIN module m ON m.id = tm.module_id 
            JOIN groupe g ON m.filiere_id = g.filiere_id
            where tm.teacher_id = :id and g.promotion = (select max(g1.promotion) from groupe g1)'
        );
        $this->db->bind(':id', $id);
        return $this->db->resultSet();
    }

    // Get all the groups for a module by id
    public function getAllGroupsForModuleId(int $id): array
    {
        $this->db->query('SELECT g.* FROM groupe g
        INNER JOIN module m ON m.filiere_id = g.filiere_id
        WHERE m.id = :id
        AND g.groupe_status = "active"');
        $this->db->bind(':id', $id);
        return $this->db->resultSet();
    }
}
?>