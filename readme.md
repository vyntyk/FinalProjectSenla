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