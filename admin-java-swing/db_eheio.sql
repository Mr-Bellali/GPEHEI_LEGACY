

DROP DATABASE IF EXISTS GPEHEI_LEGACY;
CREATE DATABASE GPEHEI_LEGACY;
USE GPEHEI_LEGACY;

CREATE TABLE admin (
	id INT AUTO_INCREMENT primary key ,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    email varchar(100) not null unique,
    hashed_password text,
    admin_role enum('regular', 'super'),
    phone varchar(12),
    status_admin enum('active', 'disabled') default 'active'
);
INSERT INTO admin (
    first_name,
    last_name,
    email,
    hashed_password,
    admin_role,
    phone
) VALUES (
    'Achraf',
    'Elabouye',
    'achrafelabouye@gmail.com',
    '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
    'super',
    '0600000000'
);


CREATE TABLE teacher (
	id INT AUTO_INCREMENT primary key,
	teacher_number int unique,
	first_name varchar(100) not null,
    last_name varchar(100) not null,
	email varchar(100) not null unique,
	hashed_password varchar(100) not null,
	teacher_status enum('active', 'disabled') default 'active',
	phone varchar(12),
	cin varchar(10),
	    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



CREATE TABLE student(
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_number INT UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    cin VARCHAR(10),
    cne VARCHAR(20),
    email VARCHAR(100) NOT NULL UNIQUE,
    hashed_password TEXT,
    phone VARCHAR(12),
    birth_date DATE NOT NULL,
    StudentStatus ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert test students
INSERT INTO student (student_number, first_name, last_name, cin, cne, email, hashed_password, phone, birth_date, StudentStatus) VALUES
(1001, 'Mohammed', 'Ali', 'BK123456', 'CNE001', 'mohammed@example.com', 'hashed_password', '0600000001', '2000-01-15', 'ACTIVE'),
(1002, 'Fatima', 'Hassan', 'BK123457', 'CNE002', 'fatima@example.com', 'hashed_password', '0600000002', '2000-03-20', 'ACTIVE'),
(1003, 'Ahmed', 'Ibrahim', 'BK123458', 'CNE003', 'ahmed@example.com', 'hashed_password', '0600000003', '2000-06-10', 'ACTIVE'),
(1004, 'Aisha', 'Mohammed', 'BK123459', 'CNE004', 'aisha@example.com', 'hashed_password', '0600000004', '2000-09-25', 'INACTIVE'),
(1005, 'Omar', 'Benali', 'BK123460', 'CNE005', 'omar@example.com', 'hashed_password', '0600000005', '2000-12-01', 'SUSPENDED');

CREATE TABLE filiere(
	id INT AUTO_INCREMENT PRIMARY KEY,
	name_filier VARCHAR(100) NOT NULL,
	short_name VARCHAR(10) NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
	);

CREATE TABLE module(
    id INT AUTO_INCREMENT PRIMARY KEY,
    module_name VARCHAR(120) NOT NULL,
    type_module ENUM('mod', 'elm') NOT NULL,
    parent_module_id INT,
    filiere_id INT NOT NULL,
    module_status enum('active', 'disabled') default 'active',
    semester INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_parent_module FOREIGN KEY (parent_module_id) REFERENCES module(id) ON DELETE CASCADE,
    CONSTRAINT fk_filiers FOREIGN KEY (filiere_id) REFERENCES filiere(id),
     CONSTRAINT chk_module_hierarchy CHECK (
        (type_module = 'mod' AND parent_module_id IS NULL) OR
        (type_module = 'elm' AND parent_module_id IS NOT NULL)
    )
    
);

CREATE TABLE teacher_module (
	teacher_id int,
    module_id int,
    primary key (teacher_id, module_id),
    foreign key (teacher_id)
    references teacher(id),
    foreign key (module_id)
    references module(id)
);

CREATE TABLE groupe (
	id INT AUTO_INCREMENT primary key,
    group_name char(1),
    promotion varchar(10),
    filiere_id INT,
    groupe_status enum('active', 'disabled') default 'active',
    CONSTRAINT fk_filiere FOREIGN KEY (filiere_id) REFERENCES filiere(id)
    
);

CREATE TABLE groupeStudent (
	groupe_id INT,
	promotion varchar(10),
   student_id INT,
   groupeStudent_status enum('active', 'disabled') default 'active',
   PRIMARY KEY (groupe_id, promotion, student_id),
   CONSTRAINT fk_groupe_student_group FOREIGN KEY (groupe_id) REFERENCES groupe(id),
   CONSTRAINT fk_groupe_student_student FOREIGN KEY (student_id) REFERENCES student(id)
);

CREATE TABLE projectSettings (
	id INT AUTO_INCREMENT primary key,
    max_capacity int,
    min_capacity int,
    filiere_id int,
    promotion varchar(10),
    deadline datetime, 
    CONSTRAINT fk_filiere_projectSettings FOREIGN KEY (filiere_id) REFERENCES filiere(id)
);

CREATE TABLE project (
    id INT AUTO_INCREMENT primary key,
    project_name varchar(120),
    project_title varchar(120),
    project_description text,
	 project_type enum('projet_synthèse', 'projet_fin_année', 'projet_fin_études'),
    project_settings_id int,
	 project_status enum('in_progress', 'submitted', 'missed') default 'in_progress',
    privacy_type ENUM('private','public') DEFAULT 'private',
    constraint project_settings_fk foreign key (project_settings_id) references projectSettings(id)
);

CREATE TABLE projectStudent(
	student_id int,
   project_id int,
    
    primary key(student_id, project_id),
    constraint student_project_fk FOREIGN KEY (student_id) REFERENCES student(id),
    constraint project_project_student_fk foreign key (project_id) references project(id)
);

CREATE TABLE projectSupervisor(
	teacher_id int,
    project_id int,
    
    primary key(teacher_id, project_id),
    
    constraint teacher_project_suppervisor_fk foreign key (teacher_id) references teacher(id),
    
    constraint project_project_suppervisor_fk foreign key (project_id) references project(id)
);


CREATE TABLE projectSubmissionAtachement (
	id INT AUTO_INCREMENT primary key,
    attachement_name varchar(100),
    link text,
    
    project_id int,
    constraint project_fk
    foreign key (project_id)
    references project(id)
);

CREATE TABLE conversation(
    id INT AUTO_INCREMENT PRIMARY KEY,
    createdby_student_id INT,
    createdby_teacher_id INT,
    totexting_student_id INT,
    totexting_teacher_id INT,
    type_conv ENUM('group','pair') NOT NULL,
    last_message_id INT,
    conversation_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (createdby_student_id) REFERENCES student(id),
    FOREIGN KEY (createdby_teacher_id) REFERENCES teacher(id),
    FOREIGN KEY (totexting_student_id) REFERENCES student(id),
    FOREIGN KEY (totexting_teacher_id) REFERENCES teacher(id),
    CONSTRAINT chk_createdby CHECK (
        (createdby_student_id IS NOT NULL AND createdby_teacher_id IS NULL) OR
        (createdby_student_id IS NULL AND createdby_teacher_id IS NOT NULL)
    ),
	 CONSTRAINT chk_totexting CHECK (
    (type_conv = 'pair' AND 
     ((totexting_student_id IS NOT NULL AND totexting_teacher_id IS NULL) OR
      (totexting_student_id IS NULL AND totexting_teacher_id IS NOT NULL))
    ) OR
    (type_conv = 'group' AND 
     totexting_student_id IS NULL AND totexting_teacher_id IS NULL)
	),
	    CONSTRAINT chk_no_self_conversation CHECK (
	        type_conv = 'group' OR
	        NOT (
	            (createdby_student_id IS NOT NULL AND createdby_student_id = totexting_student_id) OR
	            (createdby_teacher_id IS NOT NULL AND createdby_teacher_id = totexting_teacher_id)
	        )
	    )
	);

CREATE TABLE conversation_participant (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conversation_id INT NOT NULL,
    student_id INT,
    teacher_id INT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversation(id),
    FOREIGN KEY (student_id) REFERENCES student(id),
    FOREIGN KEY (teacher_id) REFERENCES teacher(id),
     CONSTRAINT chk_participant_type CHECK (
        (student_id IS NOT NULL AND teacher_id IS NULL) OR
        (student_id IS NULL AND teacher_id IS NOT NULL)
    ),
    UNIQUE KEY unique_participant (conversation_id, student_id, teacher_id)
);

CREATE TABLE course(
	id INT AUTO_INCREMENT primary key,
    course_name varchar(120),
	content text,
    course_status enum('active', 'disabled') default 'active',
    
    module_id int,
    constraint module_fk
    foreign key (module_id)
    references module(id)
);




CREATE TABLE homework(
	id INT AUTO_INCREMENT primary key,
    title varchar(120),
    content text,
    deadline datetime,
    
    teacher_id int,
    groupe_id int,
    module_id int,  
    constraint teacher_homework_fk
    foreign key (teacher_id)
    references teacher(id),
    
    constraint groupe_homework_fk
    foreign key (groupe_id)
    references groupe(id),
    
    constraint module_homework_fk
    foreign key (module_id)
    references module(id)
);

CREATE TABLE homeworkAtachement (
	id INT AUTO_INCREMENT primary key,
    attachement_name varchar(100),
    link text,
    
    homework_id int,
    constraint homework_fk
    foreign key (homework_id)
    references homework(id)
);

CREATE TABLE submission(
	id INT AUTO_INCREMENT primary key,
    title varchar(120),
    content text,
    submission_status enum('submitted', 'missed', 'in_progress') default 'in_progress',
    
# foreign keys
    homework_id int,
    student_id int,
    
    constraint homework_submission_fk
    foreign key (homework_id)
    references homework(id),
    
    constraint student_submission_fk
    foreign key (student_id)
    references student(id)
);

CREATE TABLE submissionAtachement (
	id INT AUTO_INCREMENT primary key,
    attachement_name varchar(100),
    link text,
    
    submission_id int,
    constraint submission_fk
    foreign key (submission_id)
    references submission(id)
);





CREATE TABLE library(
	id INT AUTO_INCREMENT primary key,
	library_name varchar(120),
    library_status enum('active', 'disabled') default 'active'
);

CREATE TABLE documents(
	id INT AUTO_INCREMENT primary key,
    document_name text,
    document_description text,
    link text,
    document_type enum('book', 'report', 'other'),
    document_status enum ('active', 'disabled') default 'active',
    
    #foreign keys
    admin_id int,
    teacher_id int,
    student_id int,
    
    constraint admin_fk
    foreign key (admin_id)
    references admin(id),

    constraint teacher_fk
    foreign key (teacher_id)
    references teacher(id),

    constraint student_fk
    foreign key (student_id)
    references student(id),
    
    #other params
    last_modification_by_admin_id int
);

CREATE TABLE post(
	id INT AUTO_INCREMENT primary key,
	title varchar(120),
    content text,
    post_status enum('active', 'disabled', 'updated', 'archived') default 'active',
	
    student_id int,
    teacher_id int,
    
    constraint student_post_fk
    foreign key	(student_id)
    references student(id),

	constraint teacher_post_fk
    foreign key	(teacher_id)
    references teacher(id)
);

CREATE TABLE post_attachement(
	id INT AUTO_INCREMENT primary key,
    attachement_name varchar(100),
    link text,
    
    post_id int,
    constraint post_id_fk
    foreign key (post_id)
    references post(id)
);

CREATE TABLE comment(
	id INT AUTO_INCREMENT primary key,
    content text,
    
	student_id int,
    teacher_id int,
    post_id int,
    
    constraint student_comment_fk
    foreign key	(student_id)
    references student(id),

	constraint teacher_comment_fk
    foreign key	(teacher_id)
    references teacher(id),
    
    constraint post_comment_fk
    foreign key	(post_id)
    references post(id)
);

CREATE TABLE vote(
	id INT AUTO_INCREMENT primary key,
    
	student_id int,
    teacher_id int,
    post_id int,
    comment_id int,
    
    constraint student_vote_fk
    foreign key	(student_id)
    references student(id),

	constraint teacher_vote_fk
    foreign key	(teacher_id)
    references teacher(id),
    
    constraint post_vote_fk
    foreign key	(post_id)
    references post(id),
    
    constraint comment_vote_fk
    foreign key	(comment_id)
    references comment(id)
);



CREATE TABLE message(
	id INT AUTO_INCREMENT primary key,
    content varchar(1000) not null,
    message_type enum('media', 'text') default 'text',
    mime_type varchar(50),
    file_name varchar(100),
    original_content varchar(1000),
    message_status enum('normal', 'modified', 'disabled'),
    
	conversation_id int,
	sender_teacher_id int,
    sender_student_id int,
    
	constraint conversation_message_fk
    foreign key (conversation_id)
    references conversation(id),
    
    constraint sender_teacher_fk
    foreign key (sender_teacher_id)
    references teacher(id),
    
    constraint sender_student_fk
    foreign key (sender_student_id)
    references student(id)
);

-- for admin 
CREATE TABLE IF NOT EXISTS alerts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    message VARCHAR(255) NOT NULL,
    type VARCHAR(50) DEFAULT 'info',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- random alert bach ndiro test
INSERT INTO alerts (message, type, is_read) VALUES 
('New student registration', 'info', FALSE),
('Project deadline approaching', 'warning', FALSE),
('System update available', 'info', TRUE);
