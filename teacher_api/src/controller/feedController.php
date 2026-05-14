<?php

class FeedController
{
    private FeedService $service;

    public function __construct(FeedService $service)
    {
        $this->service = $service;
    }

    public function index(): void
    {
        $feed = $this->service->getGlobalFeed();
        $this->json($feed);
    }

    public function getComments(array $params): void
    {
        $comments = $this->service->getComments((int) $params['post_id']);
        $this->json($comments);
    }

    public function storeComment(array $params): void
    {
        $data = $this->body();
        $data['teacher_id'] = (int) $params['teacher_id'];
        $data['post_id'] = (int) $data['post_id'];

        if ($this->service->createComment($data)) {
            $this->json(['message' => 'Comment added successfully']);
        } else {
            $this->json(['error' => 'Failed to add comment'], 500);
        }
    }

    public function store(array $params): void
    {
        $data = $this->body();
        $data['teacher_id'] = (int) $params['teacher_id'];

        try {
            $postId = $this->service->createPost($data);

            if (!empty($data['image'])) {
                $link = $this->service->saveBase64Image($data['image']);
                $this->service->addAttachment($postId, $link);
            }

            $this->json(['message' => 'Post created successfully', 'post_id' => $postId]);
        } catch (Exception $e) {
            $this->json(['error' => $e->getMessage()], 400);
        }
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
