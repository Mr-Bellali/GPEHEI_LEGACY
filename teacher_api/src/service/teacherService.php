<?php

class TeacherService
{
    private Database $db;

    public function __construct()
    {
        $this->db = Database::getInstance();
    }

    public function getAll(): array
    {
        $this->db->query('SELECT * FROM teacher');
        return $this->db->resultSet();
    }

    public function getById(int $id): mixed
    {
        $this->db->query('SELECT * FROM teacher WHERE id = :id');
        $this->db->bind(':id', $id);
        return $this->db->single();
    }

    public function create(array $data): mixed
    {
        $this->db->query('INSERT INTO teacher (name, email) VALUES (:name, :email)');
        $this->db->bind(':name',  $data['name']);
        $this->db->bind(':email', $data['email']);
        $this->db->execute();
        return $this->getById((int) Database::getInstance()->lastInsertId());
    }

    public function update(int $id, array $data): mixed
    {
        $this->db->query('UPDATE teacher SET name = :name, email = :email WHERE id = :id');
        $this->db->bind(':name',  $data['name']);
        $this->db->bind(':email', $data['email']);
        $this->db->bind(':id',    $id);
        $this->db->execute();
        return $this->getById($id);
    }

    public function delete(int $id): void
    {
        $this->db->query('DELETE FROM teacher WHERE id = :id');
        $this->db->bind(':id', $id);
        $this->db->execute();
    }
}