<?php
class AuthController
{
    private $authModel;
    public function __construct()
    {
        $this->authModel = new AuthModel();
    }

    public function login()
    {
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            // If it's not a request, just show login page
            include 'views/templates/auth_template.php';
            return;
        }

        // Sanitize inputs
        $email = filter_var(trim($_POST['email']), FILTER_SANITIZE_EMAIL);
        $password = trim($_POST['password']);
        $remember = isset($_POST['remember']);

        // Validation
        if (empty($email) || empty($password)) {
            $_SESSION['error'] = 'Please fill in all fields';
            header('Location: index.php');
            exit;
        }

        $user = $this->authModel->login($email, $password);

        if (!$user) {
            error_log("email: ,$email");
            error_log("password: ,$password");
            $_SESSION['error'] = 'Invalid email or password';
            header('Location: index.php');
            exit;
        }

        // Success
        $_SESSION['user_id'] = $user->id;
        $_SESSION['user_name'] = $user->first_name . ' ' . $user->last_name;
        $_SESSION['user_email'] = $user->email;

        // Handle "Remember me" with a cookie (7 days)
        if ($remember) {
            $token = bin2hex(random_bytes(32));
            setcookie('remember_token', $token, time() + (7 * 24 * 60 * 60), '/', '', true, true);
        }

        header('Location: /dashboard');
        exit;
    }

    public function logout(){
        session_destroy();
        setcookie('remember_token', '', time() - 3600, '/');
        header('Location: index.php');
        exit;
    }
}


?>