# GPEHEI Legacy Project

A containerized multiple projects that form a synced system.

## Project Overview

This project built to solve the issue that the teachers having, managing the projects by the groups from the last 3 engineering years, and they need a huge amount to supervise and follow the progress all of the projects 

## Architecture

The project follows a monolyth architecture pattern with the following components:

- **Web Server (server)**: PHP 8.4 running on Apache with PDO/MySQL support
- **Database (mysql)**: MySQL 8.0 database server

### Service Communication

- The web server depends on the MySQL service
- Database connections use Docker internal networking (service name resolution)
- The web server connects to MySQL using the hostname `mysql` (Docker's internal DNS)

## Folder Structure

```
GPEHEI_LEGACY/
├── README.md                    # This file
├── docker-compose.yaml          # Docker Compose configuration
├── admin/                       # Main PHP application
│   ├── Dockerfile              # Docker image configuration for PHP/Apache
│   ├── composer.json           # PHP dependencies
│   ├── composer.lock           # Locked dependency versions
│   ├── README.md               # Admin service documentation
│   ├── README.Docker.md        # Docker-specific documentation
│   └── src/                    # PHP application source code
│       ├── database.php        # Database connection logic
│       ├── happy_path.php      # Happy path test script
│       └── hello.php           # Hello world example
```

## Prerequisites

Before running the project, ensure you have the following installed on your system:

- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 2.0 or higher

To check if Docker is installed:
```bash
docker --version
docker compose version
```

To install Docker and Docker Compose, visit: https://docs.docker.com/get-docker/

## Getting Started

### 1. Clone or Navigate to the Project

```bash
cd /path/to/GPEHEI_LEGACY
```

### 2. Start the Entire Project

To start all services (web server + MySQL database) with a fresh build:

```bash
docker compose up --build
```

This command will:
- Build the PHP/Apache Docker image from the Dockerfile in the `admin/` directory
- Pull the MySQL 8.0 image from Docker Hub
- Start both containers
- Display logs from both services
- Create a named volume for MySQL data persistence

To start services without rebuilding images (if they already exist):

```bash
docker compose up
```

To run services in the background (detached mode):

```bash
docker compose up -d
```

### 3. Stop the Project

To stop all running services:

```bash
docker compose down
```

To stop services and remove volumes (this will delete the database):

```bash
docker compose down -v
```

### 4. View Running Services

To check the status of all services:

```bash
docker compose ps
```

## Accessing Services

### Web Application

Once the services are running, access the PHP web application at:

- Base URL: http://localhost:9000
- Hello endpoint: http://localhost:9000/hello.php
- Happy path: http://localhost:9000/happy_path.php

### MySQL Database

Connect to the MySQL database using:

- **Host**: localhost
- **Port**: 3306
- **Username**: root
- **Password**: my-secret-pw
- **Database**: my_database

Example command line connection:
```bash
mysql -h 127.0.0.1 -u root -p my_database
```

## Running Specific Services

### Run Only the Web Server

```bash
docker compose up server --build
```

Note: This will fail unless MySQL is running elsewhere, as the web server depends on the database.

### Run Only MySQL

```bash
docker compose up mysql
```

### Build Only the Web Server Image

```bash
docker compose build server
```

### Rebuild All Images

```bash
docker compose build
```

## Useful Commands

### Viewing Logs

View logs from all services:
```bash
docker compose logs
```

View logs from a specific service:
```bash
docker compose logs server
docker compose logs mysql
```

Follow logs in real-time:
```bash
docker compose logs -f
```

### Executing Commands in Running Containers

Run a command in the web server container:
```bash
docker compose exec server bash
```

Run a PHP command:
```bash
docker compose exec server php -v
```

Execute a MySQL query:
```bash
docker compose exec mysql mysql -u root -pmy-secret-pw my_database -e "SHOW TABLES;"
```

### Environment Configuration

The services are configured with the following environment variables in `docker-compose.yaml`:

**Web Server**:
- `DB_HOST`: mysql
- `DB_PORT`: 3306
- `DB_NAME`: my_database
- `DB_USER`: root
- `DB_PASSWORD`: my-secret-pw

**MySQL Database**:
- `MYSQL_ROOT_PASSWORD`: my-secret-pw
- `MYSQL_DATABASE`: my_database

To modify these values, edit the `docker-compose.yaml` file.

## Data Persistence

The MySQL database uses a named volume (`mysql-data`) to persist data across container restarts. This means:

- Data survives container stops and starts (`docker compose down` followed by `docker compose up`)
- Data is only deleted when explicitly removing volumes (`docker compose down -v`)
- Volume is managed by Docker and stored outside the container

## Development Workflow

### Making Changes to PHP Code

1. Edit files in `admin/src/` directory
2. Refresh your browser to see changes (no rebuild needed for PHP code changes)
3. If you modify the Dockerfile or dependencies, rebuild with `docker compose up --build`

### Modifying Dependencies

1. Edit `admin/composer.json` to add/remove PHP packages
2. Rebuild the image: `docker compose up --build`

### Database Changes

1. Connect to the MySQL container:
   ```bash
   docker compose exec mysql mysql -u root -pmy-secret-pw my_database
   ```
2. Execute SQL commands directly
3. Or modify database setup scripts and rebuild

## Troubleshooting

### Container Won't Start

Check the logs for specific error messages:
```bash
docker compose logs
```

### Port Already in Use

If port 9000 or 3306 is already in use, either:
- Stop the conflicting service
- Modify the port mappings in `docker-compose.yaml`

Example: Change `9000:80` to `8080:80` to use port 8080 instead

### Database Connection Errors

Ensure MySQL is fully started before the web server makes database connections. The `docker-compose.yaml` has `depends_on` configured, but this only ensures startup order, not readiness.

### Clear All Data and Start Fresh

To completely reset the project:
```bash
docker compose down -v
docker compose build --no-cache
docker compose up --build
```

## Additional Resources

- Docker PHP Documentation: https://hub.docker.com/_/php
- MySQL Docker Documentation: https://hub.docker.com/_/mysql
- Docker Compose Documentation: https://docs.docker.com/compose/
- PHP Documentation: https://www.php.net/docs.php

## License

This project is part of the GPEHEI Legacy suite.

## Support

For issues or questions, please refer to the individual README files in each service directory:
- `admin/README.md` - PHP application documentation
- `admin/README.Docker.md` - Docker-specific information
