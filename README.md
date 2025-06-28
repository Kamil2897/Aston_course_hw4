# User-Service (Spring-boot + PostgreSQL)

## Описание

Spring-приложение, работающее с REST-запросами и базой данных PostgreSQL.
Поддерживает базовые CRUD-операции над сущностью User.

## Запуск приложения

1. **Скачайте проект**
2. **Укажите параметры подключения к вашей PostgreSQL базе в 'src/main/resources/application.properties'**
3. **Запустите программу**
Базовый URL: 'http://localhost:8080/api/users'

Поддерживаемые запросы:

| Метод  | Путь     | Описание                      |
|--------|----------|-------------------------------|
| POST   | '/add/'  | Добавление нового пользователя|
| GET    | '/{id}'  | Получение пользователя по ID  |
| PUT    | '/{id}'  | Обновление данных пользователя|
| DELETE | '/{id}'  | Удаление пользователя         |

## Пример запроса (POST) в терминале при помощи curl в режиме запущенного приложения:

curl -uri "http://localhost:8080/api/users/add" -Method Post -Body '{"name":"Test","email":"Test@gmail.com","age":21}' -ContentType "application/json"

