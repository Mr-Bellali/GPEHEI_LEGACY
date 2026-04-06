<?php
// ============================================================
//  HAPPY PATH — Live test runner (Jest/Vitest style)
//  Connects to MySQL via env vars set in docker-compose
// ============================================================

// ── AJAX: return table rows as JSON ─────────────────────────
if (isset($_GET['view_table'])) {
    $h  = getenv('DB_HOST') ?: 'mysql';
    $po = getenv('DB_PORT') ?: 3306;
    $db = getenv('DB_NAME') ?: 'my_database';
    $u  = getenv('DB_USER') ?: 'root';
    $pw = getenv('DB_PASSWORD') ?: 'my-secret-pw';
    header('Content-Type: application/json');
    try {
        $p = new PDO("mysql:host=$h;port=$po;dbname=$db;charset=utf8mb4", $u, $pw, [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        ]);
        $tbl = preg_replace('/[^a-zA-Z0-9_]/', '', $_GET['view_table']);
        $rows = $p->query("SELECT * FROM `$tbl` LIMIT 100")->fetchAll();
        echo json_encode(['ok' => true, 'rows' => $rows]);
    } catch (Throwable $e) {
        echo json_encode(['ok' => false, 'error' => $e->getMessage()]);
    }
    exit;
}

$host     = getenv('DB_HOST')     ?: 'mysql';
$port     = getenv('DB_PORT')     ?: 3306;
$dbname   = getenv('DB_NAME')     ?: 'my_database';
$user     = getenv('DB_USER')     ?: 'root';
$password = getenv('DB_PASSWORD') ?: 'my-secret-pw';

// ── DSN ──────────────────────────────────────────────────────
$dsn = "mysql:host=$host;port=$port;dbname=$dbname;charset=utf8mb4";

// ── Test state ───────────────────────────────────────────────
$results  = [];   // ['label', 'status', 'detail', 'rows', 'duration']
$insertedIds = [];

// ── Helpers ──────────────────────────────────────────────────
function run(string $label, callable $fn, array &$results): void {
    $t0 = microtime(true);
    try {
        $detail = $fn();
        $ms = round((microtime(true) - $t0) * 1000, 2);
        $results[] = ['label' => $label, 'status' => 'pass', 'detail' => $detail, 'ms' => $ms];
    } catch (Throwable $e) {
        $ms = round((microtime(true) - $t0) * 1000, 2);
        $results[] = ['label' => $label, 'status' => 'fail', 'detail' => $e->getMessage(), 'ms' => $ms];
    }
}

// ── Connect ──────────────────────────────────────────────────
try {
    $pdo = new PDO($dsn, $user, $password, [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    ]);
} catch (PDOException $e) {
    renderFatal($e->getMessage());
    exit;
}

// ════════════════════════════════════════════════════════════
//  T E S T   S U I T E S
// ════════════════════════════════════════════════════════════

// ── 1. DB CONNECTION ────────────────────────────────────────
run('📡  Connect to MySQL', function() use ($pdo) {
    $row = $pdo->query("SELECT VERSION() AS v")->fetch();
    return "MySQL " . $row['v'];
}, $results);

// ── 2. TABLES EXIST ─────────────────────────────────────────
$expectedTables = [
    'admin', 'teacher', 'student', 'filiere', 'module', 'teacher_module',
    'groupe', 'groupeStudent', 'projectSettings', 'project', 'projectStudent',
    'projectSupervisor', 'projectSubmissionAtachement', 'conversation',
    'conversation_participant', 'course', 'homework', 'homeworkAtachement',
    'submission', 'submissionAtachement', 'library', 'documents', 'post',
    'post_attachement', 'comment', 'vote', 'message'
];

foreach ($expectedTables as $tbl) {
    run("🗄️  Table exists › $tbl", function() use ($pdo, $tbl, $dbname) {
        $stmt = $pdo->prepare(
            "SELECT TABLE_NAME FROM information_schema.TABLES
             WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?"
        );
        $stmt->execute([$dbname, $tbl]);
        if (!$stmt->fetch()) throw new \Exception("Table '$tbl' not found");
        return "✓ exists";
    }, $results);
}

// ── 3. INSERT SEED DATA ─────────────────────────────────────
run('➕  INSERT admin', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "INSERT INTO admin (first_name, last_name, email, hashed_password, admin_role, phone)
         VALUES (?, ?, ?, ?, ?, ?)"
    );
    $stmt->execute(['Alice', 'Admin', 'alice@school.ma', password_hash('secret', PASSWORD_BCRYPT), 'super', '0612345678']);
    $insertedIds['admin_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['admin_id'];
}, $results);

run('➕  INSERT teacher', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "INSERT INTO teacher (teacher_number, first_name, last_name, email, hashed_password, phone, cin)
         VALUES (?, ?, ?, ?, ?, ?, ?)"
    );
    $stmt->execute([1001, 'Bob', 'Teacher', 'bob@school.ma', password_hash('pass', PASSWORD_BCRYPT), '0611111111', 'AB123456']);
    $insertedIds['teacher_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['teacher_id'];
}, $results);

run('➕  INSERT student', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "INSERT INTO student (student_number, first_name, last_name, cin, cne, email, hashed_password, phone, birth_date)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
    );
    $stmt->execute([2001, 'Charlie', 'Student', 'CD789012', 'CNE001', 'charlie@school.ma',
                    password_hash('pass', PASSWORD_BCRYPT), '0622222222', '2002-05-14']);
    $insertedIds['student_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['student_id'];
}, $results);

run('➕  INSERT filiere', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("INSERT INTO filiere (name_filier, short_name) VALUES (?, ?)");
    $stmt->execute(['Computer Science', 'CS']);
    $insertedIds['filiere_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['filiere_id'];
}, $results);

run('➕  INSERT module (parent)', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "INSERT INTO module (module_name, type_module, parent_module_id, filiere_id, semester)
         VALUES (?, ?, ?, ?, ?)"
    );
    $stmt->execute(['Web Development', 'mod', NULL, $insertedIds['filiere_id'], 3]);
    $insertedIds['module_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['module_id'];
}, $results);

run('➕  INSERT module (child element)', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "INSERT INTO module (module_name, type_module, parent_module_id, filiere_id, semester)
         VALUES (?, ?, ?, ?, ?)"
    );
    $stmt->execute(['PHP & MySQL', 'elm', $insertedIds['module_id'], $insertedIds['filiere_id'], 3]);
    $insertedIds['element_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['element_id'];
}, $results);

run('➕  INSERT teacher_module (pivot)', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("INSERT INTO teacher_module (teacher_id, module_id) VALUES (?, ?)");
    $stmt->execute([$insertedIds['teacher_id'], $insertedIds['module_id']]);
    return "teacher=" . $insertedIds['teacher_id'] . " ↔ module=" . $insertedIds['module_id'];
}, $results);

run('➕  INSERT groupe', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("INSERT INTO groupe (group_name, promotion, filiere_id) VALUES (?, ?, ?)");
    $stmt->execute(['A', '2024-2025', $insertedIds['filiere_id']]);
    $insertedIds['group_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['group_id'];
}, $results);

run('➕  INSERT groupeStudent', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("INSERT INTO groupeStudent (groupe_id, promotion, student_id) VALUES (?, ?, ?)");
    $stmt->execute([$insertedIds['group_id'], '2024-2025', $insertedIds['student_id']]);
    return "student added to group";
}, $results);

run('➕  INSERT projectSettings', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "INSERT INTO projectSettings (max_capacity, min_capacity, filiere_id, promotion, deadline)
         VALUES (?, ?, ?, ?, ?)"
    );
    $stmt->execute([5, 2, $insertedIds['filiere_id'], '2024-2025', '2025-06-30 23:59:59']);
    $insertedIds['ps_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['ps_id'];
}, $results);

run('➕  INSERT project', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "INSERT INTO project (project_name, project_title, project_description, project_type, project_settings_id)
         VALUES (?, ?, ?, ?, ?)"
    );
    $stmt->execute(['E-learning Platform', 'E-learning Platform', 'Build a full LMS', 'projet_fin_études', $insertedIds['ps_id']]);
    $insertedIds['project_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['project_id'];
}, $results);

run('➕  INSERT projectStudent', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("INSERT INTO projectStudent (student_id, project_id) VALUES (?, ?)");
    $stmt->execute([$insertedIds['student_id'], $insertedIds['project_id']]);
    return "student assigned to project";
}, $results);

run('➕  INSERT projectSupervisor', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("INSERT INTO projectSupervisor (teacher_id, project_id) VALUES (?, ?)");
    $stmt->execute([$insertedIds['teacher_id'], $insertedIds['project_id']]);
    return "teacher supervises project";
}, $results);

run('➕  INSERT course', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("INSERT INTO course (course_name, content, module_id) VALUES (?, ?, ?)");
    $stmt->execute(['Intro to PDO', 'Learn PDO step by step...', $insertedIds['element_id']]);
    $insertedIds['course_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['course_id'];
}, $results);

run('➕  INSERT homework', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "INSERT INTO homework (title, content, deadline, teacher_id, groupe_id, module_id)
         VALUES (?, ?, ?, ?, ?, ?)"
    );
    $stmt->execute(['TP PDO', 'Build a CRUD app', '2025-05-01 23:59:59',
                    $insertedIds['teacher_id'], $insertedIds['group_id'], $insertedIds['element_id']]);
    $insertedIds['hw_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['hw_id'];
}, $results);

run('➕  INSERT submission', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "INSERT INTO submission (title, content, submission_status, homework_id, student_id)
         VALUES (?, ?, ?, ?, ?)"
    );
    $stmt->execute(['My PDO App', 'See attached ZIP', 'submitted', $insertedIds['hw_id'], $insertedIds['student_id']]);
    $insertedIds['sub_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['sub_id'];
}, $results);

// Fix: INSERT conversation (for pair, need both creator AND recipient)
run('➕  INSERT conversation', function() use ($pdo, &$insertedIds) {
    // For pair conversation, we need a recipient (another student or teacher)
    // Create a second student as recipient
    $stmt = $pdo->prepare(
        "INSERT INTO student (student_number, first_name, last_name, cin, cne, email, hashed_password, phone, birth_date)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
    );
    $stmt->execute([2002, 'Diana', 'Student2', 'CD789013', 'CNE002', 'diana@school.ma',
                    password_hash('pass', PASSWORD_BCRYPT), '0633333333', '2002-06-15']);
    $insertedIds['recipient_student_id'] = (int)$pdo->lastInsertId();
    
    // Now create pair conversation between student and recipient
    $stmt2 = $pdo->prepare(
        "INSERT INTO conversation (createdby_student_id, totexting_student_id, type_conv, conversation_name)
         VALUES (?, ?, ?, ?)"
    );
    $stmt2->execute([$insertedIds['student_id'], $insertedIds['recipient_student_id'], 'pair', 'Student Chat']);
    $insertedIds['conv_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['conv_id'] . " (pair conversation between student and recipient)";
}, $results);

// Fix: INSERT conversation_participant (add BOTH participants)
run('➕  INSERT conversation_participant', function() use ($pdo, &$insertedIds) {
    // Add creator as participant
    $stmt = $pdo->prepare("INSERT INTO conversation_participant (conversation_id, student_id) VALUES (?, ?)");
    $stmt->execute([$insertedIds['conv_id'], $insertedIds['student_id']]);
    
    // Add recipient as participant
    $stmt2 = $pdo->prepare("INSERT INTO conversation_participant (conversation_id, student_id) VALUES (?, ?)");
    $stmt2->execute([$insertedIds['conv_id'], $insertedIds['recipient_student_id']]);
    
    return "both participants added to conversation";
}, $results);

// Fix: INSERT message (use existing conversation_id)
run('➕  INSERT message', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "INSERT INTO message (content, message_type, message_status, conversation_id, sender_student_id)
         VALUES (?, ?, ?, ?, ?)"
    );
    $stmt->execute(['Hello everyone!', 'text', 'normal', $insertedIds['conv_id'], $insertedIds['student_id']]);
    $insertedIds['msg_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['msg_id'];
}, $results);


run('➕  INSERT post', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("INSERT INTO post (title, content, teacher_id) VALUES (?, ?, ?)");
    $stmt->execute(['Welcome!', 'Welcome to the semester.', $insertedIds['teacher_id']]);
    $insertedIds['post_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['post_id'];
}, $results);

run('➕  INSERT comment', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("INSERT INTO comment (content, student_id, post_id) VALUES (?, ?, ?)");
    $stmt->execute(['Thanks, excited!', $insertedIds['student_id'], $insertedIds['post_id']]);
    $insertedIds['comment_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['comment_id'];
}, $results);

run('➕  INSERT vote', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("INSERT INTO vote (student_id, post_id) VALUES (?, ?)");
    $stmt->execute([$insertedIds['student_id'], $insertedIds['post_id']]);
    return "student voted on post";
}, $results);

run('➕  INSERT library', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("INSERT INTO library (library_name) VALUES (?)");
    $stmt->execute(['Main Library']);
    $insertedIds['lib_id'] = (int)$pdo->lastInsertId();
    return "id=" . $insertedIds['lib_id'];
}, $results);

// ── 4. READ BACK ─────────────────────────────────────────────
run('🔍  SELECT admin by email', function() use ($pdo) {
    $stmt = $pdo->prepare("SELECT id, first_name, last_name, admin_role FROM admin WHERE email = ?");
    $stmt->execute(['alice@school.ma']);
    $r = $stmt->fetch();
    if (!$r) throw new \Exception("Admin not found");
    return json_encode($r);
}, $results);

run('🔍  SELECT student with group', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "SELECT s.first_name, s.last_name, g.group_name, g.promotion
         FROM student s
         JOIN groupeStudent gs ON gs.student_id = s.id
         JOIN groupe g ON g.id = gs.groupe_id
         WHERE s.id = ?"
    );
    $stmt->execute([$insertedIds['student_id']]);
    $r = $stmt->fetch();
    if (!$r) throw new \Exception("Student not found");
    return json_encode($r);
}, $results);

run('🔍  SELECT teacher → modules (pivot)', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "SELECT t.first_name, m.module_name
         FROM teacher_module tm
         JOIN teacher t ON t.id = tm.teacher_id
         JOIN module m ON m.id = tm.module_id
         WHERE tm.teacher_id = ?"
    );
    $stmt->execute([$insertedIds['teacher_id']]);
    $rows = $stmt->fetchAll();
    return count($rows) . " module(s): " . implode(', ', array_column($rows, 'module_name'));
}, $results);

run('🔍  SELECT module hierarchy', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "SELECT m1.module_name AS parent, m2.module_name AS child
         FROM module m1
         JOIN module m2 ON m2.parent_module_id = m1.id
         WHERE m1.id = ?"
    );
    $stmt->execute([$insertedIds['module_id']]);
    $rows = $stmt->fetchAll();
    return count($rows) . " child element(s)";
}, $results);

run('🔍  SELECT project with supervisor', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "SELECT p.project_name, t.first_name, t.last_name
         FROM project p
         JOIN projectSupervisor ps ON ps.project_id = p.id
         JOIN teacher t ON t.id = ps.teacher_id
         WHERE p.id = ?"
    );
    $stmt->execute([$insertedIds['project_id']]);
    $r = $stmt->fetch();
    if (!$r) throw new \Exception("Project/supervisor not found");
    return json_encode($r);
}, $results);

run('🔍  SELECT conversation with message', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "SELECT m.content, m.message_type, s.first_name AS sender
         FROM message m
         JOIN student s ON s.id = m.sender_student_id
         WHERE m.conversation_id = ?"
    );
    $stmt->execute([$insertedIds['conv_id']]);
    $rows = $stmt->fetchAll();
    return count($rows) . " message(s)";
}, $results);

// ── 5. UPDATE ────────────────────────────────────────────────
run('✏️  UPDATE student phone', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("UPDATE student SET phone = ? WHERE id = ?");
    $stmt->execute(['0699999999', $insertedIds['student_id']]);
    $stmt2 = $pdo->prepare("SELECT phone FROM student WHERE id = ?");
    $stmt2->execute([$insertedIds['student_id']]);
    $r = $stmt2->fetch();
    if ($r['phone'] !== '0699999999') throw new \Exception("Update failed");
    return "phone → " . $r['phone'];
}, $results);

run('✏️  UPDATE message status to modified', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("UPDATE message SET message_status = 'modified', original_content = content, content = ? WHERE id = ?");
    $stmt->execute(['Hello everyone! (edited)', $insertedIds['msg_id']]);
    return "message updated";
}, $results);

// ── 6. SOFT DELETE ───────────────────────────────────────────
run('🗑️  Soft-delete course (course_status=disabled)', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare("UPDATE course SET course_status = 'disabled' WHERE id = ?");
    $stmt->execute([$insertedIds['course_id']]);
    $stmt2 = $pdo->prepare("SELECT course_status FROM course WHERE id = ?");
    $stmt2->execute([$insertedIds['course_id']]);
    $r = $stmt2->fetch();
    return "status → " . $r['course_status'];
}, $results);

// ── 7. CONSTRAINT VIOLATION ──────────────────────────────────
run('🚧  Constraint: duplicate teacher email → must fail', function() use ($pdo) {
    $stmt = $pdo->prepare(
        "INSERT INTO teacher (teacher_number, first_name, last_name, email, hashed_password, phone, cin)
         VALUES (?, ?, ?, ?, ?, ?, ?)"
    );
    try {
        $stmt->execute([9999, 'Dupe', 'Teacher', 'bob@school.ma', 'x', '0611111111', 'XX999999']);
        throw new \Exception("Should have thrown a duplicate key error");
    } catch (\PDOException $e) {
        if (strpos($e->getMessage(), 'Duplicate') !== false || strpos($e->getMessage(), '1062') !== false) {
            return "✓ correctly rejected duplicate email";
        }
        throw $e;
    }
}, $results);

run('🚧  Constraint: FK violation → must fail', function() use ($pdo) {
    $stmt = $pdo->prepare("INSERT INTO module (module_name, type_module, parent_module_id, filiere_id, semester) VALUES (?, ?, ?, ?, ?)");
    try {
        $stmt->execute(['Ghost Module', 'mod', NULL, 999999, 1]);
        throw new \Exception("Should have thrown FK constraint error");
    } catch (\PDOException $e) {
        if (strpos($e->getMessage(), 'foreign key') !== false || strpos($e->getMessage(), '1452') !== false) {
            return "✓ correctly rejected invalid FK";
        }
        throw $e;
    }
}, $results);

run('🚧  Constraint: module hierarchy (elm without parent) → must fail', function() use ($pdo, &$insertedIds) {
    $stmt = $pdo->prepare(
        "INSERT INTO module (module_name, type_module, parent_module_id, filiere_id, semester)
         VALUES (?, ?, ?, ?, ?)"
    );
    try {
        $stmt->execute(['Orphan Element', 'elm', NULL, $insertedIds['filiere_id'], 3]);
        throw new \Exception("Should have thrown CHECK constraint error");
    } catch (\PDOException $e) {
        return "✓ correctly rejected elm without parent";
    }
}, $results);

// ── 8. TABLE SNAPSHOT ────────────────────────────────────────
$snapshots = [];
foreach ($expectedTables as $tbl) {
    try {
        $count = $pdo->query("SELECT COUNT(*) FROM `$tbl`")->fetchColumn();
        $cols  = $pdo->query("SHOW COLUMNS FROM `$tbl`")->fetchAll();
        $snapshots[$tbl] = ['count' => (int)$count, 'columns' => array_column($cols, 'Field')];
    } catch (\Throwable $e) {
        $snapshots[$tbl] = ['count' => '?', 'columns' => []];
    }
}

// ═══════════════════════════════════════════════════════════
//  R E N D E R
// ═══════════════════════════════════════════════════════════
$pass  = count(array_filter($results, fn($r) => $r['status'] === 'pass'));
$fail  = count(array_filter($results, fn($r) => $r['status'] === 'fail'));
$total = count($results);

function statusIcon(string $s): string {
    return $s === 'pass' ? '●' : '✕';
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Happy Path — DB Test Runner</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600;700&family=Syne:wght@400;700;800&display=swap" rel="stylesheet">
<style>
  :root {
    --bg:        #0b0d12;
    --surface:   #12151e;
    --border:    #1e2333;
    --pass:      #22d97a;
    --fail:      #ff4d6a;
    --warn:      #f5a623;
    --text:      #c9d1e0;
    --muted:     #52596e;
    --accent:    #5e6ad2;
    --accent2:   #a78bfa;
    --mono:      'JetBrains Mono', monospace;
    --sans:      'Syne', sans-serif;
  }

  *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

  body {
    background: var(--bg);
    color: var(--text);
    font-family: var(--mono);
    font-size: 13px;
    line-height: 1.6;
    min-height: 100vh;
  }

  body::before {
    content: '';
    position: fixed; inset: 0;
    background: repeating-linear-gradient(
      0deg,
      transparent,
      transparent 2px,
      rgba(0,0,0,.08) 2px,
      rgba(0,0,0,.08) 4px
    );
    pointer-events: none;
    z-index: 100;
  }

  .page { max-width: 960px; margin: 0 auto; padding: 48px 24px 80px; }

  header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 24px;
    margin-bottom: 40px;
    border-bottom: 1px solid var(--border);
    padding-bottom: 24px;
  }
  .logo {
    font-family: var(--sans);
    font-size: 26px;
    font-weight: 800;
    letter-spacing: -.5px;
    line-height: 1;
  }
  .logo span { color: var(--accent2); }
  .tagline { color: var(--muted); font-size: 11px; margin-top: 4px; letter-spacing: .08em; }

  .summary-badges {
    display: flex; gap: 12px; align-items: center; flex-wrap: wrap;
  }
  .badge {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 14px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: .04em;
    border: 1px solid;
  }
  .badge-pass { color: var(--pass); border-color: rgba(34,217,122,.25); background: rgba(34,217,122,.06); }
  .badge-fail { color: var(--fail); border-color: rgba(255,77,106,.25); background: rgba(255,77,106,.06); }
  .badge-total { color: var(--accent); border-color: rgba(94,106,210,.25); background: rgba(94,106,210,.06); }

  .progress-wrap { margin-bottom: 36px; }
  .progress-label {
    display: flex; justify-content: space-between;
    font-size: 11px; color: var(--muted); margin-bottom: 6px;
  }
  .progress-bar {
    height: 4px; background: var(--border); border-radius: 2px; overflow: hidden;
  }
  .progress-fill {
    height: 100%;
    background: linear-gradient(90deg, var(--pass), var(--accent2));
    border-radius: 2px;
    transition: width .4s ease;
  }

  .suite-label {
    font-family: var(--sans);
    font-size: 11px;
    font-weight: 700;
    letter-spacing: .12em;
    text-transform: uppercase;
    color: var(--muted);
    margin: 32px 0 10px;
    padding-left: 8px;
    border-left: 2px solid var(--accent);
  }

  .test-row {
    display: grid;
    grid-template-columns: 18px 1fr auto auto;
    align-items: center;
    gap: 10px;
    padding: 10px 14px;
    border-radius: 6px;
    margin-bottom: 3px;
    border: 1px solid transparent;
    transition: background .15s;
    cursor: default;
  }
  .test-row:hover { background: var(--surface); border-color: var(--border); }

  .test-dot { font-size: 9px; line-height: 1; }
  .dot-pass { color: var(--pass); }
  .dot-fail { color: var(--fail); }

  .test-label { color: var(--text); font-size: 12.5px; }
  .test-detail {
    font-size: 11px;
    color: var(--muted);
    max-width: 320px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .test-detail.fail-detail { color: var(--fail); }

  .test-ms {
    font-size: 10px;
    color: var(--muted);
    white-space: nowrap;
    text-align: right;
    min-width: 44px;
  }

  .snapshot-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 10px;
    margin-top: 12px;
  }
  .snapshot-card {
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 8px;
    padding: 12px 14px;
    transition: border-color .15s, box-shadow .15s;
    cursor: pointer;
    user-select: none;
  }
  .snapshot-card:hover { border-color: var(--accent); }
  .snapshot-card.active { border-color: var(--accent2); box-shadow: 0 0 0 1px var(--accent2); }
  .sc-name {
    font-family: var(--sans);
    font-size: 12px;
    font-weight: 700;
    color: var(--accent2);
    margin-bottom: 4px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .sc-arrow { font-size: 10px; color: var(--muted); transition: transform .2s; }
  .snapshot-card.active .sc-arrow { transform: rotate(90deg); color: var(--accent2); }
  .sc-count { font-size: 11px; color: var(--muted); margin-bottom: 8px; }
  .sc-count strong { color: var(--pass); }
  .sc-cols { display: flex; flex-wrap: wrap; gap: 4px; }
  .col-pill {
    font-size: 9.5px;
    background: rgba(94,106,210,.12);
    color: var(--accent);
    padding: 2px 7px;
    border-radius: 3px;
    border: 1px solid rgba(94,106,210,.2);
  }

  .table-drawer {
    display: none;
    grid-column: 1 / -1;
    background: var(--surface);
    border: 1px solid var(--accent2);
    border-radius: 10px;
    overflow: hidden;
    animation: slideDown .18s ease;
    margin-top: 2px;
  }
  .table-drawer.open { display: block; }
  @keyframes slideDown {
    from { opacity: 0; transform: translateY(-6px); }
    to   { opacity: 1; transform: translateY(0); }
  }
  .drawer-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 16px;
    border-bottom: 1px solid var(--border);
    background: rgba(167,139,250,.06);
  }
  .drawer-title { font-family: var(--sans); font-size: 13px; font-weight: 700; color: var(--accent2); }
  .drawer-meta  { font-size: 10px; color: var(--muted); margin-left: 10px; }
  .drawer-close {
    background: none; border: none; color: var(--muted); cursor: pointer;
    font-size: 16px; line-height: 1; padding: 2px 8px; border-radius: 4px;
    transition: color .15s, background .15s;
  }
  .drawer-close:hover { color: var(--fail); background: rgba(255,77,106,.1); }
  .drawer-scroll { overflow-x: auto; max-height: 340px; overflow-y: auto; }
  .drawer-scroll::-webkit-scrollbar { height: 5px; width: 5px; }
  .drawer-scroll::-webkit-scrollbar-track { background: var(--bg); }
  .drawer-scroll::-webkit-scrollbar-thumb { background: var(--border); border-radius: 3px; }
  .drawer-loading { padding: 28px; text-align: center; color: var(--muted); font-size: 11px; }
  .drawer-loading::after { content: ''; display: inline-block; width: 10px; height: 10px;
    border: 2px solid var(--border); border-top-color: var(--accent2);
    border-radius: 50%; animation: spin .7s linear infinite; margin-left: 8px; vertical-align: middle; }
  @keyframes spin { to { transform: rotate(360deg); } }
  .data-table { width: 100%; border-collapse: collapse; font-size: 11px; }
  .data-table thead { position: sticky; top: 0; background: #0f1220; z-index: 2; }
  .data-table th {
    padding: 8px 14px; text-align: left; color: var(--accent);
    font-weight: 600; letter-spacing: .05em; font-size: 10px;
    text-transform: uppercase; border-bottom: 1px solid var(--border); white-space: nowrap;
  }
  .data-table td {
    padding: 7px 14px; color: var(--text); border-bottom: 1px solid rgba(30,35,51,.6);
    white-space: nowrap; max-width: 240px; overflow: hidden; text-overflow: ellipsis;
  }
  .data-table tr:last-child td { border-bottom: none; }
  .data-table tr:hover td { background: rgba(94,106,210,.05); }
  .cell-null { color: var(--muted); font-style: italic; }
  .drawer-empty { padding: 28px; text-align: center; color: var(--muted); font-size: 12px; }

  footer {
    margin-top: 56px;
    padding-top: 20px;
    border-top: 1px solid var(--border);
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 11px;
    color: var(--muted);
  }
  .final-verdict {
    font-family: var(--sans);
    font-size: 14px;
    font-weight: 700;
    letter-spacing: .04em;
  }
  .verdict-ok  { color: var(--pass); }
  .verdict-err { color: var(--fail); }
</style>
</head>
<body>
<div class="page">

  <header>
    <div>
      <div class="logo">happy<span>_path</span>.php</div>
      <div class="tagline">DATABASE INTEGRATION TEST RUNNER · my_database</div>
    </div>
    <div class="summary-badges">
      <span class="badge badge-pass">● <?= $pass ?> passed</span>
      <?php if ($fail > 0): ?>
      <span class="badge badge-fail">✕ <?= $fail ?> failed</span>
      <?php endif; ?>
      <span class="badge badge-total">◈ <?= $total ?> total</span>
    </div>
  </header>

  <div class="progress-wrap">
    <div class="progress-label">
      <span>Test progress</span>
      <span><?= $pass ?> / <?= $total ?></span>
    </div>
    <div class="progress-bar"><div class="progress-fill" style="width:<?= $total > 0 ? round($pass / $total * 100) : 0 ?>%"></div></div>
  </div>

  <?php
  $groups = [
    '📡  Connect'   => [],
    '🗄️  Tables'    => [],
    '➕  Insert'    => [],
    '🔍  Select'    => [],
    '✏️  Update'    => [],
    '🗑️  Delete'    => [],
    '🚧  Constraint'=> [],
  ];
  foreach ($results as $r) {
      $matched = false;
      foreach ($groups as $prefix => $_) {
          if (str_starts_with(trim($r['label']), trim(explode(' ', $prefix)[0]))) {
              $groups[$prefix][] = $r; $matched = true; break;
          }
      }
      if (!$matched) $groups['Other'][] = $r;
  }
  foreach ($groups as $suite => $rows):
      if (empty($rows)) continue;
      $sp = count(array_filter($rows, fn($r) => $r['status'] === 'pass'));
      $sf = count($rows) - $sp;
  ?>
  <div class="suite-label"><?= htmlspecialchars($suite) ?> — <?= count($rows) ?> tests, <?= $sp ?> passed<?= $sf ? ", $sf failed" : '' ?></div>
  <?php foreach ($rows as $r): ?>
  <div class="test-row">
    <span class="test-dot dot-<?= $r['status'] ?>"><?= statusIcon($r['status']) ?></span>
    <span class="test-label"><?= htmlspecialchars($r['label']) ?></span>
    <span class="test-detail <?= $r['status'] === 'fail' ? 'fail-detail' : '' ?>" title="<?= htmlspecialchars($r['detail']) ?>">
      <?= htmlspecialchars(mb_strimwidth($r['detail'], 0, 72, '…')) ?>
    </span>
    <span class="test-ms"><?= $r['ms'] ?>ms</span>
  </div>
  <?php endforeach; ?>
  <?php endforeach; ?>

  <div class="suite-label" style="margin-top:40px">📊  Schema Snapshot — <?= count($snapshots) ?> tables · click any card to browse data</div>
  <div class="snapshot-grid" id="snapshot-grid">
    <?php foreach ($snapshots as $tbl => $info): ?>
    <div class="snapshot-card" data-table="<?= htmlspecialchars($tbl) ?>" onclick="toggleDrawer(this)">
      <div class="sc-name">
        <?= htmlspecialchars($tbl) ?>
        <span class="sc-arrow">▶</span>
      </div>
      <div class="sc-count"><strong><?= $info['count'] ?></strong> row<?= $info['count'] !== 1 ? 's' : '' ?></div>
      <div class="sc-cols">
        <?php foreach ($info['columns'] as $col): ?>
        <span class="col-pill"><?= htmlspecialchars($col) ?></span>
        <?php endforeach; ?>
      </div>
    </div>
    <?php endforeach; ?>
    <div class="table-drawer" id="table-drawer">
      <div class="drawer-header">
        <span>
          <span class="drawer-title" id="drawer-title">—</span>
          <span class="drawer-meta" id="drawer-meta"></span>
        </span>
        <button class="drawer-close" onclick="closeDrawer()">✕</button>
      </div>
      <div class="drawer-scroll" id="drawer-body">
        <div class="drawer-loading">Loading</div>
      </div>
    </div>
  </div>

  <footer>
    <span>Run at <?= date('Y-m-d H:i:s') ?> · PHP <?= PHP_VERSION ?></span>
    <span class="final-verdict <?= $fail === 0 ? 'verdict-ok' : 'verdict-err' ?>">
      <?= $fail === 0 ? '✓ ALL TESTS PASSED' : "✕ $fail TEST(S) FAILED" ?>
    </span>
  </footer>

</div>
<script>
let activeTable = null;

function toggleDrawer(card) {
  const tbl = card.dataset.table;
  if (activeTable === tbl) { closeDrawer(); return; }
  document.querySelectorAll('.snapshot-card.active').forEach(c => c.classList.remove('active'));
  card.classList.add('active');
  activeTable = tbl;

  const drawer = document.getElementById('table-drawer');
  const body   = document.getElementById('drawer-body');
  const title  = document.getElementById('drawer-title');
  const meta   = document.getElementById('drawer-meta');

  title.textContent = tbl;
  meta.textContent  = '';
  body.innerHTML    = '<div class="drawer-loading">Loading</div>';
  drawer.classList.add('open');
  drawer.scrollIntoView({ behavior: 'smooth', block: 'nearest' });

  fetch(`?view_table=${encodeURIComponent(tbl)}`)
    .then(r => r.json())
    .then(data => {
      if (!data.ok) { body.innerHTML = `<div class="drawer-empty">Error: ${data.error}</div>`; return; }
      if (!data.rows.length) { body.innerHTML = '<div class="drawer-empty">No rows yet</div>'; return; }
      meta.textContent = `${data.rows.length} row${data.rows.length !== 1 ? 's' : ''} · max 100`;
      const cols = Object.keys(data.rows[0]);
      let html = '<table class="data-table"><thead><tr>';
      cols.forEach(c => { html += `<th>${esc(c)}</th>`; });
      html += '</tr></thead><tbody>';
      data.rows.forEach(row => {
        html += '<tr>';
        cols.forEach(c => {
          const v = row[c];
          if (v === null) html += '<td><span class="cell-null">NULL</span></td>';
          else html += `<td title="${esc(String(v))}">${esc(String(v))}</td>`;
        });
        html += '</tr>';
      });
      html += '</tbody></table>';
      body.innerHTML = html;
    })
    .catch(e => { body.innerHTML = `<div class="drawer-empty">Fetch failed: ${e.message}</div>`; });
}

function closeDrawer() {
  document.getElementById('table-drawer').classList.remove('open');
  document.querySelectorAll('.snapshot-card.active').forEach(c => c.classList.remove('active'));
  activeTable = null;
}

function esc(s) {
  return String(s ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}
</script>
</body>
</html>
<?php
function renderFatal(string $msg): void { ?>
<!DOCTYPE html><html lang="en"><head>
<meta charset="UTF-8">
<link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@600&display=swap" rel="stylesheet">
<style>
  body { background: #0b0d12; color: #ff4d6a; font-family: 'JetBrains Mono', monospace;
         display: flex; align-items: center; justify-content: center; min-height: 100vh; padding: 24px; }
  .box { border: 1px solid rgba(255,77,106,.3); background: rgba(255,77,106,.06);
         padding: 32px 40px; border-radius: 8px; max-width: 640px; }
  h1 { font-size: 16px; margin-bottom: 12px; }
  pre { font-size: 12px; color: #c9d1e0; white-space: pre-wrap; }
</style>
</head><body>
<div class="box">
  <h1>✕ FATAL — Cannot connect to MySQL</h1>
  <pre><?= htmlspecialchars($msg) ?></pre>
</div>
</body></html>
<?php
}