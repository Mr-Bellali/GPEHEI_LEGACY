<?php
class WorkspaceModel
{
    private $db;

    public function __construct()
    {
        $this->db = new Database();
    }

    // Get all filieres for a teacher
    public function getTeacherFilieres($teacherId)
    {
        $this->db->query('
            SELECT DISTINCT f.id, f.name_filier, f.short_name
            FROM teacher_module tm
            JOIN module m ON tm.module_id = m.id
            JOIN filiere f ON m.filiere_id = f.id
            WHERE tm.teacher_id = :teacher_id
            ORDER BY f.name_filier
        ');
        $this->db->bind(':teacher_id', $teacherId);
        $result = $this->db->resultSet();
        return $result ? $result : [];
    }

    // Get groups for a specific filiere and promotion
    public function getGroupsByFiliereAndPromotion($filiereId, $promotion)
    {
        $this->db->query('
            SELECT 
                g.id,
                g.group_name,
                g.year_academic_level,
                COUNT(DISTINCT gs.student_id) AS student_count
            FROM groupe g
            LEFT JOIN groupeStudent gs ON g.id = gs.groupe_id AND gs.promotion = g.promotion
            WHERE g.filiere_id = :filiere_id
              AND g.promotion = :promotion
              AND g.groupe_status = "active"
            GROUP BY g.id, g.group_name, g.year_academic_level
            ORDER BY g.group_name
        ');
        $this->db->bind(':filiere_id', $filiereId);
        $this->db->bind(':promotion', $promotion);
        $result = $this->db->resultSet();
        return $result ? $result : [];
    }

    // Get the latest promotion for a filiere
    public function getLatestPromotion($filiereId)
    {
        $this->db->query('
            SELECT MAX(promotion) AS max_promotion
            FROM groupe
            WHERE filiere_id = :filiere_id
        ');
        $this->db->bind(':filiere_id', $filiereId);
        $result = $this->db->single();
        return $result ? $result->max_promotion : null;
    }

    // Get all academic levels (1-5)
    public function getAllAcademicLevels()
    {
        return [
            1 => '1st Year',
            2 => '2nd Year',
            3 => '3rd Year',
            4 => '4th Year',
            5 => '5th Year'
        ];
    }
}
?>