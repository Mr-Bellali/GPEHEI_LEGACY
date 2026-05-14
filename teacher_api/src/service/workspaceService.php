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
        $this->db->query('SELECT DISTINCT m.id as module_id, m.module_name, m.semester, f.name_filier, f.id as filiere_id
            FROM teacher_module tm
            INNER JOIN module m ON m.id = tm.module_id 
            INNER JOIN filiere f ON f.id = m.filiere_id
            WHERE tm.teacher_id = :id'
        );
        $this->db->bind(':id', $id);
        return $this->db->resultSet();
    }

    // Get all groups for a teacher in a specific module
    public function getGroupsForTeacherModule(int $teacherId, int $moduleId): array
    {
        $this->db->query('SELECT g.*, f.name_filier 
            FROM groupe g
            INNER JOIN filiere f ON f.id = g.filiere_id
            INNER JOIN module m ON m.filiere_id = g.filiere_id
            INNER JOIN teacher_module tm ON tm.module_id = m.id
            WHERE tm.teacher_id = :teacher_id AND m.id = :module_id
            AND g.groupe_status = "active"');
        $this->db->bind(':teacher_id', $teacherId);
        $this->db->bind(':module_id', $moduleId);
        return $this->db->resultSet();
    }

    public function getFlow(int $moduleId, int $groupId): array
    {
        $this->db->query('SELECT p.*, t.first_name, t.last_name 
            FROM post p
            LEFT JOIN teacher t ON t.id = p.teacher_id
            WHERE p.module_id = :module_id AND p.groupe_id = :group_id
            ORDER BY p.id DESC');
        $this->db->bind(':module_id', $moduleId);
        $this->db->bind(':group_id', $groupId);
        return $this->db->resultSet();
    }

    public function getAssignments(int $moduleId, int $groupId): array
    {
        // Get courses
        $this->db->query('SELECT "course" as type, c.id, c.course_name as title, c.content, c.course_status as status, NULL as deadline
            FROM course c
            WHERE c.module_id = :module_id');
        $this->db->bind(':module_id', $moduleId);
        $courses = $this->db->resultSet();

        // Get homeworks
        $this->db->query('SELECT "homework" as type, h.id, h.title, h.content, "assigned" as status, h.deadline
            FROM homework h
            WHERE h.module_id = :module_id AND h.groupe_id = :group_id');
        $this->db->bind(':module_id', $moduleId);
        $this->db->bind(':group_id', $groupId);
        $homeworks = $this->db->resultSet();

        return array_merge($courses, $homeworks);
    }

    public function createPost(array $data): bool
    {
        $this->db->query('INSERT INTO post (title, content, teacher_id, module_id, groupe_id) 
            VALUES (:title, :content, :teacher_id, :module_id, :groupe_id)');
        $this->db->bind(':title', $data['title']);
        $this->db->bind(':content', $data['content']);
        $this->db->bind(':teacher_id', $data['teacher_id']);
        $this->db->bind(':module_id', $data['module_id']);
        $this->db->bind(':groupe_id', $data['groupe_id']);
        return $this->db->execute();
    }

    public function createCourse(array $data): bool
    {
        $this->db->query('INSERT INTO course (course_name, content, module_id) 
            VALUES (:title, :content, :module_id)');
        $this->db->bind(':title', $data['title']);
        $this->db->bind(':content', $data['content']);
        $this->db->bind(':module_id', $data['module_id']);
        return $this->db->execute();
    }

    public function createHomework(array $data): bool
    {
        $this->db->query('INSERT INTO homework (title, content, deadline, teacher_id, groupe_id, module_id) 
            VALUES (:title, :content, :deadline, :teacher_id, :groupe_id, :module_id)');
        $this->db->bind(':title', $data['title']);
        $this->db->bind(':content', $data['content']);
        $this->db->bind(':deadline', $data['deadline']);
        $this->db->bind(':teacher_id', $data['teacher_id']);
        $this->db->bind(':groupe_id', $data['groupe_id']);
        $this->db->bind(':module_id', $data['module_id']);
        return $this->db->execute();
    }
}
?>