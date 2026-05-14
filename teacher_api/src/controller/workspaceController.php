<?php
class WorkspaceController
{
    public function __construct(private WorkspaceService $service)
    {
    }
    public function index(array $params): void
    {
        $modules = $this->json($this->service->getAllModulesForTeacherId((int) $params['id']));
    }

    // Method to get all groups for a module
    public function getGroupsForModuleById(array $params): void{
        $groups = $this->json($this->service->getAllGroupsForModuleId((int) $params['id']));
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