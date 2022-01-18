FROM openjdk:8-alpine

COPY target/uberjar/tic-tac-toe.jar /tic-tac-toe/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/tic-tac-toe/app.jar"]
