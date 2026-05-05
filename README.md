# School Management System Database

A comprehensive database system for managing students, teachers, courses, modules, conversations, and projects.

## 📋 Table of Contents
- [Features](#features)
- [Database Schema](#database-schema)
- [Installation](#installation)
- [Usage](#usage)
- [Testing](#testing)
- [Contributing](#contributing)

## ✨ Features

- **User Management**: Students, Teachers, Admins with role-based access
- **Academic Structure**: Filières, Modules (mod/elm hierarchy), Courses
- **Group Management**: Student groups with promotion tracking
- **Project Management**: Project settings, submissions, supervision
- **Communication**: Conversations (group/pair), messages, posts, comments
- **Homework System**: Assignments, submissions with attachments
- **Library**: Document management with multiple file types
- **Voting System**: Posts and comments voting

## 🗄️ Database Schema

### Core Tables
- `admin` - System administrators
- `student` - Student information
- `teacher` - Teacher information
- `filiere` - Academic departments
- `module` - Modules and elements (self-referential hierarchy)

### Academic
- `course` - Course content
- `homework` - Assignments
- `submission` - Student submissions
- `groupe` - Student groups
- `groupeStudent` - Group memberships

### Communication
- `conversation` - Chat conversations
- `conversation_participant` - Conversation members
- `message` - Individual messages
- `post` - Forum posts
- `comment` - Post comments
- `vote` - Voting system

### Project Management
- `projectSettings` - Project configuration
- `project` - Projects
- `projectStudent` - Student project assignments
- `projectSupervisor` - Teacher supervision

### Library
- `library` - Library management
- `documents` - Document storage

## 🚀 Installation

### Prerequisites
- MySQL 5.7+
- PHP 7.4+ (for test runner)
- Docker (optional)

### Using Docker

```bash
# Clone repository
git clone https://github.com/Mr-Bellali/GPEHEI_LEGACY.git
cd school-management-db

# Start containers
docker-compose up -d

# Access MySQL
docker exec -it mysql mysql -uroot -pmy-secret-pw