<?php

define('DB_HOST', getenv('DB_HOST') ?: 'mysql');
define('DB_USER', getenv('DB_USER') ?: 'root');
define('DB_PASS', getenv('DB_PASSWORD') ?: 'my-secret-pw');
define('DB_NAME', getenv('DB_NAME') ?: 'my_database');
define('DB_PORT', getenv('DB_PORT') ?: '3306');
define('BASE_URL', 'http://localhost:9000/');

?>