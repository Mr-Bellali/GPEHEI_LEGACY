<?php

// Config 
require_once 'config/db.php';
require_once 'config/jwt.php';

require_once 'middleware/authMiddleware.php';
require_once 'model/user.php';
require_once 'service/teacherService.php';
require_once 'controller/teacherController.php';

// Auth
require_once 'service/authService.php';
require_once 'controller/authController.php';

// Workspace
require_once 'service/workspaceService.php';
require_once 'controller/workspaceController.php';

// Always last
require_once 'router/api.php';