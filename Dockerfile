FROM eclipse-temurin:25-jdk AS build
WORKDIR /src
COPY src/ src/
RUN javac -d /out src/*.java

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /out/ /app/
ENV PORT=8080
EXPOSE 8080
USER 1000:1000
CMD ["java", "-cp", "/app", "Server"]