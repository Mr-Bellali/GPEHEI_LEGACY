<?php
// Define the class of authentication (it will have login and change password for sure, and maybe reset password)
class AuthModel
{
    private $db;

    public function __construct()
    {
        $this->db = new Database();
    }

    public function findUserByEmail($email)
    {
        $this->db->query('SELECT * FROM teacher WHERE email = :email');
        $this->db->bind(':email', $email);
        return $this->db->single();
    }

    public function login($email, $password)
    {
        $user = $this->findUserByEmail($email);
        error_log("user email from db: $user->email, and his password: $user->hashed_password");
        if (!$user)
            return false;

        $hashed = hash('sha256', $password);
        if ($hashed !== $user->hashed_password)
            return false;

        return $user;
    }
}
?>