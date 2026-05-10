<?php

class AuthMiddleware
{
    public static function handle(): void
    {
        $headers = getallheaders();
        $token   = $headers['Authorization'] ?? '';

        if ($token !== 'Bearer mysecrettoken') {
            http_response_code(401);
            echo json_encode(['error' => 'Unauthorized']);
            exit;
        }
    }
}