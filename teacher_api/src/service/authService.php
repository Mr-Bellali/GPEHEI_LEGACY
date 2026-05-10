<?php

class AuthService
{
    private Database $db;

    public function __construct()
    {
        $this->db = Database::getInstance();
    }

    public function login(string $email, string $password): ?string
    {
        $this->db->query('SELECT * FROM teacher WHERE email = :email');
        $this->db->bind(':email', $email);
        $user = $this->db->single();

        if (!$user) return null;
        $hashed = hash('sha256', $password);
        if ($hashed !== $user->hashed_password)
            return null;

        // Generate JWT valid for 24 hours
        $token = JWT::generate([
            'sub'   => $user->id,
            'email' => $user->email,
            'iat'   => time(),
            'exp'   => time() + 86400,
        ]);

        return $token;
    }

}