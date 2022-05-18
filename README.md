# TomokiDB-Web

This is the web-app version of TomokiDB, a tool that can be used to store, organize and query weblinks.

**This project is still in the early stages of development on not yet ready for its intended use.**

Additional infos about this project that are not included in this README can be found here:

- [My design decisions](DesignDecisions.md)

## Running from source

### Requirements

- JDK 11 or higher
- Node.js
- NPM or Yarn

### How to run

Inside `/frontend` install the dependencies for the frontend with `npm install`
and then build it with `npm run build` (or use the Yarn equivalents for these).

Afterwards, run the backend Java-app inside `/backend` with `./mvnw spring-boot:run`

Alternatively, you can build the JAR file with `./mvnw clean package` and then run the JAR file with `java -jar <path-to-JAR-file>`
