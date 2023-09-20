# 🤝ShareIt
### Бэкенд простого приложения для шеринга* личных вещей. 

## Приложение состоит из трех микросервисов:
### 🚪 Gateway
Гейтвей, принимающий все запросы и осуществляющий базовую (не задействующую бизнес логику) валидацию на корректность
запроса. Если запрос корректен, направляет его на сервер, а затем транслирует ответ, не меняя его.

### 💻 Server
Сервер принимает запросы от гейтвея, запускает бизнес логику, а также общается с базой данных и возвращает результат
гейтвею.

### 💾 DB
База данных хранит все данные приложения.

**Шеринг - это совместное, коллективное использование и потребление различных вещей и услуг.*
___

### ⭐ Возможности приложения:
* Пользователи могут оставлять запросы на вещь, которая им нужна.
* Пользователи могут выставлять свои личные вещи для шеринга. Вещи можно выставлять как в ответ на запрос, так и без него.
* Каждый пользователь может забронировать вещь на определенный срок. Не допускается:
    * пересечения бронирований;
    * бронирование "задним числом";
    * окончание бронирования раньше его начала.
* Каждый пользователь может посмотреть список своих вещей, а также последнее и следующее бронирование.
* Каждый пользователь может посмотреть список своих бронирований, а также список бронирований его вещей со следующими статусами:
    * ALL - все бронирования;
    * CURRENT - текущий бронирования;
    * PAST - прошедшие бронирования;
    * FUTURE - предстоящие бронирования;
    * WAITING - ожидающие ответа владельца вещи;
    * REJECTED - отклоненные бронирования.
* После окончания бронирования, пользователь может оставить комментарий о вещи.
___
### ⚙️ Технологический стек
* Java 11
* Spring Boot
* Hibernate
* Junit
* Mockito
* Lombok
* PostgresSQL
* Maven
* Docker Compose
___
### 📃 Инструкция по запуску:
Скачать проект.\
Убедитесь, что порт 8080 свободен.

Собрать проект:
```shell
docker compose build
```
Запустить проект:
```shell
docker compose up
```
