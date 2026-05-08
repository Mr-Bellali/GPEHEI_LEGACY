<?php
class WorkspaceController
{
    private $workspaceModel;

    public function __construct()
    {
        $this->workspaceModel = new WorkspaceModel();
    }

    // This method will render the workspace view
    public function index()
    {
        // Check if user is logged in
        if (!isset($_SESSION['user_id'])) {
            http_response_code(401);
            echo "Unauthorized";
            return;
        }

        $teacherId = $_SESSION['user_id'];

        // Get filiere_id from request (if selected)
        $selectedFiliereId = isset($_GET['filiere_id']) ? $_GET['filiere_id'] : null;

        // Get teacher's filieres
        $teacherFilieres = $this->workspaceModel->getTeacherFilieres($teacherId);

        // Get all filieres
        $allFilieres = $this->workspaceModel->getAllFilieres();

        // Create combined list with "All Filieres" option
        $filieres = [];

        // Add "All Filieres" option if teacher has access to any
        if (!empty($teacherFilieres)) {
            $allOption = new stdClass();
            $allOption->id = 'all';
            $allOption->name_filier = 'All Filieres';
            $allOption->short_name = 'ALL';
            $filieres[] = $allOption;
        }

        // Add teacher's filieres
        foreach ($teacherFilieres as $filiere) {
            $filieres[] = $filiere;
        }

        // If no filiere selected and we have filieres, use 'all' or first one
        if ($selectedFiliereId === null && !empty($filieres)) {
            $selectedFiliereId = $filieres[0]->id;
        }

        // Get selected filiere details
        $selectedFiliere = null;
        foreach ($filieres as $filiere) {
            if ($filiere->id == $selectedFiliereId) {
                $selectedFiliere = $filiere;
                break;
            }
        }

        // Get all academic levels (1-5)
        $academicLevels = $this->workspaceModel->getAllAcademicLevels();

        // Initialize groups by level with default values
        $groupsByLevel = [];
        for ($i = 1; $i <= 5; $i++) {
            $groupsByLevel[$i] = [
                'name' => $academicLevels[$i],
                'groups' => [],
                'total_students' => 0,
                'total_groups' => 0
            ];
        }

        $latestPromotion = null;

        // If a filiere is selected, get its data
        if ($selectedFiliereId) {
            if ($selectedFiliereId === 'all') {
                // Show all filieres combined
                foreach ($teacherFilieres as $filiere) {
                    $promotion = $this->workspaceModel->getLatestPromotion($filiere->id);
                    if ($promotion) {
                        $groups = $this->workspaceModel->getGroupsByFiliereAndPromotion($filiere->id, $promotion);

                        foreach ($groups as $group) {
                            $level = (int) $group->year_academic_level;
                            if ($level >= 1 && $level <= 5) {
                                // Add filiere info to group
                                $group->filiere_name = $filiere->short_name;
                                $groupsByLevel[$level]['groups'][] = $group;
                                $groupsByLevel[$level]['total_students'] += (int) $group->student_count;
                                $groupsByLevel[$level]['total_groups']++;
                            }
                        }
                    }
                }
                $latestPromotion = 'All Promotions';
            } else {
                // Get the latest promotion for the selected filiere
                $latestPromotion = $this->workspaceModel->getLatestPromotion((int) $selectedFiliereId);

                if ($latestPromotion) {
                    // Get groups for this filiere and promotion
                    $groups = $this->workspaceModel->getGroupsByFiliereAndPromotion((int) $selectedFiliereId, $latestPromotion);

                    // Organize groups by academic level
                    foreach ($groups as $group) {
                        $level = (int) $group->year_academic_level;
                        if ($level >= 1 && $level <= 5) {
                            $groupsByLevel[$level]['groups'][] = $group;
                            $groupsByLevel[$level]['total_students'] += (int) $group->student_count;
                            $groupsByLevel[$level]['total_groups']++;
                        }
                    }
                }
            }
        }

        // Prepare data array
        $data = [
            'filieres' => $filieres,
            'selectedFiliereId' => $selectedFiliereId,
            'selectedFiliere' => $selectedFiliere,
            'teacherFilieres' => $teacherFilieres,
            'academicLevels' => $academicLevels,
            'groupsByLevel' => $groupsByLevel,
            'latestPromotion' => $latestPromotion
        ];

        // Extract variables to make them available in the view
        extract($data);

        // Include the view
        include __DIR__ . '/../views/pages/workspace.php';
    }
}
?>