# Guide to Using Docker Files

## File Structure

Here is a brief description of the Docker files included in this project:

- `Dockerfile`: Defines the Docker image for the Java Spring Boot application.
- `docker-compose-local-env.yml`: Docker Compose configuration for the local environment (API + DB).
- `docker-compose-db.yml`: Docker Compose configuration for MySQL and MongoDB databases.
- `docker-compose-api.yml`: Docker Compose configuration for the API application.
- `docker-compose-dev.yml`: Docker Compose configuration for the development environment.

## Instructions

If you want to run it with your version of the API, you first need to build the Docker image of the application, then run the Docker containers using Docker Compose by changing the specified images. However, if you want to use the already built application image, you can directly run the Docker containers using the image on DockerHub: **quentinformatique/path-pilotapi**.

### Build the Docker Image

To build the Docker image of the application, run the following command in the root directory of the project:

```sh
docker build -t path-pilotapi .
```

### Environment Variables

For the application to work correctly, it is necessary to define the following environment variables with a `.env` file at the root of the project:

```.env
MYSQL_HOST={your_mysql_host} # if you are using the local environment, use the service name defined in the docker-compose file (ex: path-pilot-db-8)
MYSQL_PORT={your_mysql_port}
MYSQL_DATABASE={your_mysql_database}
MYSQL_USER={your_mysql_user}
MYSQL_PASSWORD={your_mysql_password}
MYSQL_ROOT_PASSWORD={your_mysql_root_password}
MONGO_HOST={your_mongo_host} # if you are using the local environment, use the service name defined in the docker-compose file (ex: mongodb)
MONGO_PORT={your_mongo_port}
MONGO_DATABASE={your_mongo_database}
MONGO_INITDB_ROOT_USERNAME={your_mongo_user}
MONGO_INITDB_ROOT_PASSWORD={your_mongo_password}
JWT_SECRET_KEY={your_jwt_secret_key}
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
