<?php

class Router
{
    private array $routes = [];

    public function get(string $path, callable $handler): void
    {
        $this->routes['GET'][$path] = $handler;
    }
    public function post(string $path, callable $handler): void
    {
        $this->routes['POST'][$path] = $handler;
    }

    public function put(string $path, callable $handler): void
    {
        $this->routes['PUT'][$path] = $handler;
    }

    public function delete(string $path, callable $handler): void
    {
        $this->routes['DELETE'][$path] = $handler;
    }

    public function dispatch(): void
    {
        $method = $_SERVER['REQUEST_METHOD'];
        $uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);

        foreach ($this->routes[$method] ?? [] as $path => $handler) {
            // Convert /teachers/:id -> regex
            $pattern = preg_replace('#:([a-zA-Z0-9_]+)#', '(?P<$1>[^/]+)', $path);
            $pattern = "#^" . $pattern . "$#";

            if (preg_match($pattern, $uri, $matches)) {
                // Extract named params (:id, :name, etc.)
                $params = array_filter($matches, 'is_string', ARRAY_FILTER_USE_KEY);
                call_user_func($handler, $params);
                return;
            }
        }

        // No route matched
        http_response_code(404);
        echo json_encode(['error' => 'Route not found']);
    }
}

// Handle CORS preflight
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    header('Access-Control-Allow-Origin: http://localhost:3000');
    header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
    header('Access-Control-Allow-Headers: Content-Type, Authorization');
    header('Access-Control-Max-Age: 86400');
    http_response_code(204);
    exit();
}

// Add these to all responses too
header('Access-Control-Allow-Origin: http://localhost:3000');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Register routes
$router = new Router();

// Auth routes (public)
$authController = new AuthController(new AuthService());
$router->post('/auth/login',    [$authController, 'login']);

// Workspace routes
$workspaceController = new WorkspaceController(new WorkspaceService());
$router->get('/workspace/modules', function($p) use ($workspaceController){
    AuthMiddleware::handle();

    // Inject auth id into params
    $p['id'] = $_REQUEST['auth']['sub'];
    $workspaceController->index($p);
});

// Get all the groups for a module by filliere id
$router->get('/workspace/groups/:id', function($p) use ($workspaceController){
    AuthMiddleware::handle();

    $workspaceController->getGroupsForModuleById($p);
});


// Teacher Router, To be removed
$controller = new TeacherController(new TeacherService());

$router->get('/teachers', [$controller, 'index']);
$router->get('/teachers/:id', [$controller, 'show']);
$router->post('/teachers', [$controller, 'store']);
$router->put('/teachers/:id', [$controller, 'update']);
$router->delete('/teachers/:id', [$controller, 'destroy']);



$router->dispatch();
