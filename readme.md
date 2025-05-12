Создан проект, на Gradle. 

Сделан REST API для мониторинга питания.
<details> <summary>📁 <b>Структура проекта</b></summary>

```
src/main/java/com/example/foodmonitoring
├── config          # Конфигурации (Spring Security, Flyway)
├── controller      # REST-контроллеры
├── dto             # Объекты для передачи данных
├── entity          # JPA-сущности
├── repository      # Интерфейсы Spring Data JPA
├── service         # Бизнес-логика
├── util            # Вспомогательные классы
└── exception       # Обработка ошибок
```

- **config**: Содержит конфигурационные классы, такие как настройки безопасности и миграции базы данных.
- **controller**: REST-контроллеры для обработки HTTP-запросов.
- **dto**: Data Transfer Objects для передачи данных между слоями.
- **entity**: JPA-сущности, представляющие таблицы базы данных.
- **repository**: Интерфейсы для работы с базой данных через Spring Data JPA.
- **service**: Логика приложения, отделенная от контроллеров.
- **util**: Утилитарные классы и вспомогательные методы.
- **exception**: Классы для обработки и кастомизации ошибок.

</details>

<details> <summary>📁 <b>Использование шаблонов проектирования</b></summary>

1. **Singleton**: Используется для конфигурационных классов, таких как `DataSource` или `ApplicationConfig`, чтобы гарантировать единственный экземпляр в приложении.
2. **Factory**: Применяется для создания DTO-объектов, упрощая преобразование сущностей в объекты передачи данных.
3. **Builder**: Используется для создания сложных объектов, таких как профили пользователей или сложные запросы.
4. **DAO (Data Access Object)**: Для абстракции работы с базой данных, реализуется через Spring Data JPA.
5. **Service Layer**: Разделяет бизнес-логику и контроллеры, обеспечивая чистую архитектуру.
6. **Strategy**: Применяется для реализации различных алгоритмов, например, валидации данных или обработки запросов.

</details>

<details> <summary>📁 <b>Запуск проекта в Докер</b></summary>

### 1. Установка Docker
Если Docker еще не установлен:

- **Для Windows/Mac**: Скачайте и установите Docker Desktop.
- **Для Linux (Ubuntu/Debian)**:
  ```bash
  sudo apt-get update
  sudo apt-get install docker-ce docker-ce-cli containerd.io
  sudo systemctl enable docker
  sudo systemctl start docker
  ```

### 2. Подготовка `docker-compose.yml`

Пример файла `docker-compose.yml`:
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    container_name: foodmonitoring_postgres
    environment:
      POSTGRES_DB: foodmonitoring
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: 1234
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  app:
    image: vyntyk/food-monitoring:latest
    container_name: foodmonitoring_app
    depends_on:
      - postgres
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/foodmonitoring
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: 1234

volumes:
  pgdata:
```

### 3. Запуск проекта

Для запуска выполните команду:
```bash

docker-compose up -d
```

### 4. Проверка работы

Приложение будет доступно по адресу: [http://localhost:8080](http://localhost:8080).

</details>

<details> <summary>📁 <b>Технологии</b></summary>

- **Java 17**: Основной язык разработки.
- **Spring Boot 3**: Фреймворк для создания REST API.
- **Hibernate**: ORM для работы с базой данных.
- **PostgreSQL**: Реляционная база данных.
- **Flyway**: Для миграции базы данных.
- **Docker**: Контейнеризация приложения.
- **Gradle**: Система сборки.
- **Lombok**: Для сокращения шаблонного кода.
- **JUnit 5**: Для модульного тестирования.
- **Swagger/OpenAPI**: Для документирования API.

</details>

<details> <summary>📁 <b>Тестирование</b></summary>

### 1. Модульное тестирование
Используется **JUnit 5** и **Mockito** для тестирования бизнес-логики и REST-контроллеров.

### 2. Интеграционное тестирование
Проверка взаимодействия между слоями приложения с использованием встроенной базы данных H2.

### 3. Запуск тестов
Для запуска всех тестов выполните команду:
```bash

./gradlew test
```

Отчет о тестировании будет доступен в директории `build/reports/tests/test/index.html`.

</details>

<details> <summary>📁 <b>Контакты</b></summary>

- **Автор**: Виктор Белоус
- **Email**: почта 
- **GitHub**: [https://github.com/vyntyk](https://github.com/vyntyk)
- **Telegram**: [@VyktorB](https://t.me/VyktorB)

</details>

<details> <summary>📁 <b>Лицензия</b></summary>

Проект распространяется под лицензией **MIT**. Подробнее см. файл `LICENSE`.

</details>