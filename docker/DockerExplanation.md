# Guide to Using Docker Files

## File Structure

Here is a brief description of the Docker files included in this project:

- `Dockerfile`: Defines the Docker image for the Java Spring Boot application.
- `docker-compose-local-env.yml`: Docker Compose configuration for the local environment (API + DB).
- `docker-compose-db.yml`: Docker Compose configuration for MySQL and MongoDB databases.
- `docker-compose-api.yml`: Docker Compose configuration for the API application.
- `docker-compose-dev.yml`: Docker Compose configuration for the development environment.

## Instructions

If you want to run it with your version of the API, you first need to build the Docker image of the application, then
run the Docker containers using Docker Compose by changing the specified images. However, if you want to use the already
built application image, you can directly run the Docker containers using the image on DockerHub: *
*quentinformatique/path-pilotapi**.

### Build the Docker Image

To build the Docker image of the application, run the following command in the root directory of the project:

```sh
docker build -t path-pilotapi .
```

### Environment Variables

For the application to work correctly, it is necessary to define the following environment variables with a `.env` file
at the root of the project:


```sh
MYSQL_HOST # Hostname for MySQL database (e.g., path-pilot-db-8 if using local environment)
MYSQL_PORT # Port number for MySQL database (default: 3306)
MYSQL_DATABASE # Name of the MySQL database (e.g., path-pilot)
MYSQL_USER # Username for MySQL database (e.g., pathpilot)
MYSQL_PASSWORD # Password for MySQL database
MYSQL_ROOT_PASSWORD # Root password for MySQL database

# MONGO configuration
MONGO_HOST # Hostname for MongoDB (e.g., mongodb if using local environment)
MONGO_PORT # Port number for MongoDB (default: 27017)
MONGO_INITDB_ROOT_USERNAME # Root username for MongoDB
MONGO_INITDB_ROOT_PASSWORD # Root password for MongoDB
MONGO_INITDB_DATABASE # Name of the MongoDB database (new variable)

# JWT configuration
JWT_SECRET_KEY # Secret key for JWT authentication
```



### Complete Local Environment

To launch the complete local environment, run the following command:

```sh
docker-compose -f docker-compose-local-env.yml up
```

### Development Environment of the API

To launch the development environment, run the following command:

```sh
docker-compose -f docker-compose-dev.yml up
```

### Local Databases

To launch the MySQL and MongoDB databases locally, run the following command:

```sh
docker-compose -f docker-compose-db.yml up
```

### Local API

To launch the API application locally, run the following command

```sh
docker-compose -f docker-compose-api.yml up
```
