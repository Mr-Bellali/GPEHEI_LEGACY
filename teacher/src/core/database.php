<?php
// Define a class named Database
class Database
{
    private $host = '127.0.0.1';
    private $user = 'root';
    private $pass = '12345678';
    private $dbname = 'EHEI_DB';

    // Will be the. PDO object
    private $dbh;
    private $stmt;
    private $error;

    public function __construct()
    {
        $this->host   = getenv('DB_HOST') ?: '127.0.0.1';
        $this->user   = getenv('DB_USER') ?: 'root';
        $this->pass   = getenv('DB_PASSWORD') ?: '12345678';
        $this->dbname = getenv('DB_NAME') ?: 'EHEI_DB';
        
        // Set DSN
        $dsn = 'mysql:host=' . $this->host . ';dbname=' . $this->dbname;
        $options = array(
            PDO::ATTR_PERSISTENT => true, // check if dpo already existing
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION // error handling in pdo
        );

        // Create PDO instance 
        try {
            $this->dbh = new PDO($dsn, $this->user, $this->pass, $options);
        } catch (PDOException $e) {
            $this->error = $e->getMessage();
            echo $this->error;
        }

    }
    // Prepare statement with query
    public function query($sql)
    {
        $this->stmt = $this->dbh->prepare($sql);
    }

    //  Bind values to prepared statements using named parameters
    public function bind($param, $value, $type = null)
    {
        if (is_null($type)) {
            switch (true) {
                case is_int($value):
                    $type = PDO::PARAM_INT;
                    break;
                case is_bool($value):
                    $type = PDO::PARAM_BOOL;
                    break;
                case is_null($value):
                    $type = PDO::PARAM_NULL;
                    break;

                default:
                    $type = PDO::PARAM_STR;
            }
        }
        $this->stmt->bindValue($param, $value, $type);
    }

    // Execute the prepared statement
    public function execute()
    {
        return $this->stmt->execute();
    }

    // Return multiple records
    public function resultSet()
    {
        $this->execute();
        return $this->stmt->fetchAll(PDO::FETCH_OBJ);
    }

    // Return a single record
    public function single()
    {
        $this->execute();
        return $this->stmt->fetch(PDO::FETCH_OBJ);
    }

    // Get row count
    public function rowCount()
    {
        return $this->stmt->rowCount();
    }
}
?>