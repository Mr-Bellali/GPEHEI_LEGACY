<?php

class AuthController
{
    public function __construct(private AuthService $service) {}

    public function login(array $params): void
    {
        $data  = $this->body();
        $email = $data['email']    ?? null;
        $pass  = $data['password'] ?? null;

        if (!$email || !$pass) {
            $this->json(['error' => 'Email and password are required'], 422);
            return;
        }

        $token = $this->service->login($email, $pass);

        if (!$token) {
            $this->json(['error' => 'Invalid credentials'], 401);
            return;
        }

        $this->json(['token' => $token]);
    }

    private function json(mixed $data, int $status = 200): void
    {
        http_response_code($status);
        header('Content-Type: application/json');
        echo json_encode($data);
    }

    private function body(): array
    {
        return json_decode(file_get_contents('php://input'), true) ?? [];
    }
}