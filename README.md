## Порядок запуска 

1. Запустить Docker Desktop

2. В терминале IntelliJ IDEA выполнить команду "docker-compose up"

3. В терминале IntelliJ IDEA выполнить команду для запуска приложения:
- для MySQL:
  `java -jar .\aqa-shop.jar --spring.datasource.url=jdbc:mysql://localhost:3306/app`

- для Postgres:
  `java -jar .\aqa-shop.jar --spring.datasource.url=jdbc:postgresql://localhost:5432/app`

4. В терминале IntelliJ IDEA выполнить команду для прогона тестов.

- для MySQL:
  ` .\gradlew clean test -D dbUrl=jdbc:mysql://localhost:3306/app -D dbUser=app -D dbPass=pass`
- для Postgres:
  `.\gradlew clean test -D dbUrl=jdbc:postgresql://localhost:5432/app -D dbUser=app -D dbPass=pass`

5. В терминале выполнить команду для получения отчета:
   `.\gradlew allureServe `

