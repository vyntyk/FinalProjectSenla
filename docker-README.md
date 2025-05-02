# Docker Configuration

This directory contains the Docker configuration for the Food Monitoring application.

## Configuration Files

- **docker-compose.yml**: Defines the services required for the application (PostgreSQL database and the application itself)
- **Dockerfile**: Defines how to build the application container

## Combined Configuration

For reference purposes, we've provided several ways to view the combined Docker configuration:

1. **docker-combined.md**: A Markdown file that contains both configurations in a readable format
2. **docker-combined.bat**: A Windows batch script that combines both files into a single text file
3. **docker-combined.sh**: A Unix/Linux shell script that combines both files into a single text file

## How to Use

1. Make sure Docker and Docker Compose are installed on your system
2. Navigate to the project root directory
3. Run `docker-compose up` to start the services

## Notes

- The PostgreSQL database is configured with the following credentials:
  - Database: food_monitoring
  - Username: postgres
  - Password: 1234
- The application is accessible at http://localhost:8080
- Data is persisted in a Docker volume named "pgdata"