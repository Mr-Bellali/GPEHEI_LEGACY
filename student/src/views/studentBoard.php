<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Classroom Dashboard</title>
    
</head>
<body>
    <div class="container">
        <nav class="navbar">
            <h1>Classroom Dashboard</h1>
            <ul class="nav-links">
                <li><a href="/dashboard">flow</a></li>
                <li><a href="/assignments">Courses & assignments</a></li>
            </ul>
        </nav>

        <!-- Debug Section -->
        <div style="background: #eee; padding: 10px; margin: 10px 0; border: 1px solid #ccc;">
            <h3>Debug Info:</h3>
            <p><strong>Logged User ID:</strong> <?= $_SESSION['userId'] ?? 'None' ?></p>
            <p><strong>Selected Module:</strong> <?= $selectedModuleId ?? 'None' ?></p>
        </div>

        <div class="modules-list">
            <h3>Available Modules</h3>
            <?php if (!empty($modules)): ?>
                <?php foreach ($modules as $module): ?>
                    <div class="module-card" style="border: 1px solid #ddd; padding: 10px; margin-bottom: 5px;">
                        <h4><?= htmlspecialchars($module->moduleName) ?></h4>
                        <p>Module ID: <?= htmlspecialchars($module->id) ?></p>
                        <a href="?module_id=<?= $module->id ?>">Select Module</a>
                    </div>
                <?php endforeach; ?>
            <?php else: ?>
                <p>No modules found for your group.</p>
            <?php endif; ?>
        </div>

        <div class="content-section">
            <h3>Courses in Selected Module</h3>
            <?php if (!empty($courses)): ?>
                <ul>
                    <?php foreach ($courses as $course): ?>
                        <li><?= htmlspecialchars($course->course_name) ?></li>
                    <?php endforeach; ?>
                </ul>
            <?php else: ?>
                <p>Select a module to see courses.</p>
            <?php endif; ?>

            <h3>Homeworks</h3>
            <?php if (!empty($homeworks)): ?>
                <pre><?php print_r($homeworks); ?></pre>
            <?php else: ?>
                <p>No homeworks found for this module.</p>
            <?php endif; ?>
        </div>
    </div>

</body>
</html>
