<?php
session_start();

// Autoload core files
require_once 'core/database.php';
require_once 'models/auth_model.php';
require_once 'models/workspace_model.php';
require_once 'controllers/auth.php';
require_once 'controllers/workspace.php';

$controller = new AuthController();

// Get the action from URL parameter
$action = $_GET['action'] ?? '';

// Handle different actions
switch ($action) {
    case 'workspace':
        // Check if user is logged in
        if (!isset($_SESSION['user_id'])) {
            header('Location: index.php?action=login');
            exit;
        }
        
        // Check if it's an AJAX request for SPA
        $isAjax = isset($_SERVER['HTTP_X_REQUESTED_WITH']) && 
                  $_SERVER['HTTP_X_REQUESTED_WITH'] === 'XMLHttpRequest';
        
        if ($isAjax) {
            // For AJAX requests, just return the workspace content
            $workspaceController = new WorkspaceController();
            $workspaceController->index();
        } else {
            // For regular requests, load the dashboard with workspace
            include 'views/dashboard.php';
        }
        break;
        
    case 'home':
        // Check if user is logged in
        if (!isset($_SESSION['user_id'])) {
            header('Location: index.php?action=login');
            exit;
        }
        
        // Check if it's an AJAX request for SPA
        $isAjax = isset($_SERVER['HTTP_X_REQUESTED_WITH']) && 
                  $_SERVER['HTTP_X_REQUESTED_WITH'] === 'XMLHttpRequest';
        
        // Handle page requests for SPA
        if ($isAjax && isset($_GET['page'])) {
            $page = $_GET['page'];
            
            if ($page === 'workspace') {
                $workspaceController = new WorkspaceController();
                $workspaceController->index();
            } else {
                $pageFile = __DIR__ . "/views/pages/{$page}.php";
                if (file_exists($pageFile)) {
                    include $pageFile;
                } else {
                    http_response_code(404);
                    echo "Page not found";
                }
            }
            exit;
        }
        
        // For regular requests, load the dashboard
        include 'views/dashboard.php';
        break;
        
    case 'logout':
        $controller->logout();
        break;
        
    case 'login':
    default:
        // If already logged in, redirect to home
        if (isset($_SESSION['user_id'])) {
            header('Location: index.php?action=home');
            exit;
        }
        $controller->login();
        break;
}
?>