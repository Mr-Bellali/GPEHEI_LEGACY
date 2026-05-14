<?php
class WorkspaceController
{
    public function __construct(private WorkspaceService $service)
    {
    }
    public function index(array $params): void
    {
        $modules = $this->service->getAllModulesForTeacherId((int) $params['id']);
        $this->json($modules);
    }

    public function getGroups(array $params): void
    {
        $teacherId = (int) $params['teacher_id'];
        $moduleId = (int) $params['module_id'];
        $groups = $this->service->getGroupsForTeacherModule($teacherId, $moduleId);
        $this->json($groups);
    }

    public function getFlow(array $params): void
    {
        $moduleId = (int) $params['module_id'];
        $groupId = (int) $params['group_id'];
        $flow = $this->service->getFlow($moduleId, $groupId);
        $this->json($flow);
    }

    public function getAssignments(array $params): void
    {
        $moduleId = (int) $params['module_id'];
        $groupId = (int) $params['group_id'];
        $assignments = $this->service->getAssignments($moduleId, $groupId);
        $this->json($assignments);
    }

    public function createPost(array $params): void
    {
        $data = $this->body();
        $data['teacher_id'] = (int) $params['teacher_id'];
        if ($this->service->createPost($data)) {
            $this->json(['message' => 'Post created successfully']);
        } else {
            $this->json(['error' => 'Failed to create post'], 500);
        }
    }

    public function createCourse(array $params): void
    {
        $data = $this->body();
        if ($this->service->createCourse($data)) {
            $this->json(['message' => 'Course created successfully']);
        } else {
            $this->json(['error' => 'Failed to create course'], 500);
        }
    }

    public function createHomework(array $params): void
    {
        $data = $this->body();
        $data['teacher_id'] = (int) $params['teacher_id'];
        if ($this->service->createHomework($data)) {
            $this->json(['message' => 'Homework created successfully']);
        } else {
            $this->json(['error' => 'Failed to create homework'], 500);
        }
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
?>