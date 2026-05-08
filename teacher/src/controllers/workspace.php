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
        $selectedFiliereId = isset($_GET['filiere_id']) ? (int)$_GET['filiere_id'] : null;
        
        // Get all filieres for this teacher
        $filieres = $this->workspaceModel->getTeacherFilieres($teacherId);
        
        // If no filiere selected and we have filieres, use the first one
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
            // Get the latest promotion for the selected filiere
            $latestPromotion = $this->workspaceModel->getLatestPromotion($selectedFiliereId);
            
            if ($latestPromotion) {
                // Get groups for this filiere and promotion
                $groups = $this->workspaceModel->getGroupsByFiliereAndPromotion($selectedFiliereId, $latestPromotion);
                
                // Organize groups by academic level
                foreach ($groups as $group) {
                    $level = (int)$group->year_academic_level;
                    if ($level >= 1 && $level <= 5) {
                        $groupsByLevel[$level]['groups'][] = $group;
                        $groupsByLevel[$level]['total_students'] += (int)$group->student_count;
                        $groupsByLevel[$level]['total_groups']++;
                    }
                }
            }
        }
        
        // Prepare data array
        $data = [
            'filieres' => $filieres,
            'selectedFiliereId' => $selectedFiliereId,
            'selectedFiliere' => $selectedFiliere,
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