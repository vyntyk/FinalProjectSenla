@echo off
echo # Combined Docker Configuration > docker-combined.yml
echo # This file contains both docker-compose.yml and Dockerfile configurations >> docker-combined.yml
echo # Note: This is a reference file only and not meant to be used directly with Docker >> docker-combined.yml
echo. >> docker-combined.yml
echo # ===================== DOCKER COMPOSE CONFIGURATION ===================== >> docker-combined.yml
type docker-compose.yml >> docker-combined.yml
echo. >> docker-combined.yml
echo # ===================== DOCKERFILE CONFIGURATION ===================== >> docker-combined.yml
type Dockerfile >> docker-combined.yml

echo Files have been combined into docker-combined.yml