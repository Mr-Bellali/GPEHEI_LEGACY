<?php 
// Check if user is logged in
if (!isset($_SESSION['user_id'])) {
    header('Location: index.php?action=login');
    exit;
}

// Get user data if needed
$user_id = $_SESSION['user_id'];

include "templates/dashboard_template.php";
?>