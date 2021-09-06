## Tech Stack:
- Open JDK 11
- [Micronaut Web Framework]((https://docs.micronaut.io/2.5.1/guide/index.html))
- [Postgres](https://www.postgresql.org/docs/12/index.html) database
- [Liquibase](https://www.liquibase.org/get-started/quickstart) migration library
- [Testcontainers](https://www.testcontainers.org/) for database integration test
- [Docker](https://docs.docker.com/get-started/overview/)

- Install JDK:
  `$brew cask install adoptopenjdk/openjdk/adoptopenjdk11`

- Setup JAVA_HOME: Follow [these](https://www.appsdeveloperblog.com/how-to-set-java_home-on-mac/) steps.
- Select Gradle JVM Version 11.08

## Steps to run locally:
- Install postgres(optional), docker
- Run the command : `docker compose up -d`
    - First time this takes time to download the docker image and get the postgresql up.
- Build: `./gradlew clean build`
- Build with lint: `./gradlew clean buildWithLint`
- Run application: `./gradlew run`. App would run on `http://localhost:9000`
 - Apply norm changes: Run `./gradlew norm`
 - Test application using `http://localhost:9000/api/movies`
- Run application in watch mode: `./gradlew run -t`
    - This will restart the server on code changes

## Micronaut 2.5.1 Documentation

- [User Guide](https://docs.micronaut.io/2.5.1/guide/index.html)
- [API Reference](https://docs.micronaut.io/2.5.1/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/2.5.1/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
