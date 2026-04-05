<?php
$host     = getenv('DB_HOST')     ?: 'mysql';
$port     = getenv('DB_PORT')     ?: 3306;
$dbname   = getenv('DB_NAME')     ?: 'my_database';
$user     = getenv('DB_USER')     ?: 'root';
$password = getenv('DB_PASSWORD') ?: 'my-secret-pw';

$dsn = "mysql:host=$host;port=$port;dbname=$dbname;charset=utf8mb4";
$opts = [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION, PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC];

// ── AJAX endpoint ──────────────────────────────────────────
if (isset($_GET['api'])) {
    header('Content-Type: application/json');
    try {
        $pdo = new PDO($dsn, $user, $password, $opts);
        $action = $_GET['api'];

        if ($action === 'rows') {
            $tbl    = preg_replace('/[^a-zA-Z0-9_]/', '', $_GET['table'] ?? '');
            $page   = max(1, (int)($_GET['page'] ?? 1));
            $limit  = 50;
            $offset = ($page - 1) * $limit;
            $search = trim($_GET['search'] ?? '');

            // Get columns
            $cols = $pdo->query("SHOW COLUMNS FROM `$tbl`")->fetchAll();
            $colNames = array_column($cols, 'Field');

            // Build WHERE for search
            $where = '';
            $params = [];
            if ($search !== '') {
                $clauses = array_map(fn($c) => "CAST(`$c` AS CHAR) LIKE ?", $colNames);
                $where = 'WHERE ' . implode(' OR ', $clauses);
                $params = array_fill(0, count($colNames), "%$search%");
            }

            $total = $pdo->prepare("SELECT COUNT(*) FROM `$tbl` $where");
            $total->execute($params);
            $totalRows = (int)$total->fetchColumn();

            $stmt = $pdo->prepare("SELECT * FROM `$tbl` $where LIMIT $limit OFFSET $offset");
            $stmt->execute($params);
            $rows = $stmt->fetchAll();

            // Column meta (type, key, nullable)
            $colMeta = [];
            foreach ($cols as $c) {
                $colMeta[$c['Field']] = ['type' => $c['Type'], 'key' => $c['Key'], 'null' => $c['Null']];
            }

            echo json_encode([
                'ok' => true, 'rows' => $rows,
                'columns' => $colNames, 'colMeta' => $colMeta,
                'total' => $totalRows, 'page' => $page, 'pages' => ceil($totalRows / $limit),
                'limit' => $limit,
            ]);
        }

        elseif ($action === 'tables') {
            $tables = [];
            $stmt = $pdo->query("
                SELECT TABLE_NAME, TABLE_ROWS, TABLE_COMMENT,
                       ROUND((DATA_LENGTH + INDEX_LENGTH) / 1024, 1) AS size_kb
                FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = '$dbname'
                ORDER BY TABLE_NAME
            ");
            foreach ($stmt->fetchAll() as $r) {
                // Exact count (TABLE_ROWS is approximate for InnoDB)
                $cnt = $pdo->query("SELECT COUNT(*) FROM `{$r['TABLE_NAME']}`")->fetchColumn();
                $tables[] = ['name' => $r['TABLE_NAME'], 'rows' => (int)$cnt, 'size' => $r['size_kb']];
            }
            echo json_encode(['ok' => true, 'tables' => $tables]);
        }

    } catch (Throwable $e) {
        echo json_encode(['ok' => false, 'error' => $e->getMessage()]);
    }
    exit;
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>DB Browser — <?= htmlspecialchars($dbname) ?></title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:ital,wght@0,400;0,600;1,400&family=Syne:wght@700;800&display=swap" rel="stylesheet">
<style>
:root {
  --bg:       #080a10;
  --sidebar:  #0d1018;
  --surface:  #10141f;
  --card:     #141825;
  --border:   #1c2236;
  --border2:  #252d42;
  --pass:     #20d472;
  --fail:     #ff4560;
  --warn:     #f0a500;
  --blue:     #4c9cf8;
  --purple:   #9d7dea;
  --text:     #cdd5e8;
  --muted:    #4a5470;
  --muted2:   #6b7694;
  --mono:     'JetBrains Mono', monospace;
  --sans:     'Syne', sans-serif;
  --sidebar-w: 260px;
}
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
html, body { height: 100%; overflow: hidden; }
body { background: var(--bg); color: var(--text); font-family: var(--mono); font-size: 13px; display: flex; }

/* ── Sidebar ── */
#sidebar {
  width: var(--sidebar-w);
  min-width: var(--sidebar-w);
  height: 100vh;
  background: var(--sidebar);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.sidebar-top {
  padding: 18px 16px 12px;
  border-bottom: 1px solid var(--border);
}
.db-badge {
  display: flex; align-items: center; gap: 8px; margin-bottom: 14px;
}
.db-dot {
  width: 8px; height: 8px; border-radius: 50%;
  background: var(--pass);
  box-shadow: 0 0 6px var(--pass);
  flex-shrink: 0;
}
.db-name { font-family: var(--sans); font-size: 14px; font-weight: 700; color: var(--text); }
.db-host { font-size: 10px; color: var(--muted2); margin-top: 1px; }

#sidebar-search {
  width: 100%; background: var(--surface); border: 1px solid var(--border2);
  border-radius: 6px; color: var(--text); font-family: var(--mono); font-size: 11px;
  padding: 7px 10px; outline: none;
  transition: border-color .15s;
}
#sidebar-search::placeholder { color: var(--muted); }
#sidebar-search:focus { border-color: var(--purple); }

.sidebar-list {
  flex: 1; overflow-y: auto; padding: 8px 0;
}
.sidebar-list::-webkit-scrollbar { width: 4px; }
.sidebar-list::-webkit-scrollbar-track { background: transparent; }
.sidebar-list::-webkit-scrollbar-thumb { background: var(--border2); border-radius: 2px; }

.tbl-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 16px;
  cursor: pointer;
  border-left: 2px solid transparent;
  transition: background .12s, border-color .12s;
  gap: 8px;
}
.tbl-item:hover { background: rgba(255,255,255,.03); }
.tbl-item.active {
  background: rgba(157,125,234,.08);
  border-left-color: var(--purple);
}
.tbl-item-name {
  font-size: 12px; color: var(--text); flex: 1; overflow: hidden;
  text-overflow: ellipsis; white-space: nowrap;
}
.tbl-item.active .tbl-item-name { color: var(--purple); }
.tbl-item-count {
  font-size: 10px; color: var(--muted);
  background: var(--surface); padding: 1px 6px; border-radius: 10px;
  white-space: nowrap; flex-shrink: 0;
}

.sidebar-footer {
  padding: 10px 16px;
  border-top: 1px solid var(--border);
  font-size: 10px;
  color: var(--muted);
}

/* ── Main ── */
#main {
  flex: 1; display: flex; flex-direction: column; overflow: hidden; height: 100vh;
}

/* Top bar */
#topbar {
  display: flex; align-items: center; gap: 12px;
  padding: 12px 20px;
  border-bottom: 1px solid var(--border);
  background: var(--surface);
  flex-shrink: 0;
}
#topbar-title {
  font-family: var(--sans); font-size: 16px; font-weight: 700;
  color: var(--text); flex: 1;
}
#topbar-title span { color: var(--purple); }
#topbar-meta { font-size: 11px; color: var(--muted2); white-space: nowrap; }

#search-box {
  background: var(--card); border: 1px solid var(--border2);
  border-radius: 6px; color: var(--text); font-family: var(--mono);
  font-size: 11px; padding: 6px 12px; outline: none; width: 220px;
  transition: border-color .15s, width .2s;
}
#search-box::placeholder { color: var(--muted); }
#search-box:focus { border-color: var(--blue); width: 280px; }

/* Column headers */
#col-header {
  display: flex; align-items: center;
  padding: 0 20px;
  background: var(--sidebar);
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
  overflow-x: auto;
  overflow-y: hidden;
}
#col-header::-webkit-scrollbar { display: none; }

/* Data area */
#data-area {
  flex: 1; overflow: auto; position: relative;
}
#data-area::-webkit-scrollbar { width: 8px; height: 8px; }
#data-area::-webkit-scrollbar-track { background: var(--bg); }
#data-area::-webkit-scrollbar-thumb { background: var(--border2); border-radius: 4px; }

/* Table */
#data-table {
  width: max-content; min-width: 100%;
  border-collapse: collapse;
}
#data-table thead { position: sticky; top: 0; z-index: 5; }
#data-table th {
  background: var(--sidebar);
  padding: 10px 16px;
  text-align: left;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: .07em;
  text-transform: uppercase;
  color: var(--blue);
  border-bottom: 1px solid var(--border);
  border-right: 1px solid var(--border);
  white-space: nowrap;
  cursor: default;
}
#data-table th .col-type {
  display: block;
  font-size: 9px;
  color: var(--muted);
  font-weight: 400;
  letter-spacing: 0;
  text-transform: none;
  margin-top: 1px;
}
#data-table th .col-key { color: var(--warn); margin-left: 4px; }
#data-table td {
  padding: 8px 16px;
  border-bottom: 1px solid var(--border);
  border-right: 1px solid rgba(28,34,54,.8);
  white-space: nowrap;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 12px;
  color: var(--text);
  vertical-align: middle;
}
#data-table tr:hover td { background: rgba(76,156,248,.04); }
#data-table tr:hover td:first-child { border-left: 2px solid var(--blue); padding-left: 14px; }
.cell-null { color: var(--muted); font-style: italic; font-size: 11px; }
.cell-num  { color: var(--blue); }
.cell-bool-t { color: var(--pass); }
.cell-bool-f { color: var(--fail); }
.cell-hash { color: var(--muted2); }
.cell-date { color: var(--warn); }
.cell-long { color: var(--muted2); cursor: help; }
.cell-enum { 
  display: inline-block; padding: 1px 8px; border-radius: 10px;
  font-size: 10px; font-weight: 600;
  background: rgba(157,125,234,.15); color: var(--purple); border: 1px solid rgba(157,125,234,.3);
}

/* Row number col */
.row-num { color: var(--muted); font-size: 10px; user-select: none; }

/* Empty / loading states */
#state-overlay {
  position: absolute; inset: 0;
  display: flex; align-items: center; justify-content: center;
  flex-direction: column; gap: 14px;
  background: var(--bg);
}
.spinner {
  width: 32px; height: 32px;
  border: 2px solid var(--border2);
  border-top-color: var(--purple);
  border-radius: 50%;
  animation: spin .7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
.state-msg { font-size: 12px; color: var(--muted2); }
.state-hint { font-size: 11px; color: var(--muted); }

/* Pagination */
#pagination {
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px 20px;
  border-top: 1px solid var(--border);
  background: var(--surface);
  flex-shrink: 0;
  font-size: 11px;
  color: var(--muted2);
}
.pg-info strong { color: var(--text); }
.pg-controls { display: flex; gap: 6px; align-items: center; }
.pg-btn {
  background: var(--card); border: 1px solid var(--border2);
  color: var(--text); font-family: var(--mono); font-size: 11px;
  padding: 4px 12px; border-radius: 5px; cursor: pointer;
  transition: border-color .15s, background .15s;
}
.pg-btn:hover:not(:disabled) { border-color: var(--purple); background: rgba(157,125,234,.08); }
.pg-btn:disabled { opacity: .3; cursor: default; }
.pg-num { color: var(--muted2); padding: 0 6px; }

/* Welcome screen */
#welcome {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  height: 100%; gap: 12px; color: var(--muted);
}
.welcome-icon { font-size: 40px; margin-bottom: 8px; }
.welcome-title { font-family: var(--sans); font-size: 18px; font-weight: 700; color: var(--muted2); }
.welcome-sub { font-size: 12px; }
</style>
</head>
<body>

<!-- ── Sidebar ── -->
<aside id="sidebar">
  <div class="sidebar-top">
    <div class="db-badge">
      <div class="db-dot"></div>
      <div>
        <div class="db-name"><?= htmlspecialchars($dbname) ?></div>
        <div class="db-host"><?= htmlspecialchars($host) ?>:<?= $port ?></div>
      </div>
    </div>
    <input id="sidebar-search" type="text" placeholder="Filter tables…" oninput="filterSidebar(this.value)">
  </div>
  <div class="sidebar-list" id="sidebar-list">
    <div style="padding:20px 16px;color:var(--muted);font-size:11px;">Loading tables…</div>
  </div>
  <div class="sidebar-footer" id="sidebar-footer">—</div>
</aside>

<!-- ── Main ── -->
<main id="main">
  <div id="topbar">
    <div id="topbar-title">Select a table</div>
    <span id="topbar-meta"></span>
    <input id="search-box" type="text" placeholder="Search rows…" oninput="debounceSearch(this.value)" style="display:none">
  </div>

  <div id="content" style="flex:1;display:flex;flex-direction:column;overflow:hidden;position:relative;">
    <div id="welcome">
      <div class="welcome-icon">🗄️</div>
      <div class="welcome-title">Database Browser</div>
      <div class="welcome-sub">Pick a table from the sidebar</div>
    </div>
  </div>
</main>

<script>
const state = {
  tables: [],
  activeTable: null,
  page: 1,
  pages: 1,
  total: 0,
  search: '',
  colMeta: {},
  searchTimer: null,
};

// ── Boot ─────────────────────────────────────────────────────
fetch('?api=tables')
  .then(r => r.json())
  .then(data => {
    if (!data.ok) { showSidebarError(data.error); return; }
    state.tables = data.tables;
    renderSidebar(data.tables);
    document.getElementById('sidebar-footer').textContent =
      `${data.tables.length} tables · ${data.tables.reduce((s,t)=>s+t.rows,0).toLocaleString()} total rows`;
  })
  .catch(e => showSidebarError(e.message));

// ── Sidebar ──────────────────────────────────────────────────
function renderSidebar(tables) {
  const list = document.getElementById('sidebar-list');
  if (!tables.length) { list.innerHTML = '<div style="padding:16px;color:var(--muted);font-size:11px;">No tables found</div>'; return; }
  list.innerHTML = tables.map(t => `
    <div class="tbl-item" id="si-${t.name}" onclick="openTable('${esc(t.name)}')">
      <span class="tbl-item-name">${esc(t.name)}</span>
      <span class="tbl-item-count">${t.rows.toLocaleString()}</span>
    </div>`).join('');
}

function filterSidebar(q) {
  const filtered = q ? state.tables.filter(t => t.name.toLowerCase().includes(q.toLowerCase())) : state.tables;
  renderSidebar(filtered);
  if (state.activeTable) {
    const el = document.getElementById('si-' + state.activeTable);
    if (el) el.classList.add('active');
  }
}

function showSidebarError(msg) {
  document.getElementById('sidebar-list').innerHTML =
    `<div style="padding:16px;color:var(--fail);font-size:11px;">${esc(msg)}</div>`;
}

// ── Open table ────────────────────────────────────────────────
function openTable(name) {
  document.querySelectorAll('.tbl-item').forEach(el => el.classList.remove('active'));
  const item = document.getElementById('si-' + name);
  if (item) item.classList.add('active');
  state.activeTable = name;
  state.page = 1;
  state.search = '';
  document.getElementById('search-box').value = '';
  document.getElementById('search-box').style.display = '';
  loadRows();
}

// ── Load rows ─────────────────────────────────────────────────
function loadRows() {
  const { activeTable: tbl, page, search } = state;
  if (!tbl) return;

  setContent(`
    <div id="state-overlay">
      <div class="spinner"></div>
      <div class="state-msg">Loading <strong>${esc(tbl)}</strong>…</div>
    </div>`);

  const url = `?api=rows&table=${encodeURIComponent(tbl)}&page=${page}&search=${encodeURIComponent(search)}`;
  fetch(url)
    .then(r => r.json())
    .then(data => {
      if (!data.ok) { setContent(`<div id="state-overlay"><div class="state-msg" style="color:var(--fail)">${esc(data.error)}</div></div>`); return; }
      state.pages   = data.pages;
      state.total   = data.total;
      state.colMeta = data.colMeta;
      renderTable(data);
    })
    .catch(e => setContent(`<div id="state-overlay"><div class="state-msg" style="color:var(--fail)">${esc(e.message)}</div></div>`));
}

// ── Render table ──────────────────────────────────────────────
function renderTable(data) {
  const { rows, columns, colMeta, total, page, pages, limit } = data;
  const from = (page - 1) * limit + 1;
  const to   = Math.min(page * limit, total);

  // Topbar
  document.getElementById('topbar-title').innerHTML = `<span>${esc(state.activeTable)}</span>`;
  document.getElementById('topbar-meta').textContent =
    total ? `${from.toLocaleString()}–${to.toLocaleString()} of ${total.toLocaleString()} rows` : '0 rows';

  if (!rows.length) {
    setContent(`<div id="state-overlay"><div class="state-msg">No rows found</div><div class="state-hint">${state.search ? 'Try a different search' : 'Table is empty'}</div></div>`);
    renderPagination(total, page, pages, limit, from, to);
    return;
  }

  // Build table HTML
  let th = '<th style="width:40px"><span style="color:var(--muted)">#</span></th>';
  columns.forEach(col => {
    const m   = colMeta[col] || {};
    const key = m.key === 'PRI' ? ' <span class="col-key">PK</span>' : (m.key === 'MUL' ? ' <span class="col-key" style="color:var(--blue)">FK</span>' : '');
    th += `<th>${esc(col)}${key}<span class="col-type">${esc(m.type || '')}</span></th>`;
  });

  let tbody = '';
  const rowStart = (page - 1) * limit;
  rows.forEach((row, i) => {
    let tds = `<td class="row-num">${(rowStart + i + 1).toLocaleString()}</td>`;
    columns.forEach(col => {
      tds += `<td title="${esc(String(row[col] ?? ''))}">${formatCell(row[col], col, colMeta[col])}</td>`;
    });
    tbody += `<tr>${tds}</tr>`;
  });

  const tableHTML = `
    <div id="data-area">
      <table id="data-table">
        <thead><tr>${th}</tr></thead>
        <tbody>${tbody}</tbody>
      </table>
    </div>`;

  setContent(tableHTML);
  renderPagination(total, page, pages, limit, from, to);
}

// ── Format cell value ─────────────────────────────────────────
function formatCell(val, col, meta) {
  if (val === null || val === undefined) return '<span class="cell-null">NULL</span>';
  const s   = String(val);
  const t   = (meta?.type || '').toLowerCase();

  // Hashed passwords
  if (col.includes('password') || col.includes('hashed')) {
    return `<span class="cell-hash" title="${esc(s)}">${s.slice(0, 20)}…</span>`;
  }
  // Dates
  if (t.includes('date') || t.includes('time')) {
    return `<span class="cell-date">${esc(s)}</span>`;
  }
  // Enums
  if (t.startsWith('enum')) {
    return `<span class="cell-enum">${esc(s)}</span>`;
  }
  // Numbers
  if (t.includes('int') || t.includes('float') || t.includes('decimal') || t.includes('double')) {
    return `<span class="cell-num">${esc(s)}</span>`;
  }
  // Long text
  if (s.length > 80) {
    return `<span class="cell-long" title="${esc(s)}">${esc(s.slice(0, 80))}…</span>`;
  }
  return esc(s);
}

// ── Pagination ────────────────────────────────────────────────
function renderPagination(total, page, pages, limit, from, to) {
  // Remove existing
  const old = document.getElementById('pagination');
  if (old) old.remove();

  const pag = document.createElement('div');
  pag.id = 'pagination';
  pag.innerHTML = `
    <span class="pg-info">
      Showing <strong>${from.toLocaleString()}–${to.toLocaleString()}</strong> of <strong>${total.toLocaleString()}</strong> rows
      ${state.search ? `· filtered` : ''}
    </span>
    <div class="pg-controls">
      <button class="pg-btn" onclick="goPage(1)" ${page<=1?'disabled':''}>«</button>
      <button class="pg-btn" onclick="goPage(${page-1})" ${page<=1?'disabled':''}>‹ Prev</button>
      <span class="pg-num">Page ${page} / ${pages||1}</span>
      <button class="pg-btn" onclick="goPage(${page+1})" ${page>=pages?'disabled':''}>Next ›</button>
      <button class="pg-btn" onclick="goPage(${pages})" ${page>=pages?'disabled':''}>»</button>
    </div>`;
  document.getElementById('main').appendChild(pag);
}

function goPage(p) {
  state.page = p;
  loadRows();
}

// ── Search ────────────────────────────────────────────────────
function debounceSearch(val) {
  clearTimeout(state.searchTimer);
  state.searchTimer = setTimeout(() => {
    state.search = val;
    state.page   = 1;
    loadRows();
  }, 320);
}

// ── Helpers ───────────────────────────────────────────────────
function setContent(html) {
  const old = document.getElementById('pagination');
  if (old) old.remove();
  document.getElementById('content').innerHTML = html;
}

function esc(s) {
  return String(s ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}
</script>
</body>
</html>