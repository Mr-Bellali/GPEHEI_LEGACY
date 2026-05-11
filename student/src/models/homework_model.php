<?php
session_start();
class groupe{
        private $id;
        private $groupeName;
        private $promotion;
        private $filiereId;
        private $groupeStatus;

        public function __construct() {
            $conn=Database::getInstance();
            // Corrected table names and joins
            $query="SELECT g.id
                    FROM groupe g
                    JOIN groupeStudent gs on g.id=gs.groupe_id
                    WHERE gs.student_id=:userId AND g.promotion=(SELECT MAX(g1.promotion)FROM groupe g1)";
            
            $conn->query($query);
            $conn->bind(':userId', $_SESSION['userId']);
            $result = $conn->single();

            if (!$result) {
                return;
            }

            $id = $result->id;
                $query="SELECT g.group_name, g.filiere_id, g.promotion, g.groupe_status
                FROM groupe g
                WHERE id=:id AND g.promotion=(SELECT MAX(g1.promotion)FROM groupe g1)";
                $conn->query($query);
                $conn->bind(':id', $id);
                $result = $conn->single();

            $this->id = $id;
            $this->groupeName=$result->group_name;
            $this->promotion=$result->promotion;
            $this->filiereId=$result->filiere_id;
            $this->groupeStatus=$result->groupe_status;
        }

        public function getHomeWorks($module){
            /*to get the homework for a specific group */ 
            $query="SELECT * 
            FROM homework h
            JOIN homeworkAtachement ha ON h.id=ha.homework_id
            JOIN groupe g ON h.groupe_id=g.id
            JOIN groupeStudent gs ON g.id=gs.groupe_id
            JOIN module m ON m.id=h.module_id
            JOIN teacher_module tm ON m.id=tm.module_id AND g.id=tm.groupe_id
            JOIN teacher t ON tm.teacher_id=t.id
            WHERE :module=m.id AND g.promotion = (SELECT MAX(g1.promotion)FROM groupe g1) AND gs.student_id=:userId AND gs.promotion=(SELECT MAX(g1.promotion)FROM groupe g1)";
            $conn=Database::getInstance();
            $conn->query($query);
            $conn->bind(':userId', $_SESSION['userId']);
            $conn->bind(':module',$module);
            $homeworks = $conn->resultSet();
            return $homeworks;
        }
        public function getCourses($module){
            /*to get the courses of a module for a specific group */ 
            $query="SELECT * 
            FROM course c
            JOIN courseAtachement ca ON c.id=ca.course_id
            JOIN module m ON c.module_id=m.id
            JOIN teacher_module tm ON c.module_id=tm.module_id
            JOIN teacher t ON tm.teacher_id=t.id
            JOIN groupe g ON tm.groupe_id=g.id
            JOIN groupeStudent gs ON g.id=gs.groupe_id
            WHERE :module=m.id AND g.promotion = (SELECT MAX(g1.promotion)FROM groupe g1) AND gs.student_id=:userId AND gs.promotion=(SELECT MAX(g1.promotion)FROM groupe g1)";
            $conn=Database::getInstance();
            $conn->query($query);
            $conn->bind(':userId', $_SESSION['userId']);
            $conn->bind(':module',$module);
            $courses = $conn->resultSet();
            return $courses;
        }
        public function getModulesForCurrentGroup() {
        $conn = Database::getInstance();
        $query = "SELECT DISTINCT m.id, m.module_name AS moduleName
        FROM module m
        JOIN groupe g ON m.filiere_id = g.filiere_id
        WHERE g.id = :groupeId AND g.promotion=(SELECT MAX(g1.promotion) FROM groupe g1)";
        $conn->query($query);
        $conn->bind(':groupeId', $this->id);
        return $conn->resultSet();
    }
}
?>