<?php

class FeedService
{
    private Database $db;

    public function __construct()
    {
        $this->db = Database::getInstance();
    }

    public function getGlobalFeed(): array
    {
        $this->db->query('SELECT p.*, t.first_name as teacher_first, t.last_name as teacher_last, 
                         s.first_name as student_first, s.last_name as student_last,
                         (SELECT COUNT(*) FROM comment c WHERE c.post_id = p.id) as comment_count,
                         (SELECT COUNT(*) FROM vote v WHERE v.post_id = p.id) as vote_count,
                         pa.link as image_link
                         FROM post p
                         LEFT JOIN teacher t ON p.teacher_id = t.id
                         LEFT JOIN student s ON p.student_id = s.id
                         LEFT JOIN post_attachement pa ON pa.post_id = p.id
                         WHERE p.post_status = "active" 
                         AND p.module_id IS NULL 
                         AND p.groupe_id IS NULL
                         ORDER BY p.id DESC');
        $posts = $this->db->resultSet();

        // Convert image links to base64
        foreach ($posts as &$post) {
            $postArray = (array)$post;
            if (!empty($postArray['image_link'])) {
                $filePath = '/var/www/html/public' . $postArray['image_link'];
                if (file_exists($filePath)) {
                    $type = pathinfo($filePath, PATHINFO_EXTENSION);
                    $data = file_get_contents($filePath);
                    if (is_object($post)) {
                        $post->image_base64 = 'data:image/' . $type . ';base64,' . base64_encode($data);
                    } else {
                        $post['image_base64'] = 'data:image/' . $type . ';base64,' . base64_encode($data);
                    }
                } else {
                    if (is_object($post)) {
                        $post->image_base64 = null;
                    } else {
                        $post['image_base64'] = null;
                    }
                }
            } else {
                if (is_object($post)) {
                    $post->image_base64 = null;
                } else {
                    $post['image_base64'] = null;
                }
            }
        }

        return $posts;
    }

    public function createPost(array $data): int
    {
        $this->db->query('INSERT INTO post (content, teacher_id, post_status) 
                         VALUES (:content, :teacher_id, "active")');
        $this->db->bind(':content', $data['content']);
        $this->db->bind(':teacher_id', $data['teacher_id']);
        $this->db->execute();
        return (int) $this->db->lastInsertId();
    }

    public function getComments(int $postId): array
    {
        $this->db->query('SELECT c.*, t.first_name as teacher_first, t.last_name as teacher_last,
                         s.first_name as student_first, s.last_name as student_last
                         FROM comment c
                         LEFT JOIN teacher t ON c.teacher_id = t.id
                         LEFT JOIN student s ON c.student_id = s.id
                         WHERE c.post_id = :post_id
                         ORDER BY c.id ASC');
        $this->db->bind(':post_id', $postId);
        return $this->db->resultSet();
    }

    public function createComment(array $data): bool
    {
        $this->db->query('INSERT INTO comment (content, teacher_id, post_id) 
                         VALUES (:content, :teacher_id, :post_id)');
        $this->db->bind(':content', $data['content']);
        $this->db->bind(':teacher_id', $data['teacher_id']);
        $this->db->bind(':post_id', $data['post_id']);
        return $this->db->execute();
    }

    public function addAttachment(int $postId, string $link): void
    {
        $this->db->query('INSERT INTO post_attachement (post_id, link, attachement_name) 
                         VALUES (:post_id, :link, :name)');
        $this->db->bind(':post_id', $postId);
        $this->db->bind(':link', $link);
        $this->db->bind(':name', 'Image Attachment');
        $this->db->execute();
    }

    public function saveBase64Image(string $base64String): string
    {
        if (empty($base64String)) return '';

        // Extract format and data
        if (preg_match('/^data:image\/(\w+);base64,/', $base64String, $type)) {
            $data = substr($base64String, strpos($base64String, ',') + 1);
            $type = strtolower($type[1]); // jpg, png, gif

            if (!in_array($type, ['jpg', 'jpeg', 'gif', 'png'])) {
                throw new Exception('Invalid image type');
            }

            $data = base64_decode($data);
            if ($data === false) {
                throw new Exception('base64_decode failed');
            }
        } else {
            throw new Exception('Did not match data URI with image data');
        }

        $dir = '/var/www/html/public/uploads/posts/';
        if (!is_dir($dir)) {
            mkdir($dir, 0777, true);
        }

        // Final check to ensure directory exists before writing
        if (!is_dir($dir)) {
            throw new Exception('Upload directory could not be found: ' . $dir);
        }

        $fileName = time() . '_' . md5(uniqid()) . '.' . $type;
        $filePath = $dir . $fileName;

        if (file_put_contents($filePath, $data)) {
            return '/uploads/posts/' . $fileName;
        }

        throw new Exception('Failed to save file');
    }
}
