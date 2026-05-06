<?php
session_start();

// Autoload core files
require_once 'core/database.php';
require_once 'models/auth_model.php';
require_once 'controllers/auth.php';

$controller = new AuthController();

// Simple routing based on POST type or URL
$type = $_POST['type'] ?? $_GET['action'] ?? 'login';

match($type) {
    'login'  => $controller->login(),
    'logout' => $controller->logout(),
    default  => $controller->login()
};