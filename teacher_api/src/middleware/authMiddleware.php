<?php

class AuthMiddleware
{
    public static function handle(): void
    {
        $headers = getallheaders();
        $auth = $headers['Authorization'] ?? '';

        if (!str_starts_with($auth, 'Bearer ')) {
            http_response_code(401);
            echo json_encode(['error' => 'Token missing']);
            exit;
        }

        $token = substr($auth, 7);
        $payload = JWT::verify($token);

        if (!$payload) {
            http_response_code(401);
            echo json_encode(['error' => 'Invalid or expired token']);
            exit;
        }

        // Make payload available globally
        $_REQUEST['auth'] = $payload;
    }
}