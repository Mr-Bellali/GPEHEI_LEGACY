<?php

class TeacherController
{
    public function __construct(private TeacherService $service) {}

    public function index(array $params): void
    {
        $this->json($this->service->getAll());
    }

    public function show(array $params): void
    {
        $teacher = $this->service->getById((int) $params['id']);

        if (!$teacher) {
            $this->json(['error' => 'Teacher not found'], 404);
            return;
        }

        $this->json($teacher);
    }

    public function store(array $params): void
    {
        $data    = $this->body();
        $teacher = $this->service->create($data);
        $this->json($teacher, 201);
    }

    public function update(array $params): void
    {
        $data    = $this->body();
        $teacher = $this->service->update((int) $params['id'], $data);
        $this->json($teacher);
    }

    public function destroy(array $params): void
    {
        $this->service->delete((int) $params['id']);
        $this->json(['message' => 'Deleted successfully']);
    }

    // ── Helpers ──────────────────────────────────────────────
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