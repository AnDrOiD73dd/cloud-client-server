# Cloud: client & server
Client and server side for cloud storage

# Протокол взаимодействия
Пример содержания пакета:
```
{
  "id": 1000,
  "request": {
    "cmd": "some cmd",
    "data": {
      "field1": "value1",
      "filed2": 100
    }
  },
  "responseCode": 200
}
```
Описание:
* id - это id пакета. Тот кто породил пакет - должен присовить ему ID, а тот кто ответил на пакет - должен вернуть ответ с этим же ID. 
* request - представляет собой набор параметров запроса, описанных ниже
    * cmd - Может принимать следующие значения:
      * signin - для запроса аутентификации. Требуемые поля: username, password. 
      * signout - для выхода из системы.
      * signup - для регистрации пользователя.
    * data - служит для передачи данных в запросе, относящихся к заданной команде в "cmd". Количество ключей и тип значений зависит от "cmd".
* responseCode (опционально, только для ответа на команду), код ответа на ранее присланную команду. Описание кодов:
    * signin
        * 0 - ошибок нет, принято в обработку
        * 1 - аутентификация пройдена успешно
        * 2 - неверный формат данных (неверный тип требуемого параметра или он отсутствует)
        * 3 - неверный логин/пароль
        * 4 - произошла внутренняя ошибка
    * signout
        * 0 - ошибок нет, принято в обработку
        * 1 - сенс пользователя успешно завершен
        * 3 - пользователь не проходил аутентификацию, сеанс не был запущен ранее
    * signup
        * 0 - ошибок нет, принято в обработку
        * 1 - успешно зарегистрирован
        * 2 - неверный формат данных (неверный тип требуемого параметра или он отсутствует)
        * 3 - произошла внутренняя ошибка при подключении
        * 4 - пользователь с таким именем уже существует
        * 5 - пользователь с таким email уже существует
    * file_add
        * 0 - ошибок нет, принято в обработку
        * 1 - файл успешно добавлен в БД
        * 2 - пользователь не аутентифицирован
        * 3 - произошла внутренняя ошибка
    * file_delete
        * 0 - ошибок нет, принято в обработку
        * 1 - файл успешно удален с сервера
        * 2 - пользователь не аутентифицирован
        * 3 - произошла внутренняя ошибка
    * file_list
        * 0 - ошибок нет, принято в обработку
        * 1 - неверный формат данных (неверный тип требуемого параметра или он отсутствует)

