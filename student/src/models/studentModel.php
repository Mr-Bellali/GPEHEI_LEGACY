<?php
    session_start();
    require_once '../core/database.php';
    class student{
        private $id;
        private $studentNumber;
        private $firstName;
        private $familyName;
        private $hashedPassword;
        private $birthDate;

        public function __construct() {
            $userId = $_SESSION['userId'] ?? 0;
            $conn = Database::getInstance();
            $query="SELECT s.last_name,s.first_name,s.email,s.student_number,s.birth_date
                    FROM student s
                    WHERE s.id=:userID";
            $conn->query($query);
            $conn->bind(':userID', $userId);
            $result = $conn->single();

            $this->id = $userId;
            $this->studentNumber = $result->student_number;
            $this->firstName = $result->first_name;
            $this->familyName = $result->last_name; 
            $this->birthDate = $result->birth_date;
        }
        public function submitAssignment($homeworkId){
            $query0="SELECT h.deadline
                    FROM homework h
                    WHERE h.id=:homeworkId";

            $conn=Database::getInstance();
            $conn->query($query0);
            $conn->bind(':homeworkId', $homeworkId);
            $deadline = $conn->single()->deadline;
            /*if(strtotime($deadline) < time()){
                return false; // Deadline has passed
            }*/
            /*to submit an assignment for a specific homework */ 
            $query1="INSERT INTO submission (homework_id, student_id, submission_date) VALUES (:homeworkId, :studentId, NOW())";
            $conn->query($query1);
            $conn->bind(':homeworkId', $homeworkId);
            $conn->bind(':studentId', $this->id);
            $conn->execute();

            $query2="INSERT INTO submissionAtachment (attachement_name, link, submission_id) VALUES (:attachmentName, :filePath, :submissionId)";
            $conn->query($query2);
            $conn->bind(':attachmentName', $attachmentName);
            $conn->bind(':filePath', $filePath);
            $conn->bind(':submissionId', $conn->lastInsertId());
            $conn->execute();
        }


    }