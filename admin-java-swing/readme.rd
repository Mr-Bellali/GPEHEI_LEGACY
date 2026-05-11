i add new tabel fot alert
-- Add these tables if they don't exist
CREATE TABLE IF NOT EXISTS alerts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    message VARCHAR(255) NOT NULL,
    type VARCHAR(50) DEFAULT 'info',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample alerts
INSERT INTO alerts (message, type, is_read) VALUES
('New student registration', 'info', FALSE),
('Project deadline approaching', 'warning', FALSE),
('System update available', 'info', TRUE);