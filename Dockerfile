FROM maven:3.8.8-eclipse-temurin-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -q package -DskipTests
FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /app/target/loja-ecommerce.jar ./loja-ecommerce.jar
COPY --from=build /app/target/lib ./lib
ENTRYPOINT ["java", "-jar", "loja-ecommerce.jar"]
CMD ["api"]
