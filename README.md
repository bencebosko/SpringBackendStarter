<p align="center">
    <img src="https://github.com/user-attachments/assets/6a71a3a4-4fae-47c8-8904-8023d1655b38" width="500" alt="project-logo"/>
</p>

**Starter project** to build backend services with the [Spring Framework](https://spring.io/projects/spring-framework). The repository contains a simple but **easily extendable** 3-layered architecture, implementing **core features** that are neccessary in every web application. With this project its easy to spin up simpler backend applications. 

:warning: This is an independent open-source project and is <ins>not officially maintained by the Spring Team<ins>.

**Test coverage**: ~100% (with Postman tests)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-blue)

## Overview
Standard 3-layered backend application using a **REST API** for communication. The application is running in a **Docker environment**, but it can be also setup without docker.

## Supported features
* **Two-Factor Authentication** with email verification
* **Authorization** with multiple Roles
* Measures to handle **OWASP** security threats
* **Email sending** and persistence
* Key based **rate limiting**
* **PostgreSQL** database
* Global exception handling
* **Translation** loading from JSON files
* **Containerization** with Docker
* Testing environment

## REST endpoints
In this section, there is a brief description of the current endpoints. The **exact API** can be imported from the `api-test.postman.json` file or viewed in the source code. (the `baseUrl` variable must be set in Postman)

### Auth Resource
Resource used for **authenticating** users.

| Endpoint | Method   | Description | Request Body |
|----------|----------|---------------|--------------|
| **/auth/authenticate** | **POST** | Authenticates users. Sends login verification email if enabled. | `usernameOrEmail` , `password` |
| **/auth/login** | **POST** | Logs users in with the verification code. Generates access and refresh token cookies. | `usernameOrEmail` , `password`, `verificationCode` |
| **/auth/logout** | **POST** | Removes the refresh token from the database. Invalidates access and refresh token cookies. | |
| **/auth/refresh** | **GET**  | Refreshes the access token. Refresh token cookie must be set in the request. | |

### User Resource
Resource for **writing** and **reading users**. Some operations require confirmation password, which must match the password of the authenticated user.

| Endpoint | Method | Role | Description | Request Body |
|----------|------|---------------|------|--------------|
| **/users/me** | **GET** | `AUTHENTICATED_USER` | Returns the current authenticated user. | |
| **/admin/users/** | **POST** | `ADMIN` | Creates a user with role `AUTHENTICATED_USER` and `SIMPLE_USER`. | `userRequest`, `confirmationPassword` |
| **/admin/users/** | **PATCH** | `ADMIN` | Partially updates a user with the given username. | `username`, `userPatch`, `confirmationPassword`|
| **/admin/users/** | **DELETE** | `ADMIN` | Deletes a user with the given username. | `username`, `confirmationPassword` |

## Email templates
<p align="left">
  <img src="https://github.com/user-attachments/assets/254db2cf-9215-4fd3-90f1-d486d4a397dc" width="400px" alt="login-verification" />
</p>

## Setting up the application
The section describes how to **setup**, **configure** and **start** the application with its dependencies.

### Requirements
* **Docker installed**
* **Java 21**
* **Maven**
* Shell (commands in the `.sh` scripts can be run in command line)
* Postman (optional)

### Certificate
A self-signed certificate must be generated to enable TLS communication.

* To generate the certificate, navigate to the base directory
* Run the `generate-cert.sh` script. Do not forget to give a **custom password** with the `-storepass` switch.
* If the certificate is successfully generated, put it in the `src/main/resources/cert` directory.

### Environment variables
For setting the environment variables, do the following steps:

* Navigate to the `docker` directory
* Copy the `.env.sample` file and paste it as **.env**
* Set your own environment variables
* Pass the same variables to the application runner (if you are using IntelliJ the `.env` file can be directly imported at the run configurations)

All environment variables the application is currently using:

| Name | Description |
|------|-------------|
| `SPRING_ACTIVE_PROFILE` | The application will be started in this profile. |
| `HOST` | Host machine where the application is running. (usually `localhost` or `host.docker.internal`) |
| `SERVER_PORT` | Port where the API is available after startup. |
| `DEFAULT_USER` | Root username (the user will be created with `ADMIN` role) |
| `DEFAULT_PASSWORD` | Root user password |
| `DEFAULT_EMAIL` | Email of the root user. Login verification will be sent to this address. |
| `TOKEN_ENCRYPTION_KEY` | Access and refresh tokens will be encrypted with this key. |
| `POSTGRES_PORT` | Port where postgres will be mapped on the host. |
| `POSTGRES_USER` | Postgres root username |
| `POSTGRES_PASSWORD` | Postgres root password |
| `POSTGRES_DB` | Name of the default database |
| `SMTP_HOST` | Host of the SMTP server |
| `SMTP_PORT` | Port where the SMTP server is available |
| `SMTP_USER` | Username to authenticate with the SMTP server |
| `SMTP_PASSWORD` | Password for the authentication |
| `CERT_PASSWORD` | Store password of the certificate |

### Configuration properties
To use your own configurations:

* Navigate to the `resources` directory
* Set your configuration properties in the `application-*.yml` files

Some of the currently used configuration properties:

| Name | Description |
|------|-------------|
| `hikari.maximum-pool-size` | Maximum concurrent connections with the database |
| `hikari.minimum-idle` | Minimum connections in the of the connection pool kept alive |
| `login-verification-enabled` | Turns on Two-Factor Authentication. |
| `token.encryption-key` | Access and Refresh tokens will be encrypted with this key. |
| `token.refresh-token-expiration-days` | Refresh token will be valid until this date. |
| `token.access-token-validity-minutes` | Short-term validity for Access tokens |
| `mail.from-address` | Email address of the sender |
| `mail.login-verification-processing-cron` | Processing and sending rate of the login verification mails. (every 5 seconds by default) |
| `rate-limiter.requests-per-username.request-count` | Username based rate limiters will allow this number of request with the same username. |
| `rate-limiter.requests-per-username.time-interval-seconds` | Username based rate limiters will use this interval for allowed requests. |
| `rate-limiter.requests-per-token.request-count` | Token based rate limiters will allow this number of request with the same token. |
| `rate-limiter.requests-per-token.time-interval-seconds` | Token based rate limiters will use this interval for allowed requests. |
| `rate-limiter.cache-size.request-count` | The number of different keys (username or token currently) rate limiters can store at the same time. |
| `rate-limiter.cache-expiration-seconds` | Unused keys are automatically removed from the rate limiter caches. |

### Starting the application
After starting postgres, the application will be ready to start:

* Navigate to the `docker` directory.
* Build your **postgres** database image with the `services/postgres/build.sh` script
* Start the postgres container in the **docker-compose** file
* When the postgres container is running, you can start the application by running the `SpringBackendApplication` runner class.
* The application is listening on the configured host and port

### Containerization
To dockerize the application do the following steps:

* Package your application running `mvn clean package`
* Copy the **jar file** from the `target` directory into the `docker/services/spring-backend` directory
* Open the `Dockerfile` and set your own environment variables and arguments
* Run the `build.sh` script to build the image (the name of the image can be changed in the script)
  
## Customization
### Renaming and refactoring classes
First you should **rename** a few entities in the project:

* Images and containers in the `docker` directory
* The project in the `pom.xml`
* The package structure
* Every Class with the `SpringBackend.*` pattern
* Prefix of the configuration properties
* `SPRING_BACKEND_ERROR` constant

### Translations
To configure or add new translations:

* Navigate to the `resources/translations` directory
* Open the `en-US.json` translation file and change the translation values
* **Translation params** can be used with the `{{parameterName}}` syntax
* Add new translation files using their **locale** as file name

The content of the translation file currently:
```
{
    "mail.all.header.greetings": "Dear {{firstName}},",
    "mail.all.footer.goodbye": "Best wishes,",
    "mail.all.footer.signature": "BackendStarter Team",

    "mail.login-verification.subject.subject": "BackendStarter - {{verificationCode}}",
    "mail.login-verification.body.first-paragraph": "We have received a login request with the username",
    "mail.login-verification.body.second-paragraph": "Please verify your login with the following verification code:"
}

```
