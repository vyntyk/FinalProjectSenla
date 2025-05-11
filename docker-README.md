# Docker Configuration

This directory contains the Docker configuration for the Food Monitoring application.

## Configuration Files

- **docker-compose.yml**: Defines the services required for the application (PostgreSQL database and the application itself)
- **Dockerfile**: Defines how to build the application container

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