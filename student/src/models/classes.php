<?php
    session_start();
    require_once '../core/database.php';
    $userId=$_SESSION["userId"];
    class student{
        private $id;
        private $studentNumber;
        private $firstName;
        private $familyName;
        private $hashedPassword;
        private $birthDate;

        public function __construct($userId) {
            $conn=new Database();
            $query="SELECT s.last_name,s.first_name,s.email,s.hashed_password,s.student_number,s.birth_date
                    FROM student s
                    WHERE s.id=:userID";
            $conn->query($query);
            $conn->bind(':userID', $userId);
            $result = $conn->single();

            $this->id = $userId;
            $this->studentNumber = $result->student_number;
            $this->firstName = $result->first_name;
            $this->familyName = $result->last_name;
            $this->hashedPassword = $result->hashed_password;
            $this->birthDate = $result->birth_date;
        }


    }


    class groupe{
        private $id;
        private $groupeName;
        private $promotion;
        private $filiere_id;
        private $groupeStatus;

        public function __construct($id, $groupeName, $promotion, $filiere_id, $groupeStatus) {

            $this->id = $id;
            $this->groupeName = $groupeName;
            $this->promotion = $promotion;
            $this->filiere_id = $filiere_id;
            $this->groupeStatus = $groupeStatus;
        }

        public function getHomeWorks($module){
            /*to get the homework for a specific group */ 
            $query="SELECT * 
            FROM homework h
            JOIN homeworkAtachement ha ON h.id=ha.homework_id
            JOIN groupe g ON h.groupe_id=g.id
            JOIN groupeStudent gs ON g.id=gs.groupe.id
            JOIN module m ON m.id=h.module_id
            JOIN teacher_module tm ON m.id=tm.module_id AND g.id=tm.groupe_id
            JOIN teacher t ON tm.teacher_id=t.id
            WHERE :module=m.id AND g.promotion = (SELECT MAX(g1.promotion)FROM groupe g1) AND gs.student_id=:userId AND gs.promotion=(SELECT MAX(g1.promotion)FROM groupe g1)";
            $conn=new Database();
            $conn->query($query);
            $conn->bind(':userId', $_SESSION['user_id']);
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
            $conn=new Database();
            $conn->query($query);
            $conn->bind(':userId', $_SESSION['user_id']);
            $conn->bind(':module',$module);
            $courses = $conn->resultSet();
            return $courses;
        }
    }
?>