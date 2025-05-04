@echo off
title Сборка и запуск проекта
echo Сборка Spring Boot приложения...
call gradlew build || (
  echo Ошибка сборки!
  pause
  exit /b 1
)

echo Запуск Docker-контейнеров...
docker-compose up --build
pause