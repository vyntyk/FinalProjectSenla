Создан проект, на Gradle. 

Инициализирован Git репозиторий.

<details> <summary>📁 <b>Структура проекта</b></summary>

src/main/java/com/example/foodmonitoring

├── config          // Конфигурации (Spring Security, Flyway)

├── controller      // REST-контроллеры

├── dto             // Объекты для передачи данных

├── entity          // JPA-сущности

├── repository      // Интерфейсы Spring Data JPA

├── service         // Бизнес-логика

├── util            // Вспомогательные классы

└── exception       // Обработка ошибок
</details>

<details> <summary>📁 <b>Использование шаблонов проектирования</b></summary>

1. Singleton: Для конфигурации (например, DataSource).
2. Factory: Для создания DTO-объектов.
3. Builder: Для удобного создания сложных объектов (например, профиля пользователя).
4. DAO (Data Access Object): Для работы с базой данных.
5. Service Layer: Для разделения бизнес-логики и контроллеров.
</details>

<details> <summary>📁 <b>Запуск проекта в Докер</b></summary>

### 1. Установка Docker
Если Docker еще не установлен:

**Для Windows/Mac**: 
Скачайте и установите Docker Desktop

**Для Linux (Ubuntu/Debian)**:
   - sudo apt-get update
   - sudo apt-get install docker-ce docker-ce-cli containerd.io
   - sudo systemctl enable docker
   - sudo systemctl start docker

### 2. Скачивание образа с Docker Hub

`docker pull vyntyk/food-monitoring:latest`

### 3. Подготовка docker-compose.yml

- services:
- postgres:
- image: postgres:15
- container_name: foodmonitoring_postgres
- environment:
- POSTGRES_DB: foodmonitoring
- POSTGRES_USER: postgres
- POSTGRES_PASSWORD: 1234
- ports:
- "5432:5432"
- volumes:
- pgdata:/var/lib/postgresql/data

- app:
- image: vyntyk/food-monitoring:latest  # Используем скачанный образ
- container_name: foodmonitoring_app
- depends_on:
- postgres
ports:
- "8080:8080"
- environment:
- SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/foodmonitoring
- SPRING_DATASOURCE_USERNAME: postgres
- SPRING_DATASOURCE_PASSWORD: 1234

- volumes:
- pgdata:


### 4. Запуск проекта

`docker-compose up -d`

### 5. Проверка работы

Приложение будет доступно по адресу: http://localhost:8080
</details>