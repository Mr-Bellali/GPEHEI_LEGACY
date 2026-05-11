<?php
// controllers/StudentController.php

require_once '../models/homework_model.php'; 
require_once '../models/studentModel.php';

class StudentController {
    private $student;
    private $currentGroup;

    public function __construct() {
        // Start session if not already started
        if (session_status() === PHP_SESSION_NONE) {
            session_start();
        }

        // Ensure user is logged in
        if (!isset($_SESSION['userId'])) {
            header('Location: ../controllers/login.php');
            exit;
        }

        $this->student = new student();
        $this->currentGroup = new groupe();
    }

    public function dashboard() {
        // In a real app you would fetch all modules for the student's group.
        // For simplicity, we will define a static list or fetch from DB.
        // Let's assume we have a `Module` model or a method in `Groupe` to get all modules.
        
        $modules = $this->currentGroup->getModulesForCurrentGroup();

        $selectedModuleId = $_GET['module_id'] ?? ($modules[0]->id ?? null);
        
        $courses = [];
        $homeworks = [];

        if ($selectedModuleId) {
            $courses = $this->currentGroup->getCourses($selectedModuleId);
            $homeworks = $this->currentGroup->getHomeWorks($selectedModuleId);
        }

        // Pass data to the view
        include '../views/studentBoard.php';
    }

    // Helper: retrieve all modules for the current group (you need to implement this)
    
}
