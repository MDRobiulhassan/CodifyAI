# CodifyAI

CodifyAI is an AI-powered application builder backend. Users can describe an application in natural language, manage generated projects and files, chat with an AI model, deploy projects, and manage subscriptions.

The backend is built with Spring Boot, Java, PostgreSQL, Redis, MinIO, Spring AI, JWT, Stripe, and Kubernetes integration.

## Features

- User signup, login, and authenticated profile access
- AI chat with streaming responses
- Project creation, updates, deletion, and deployment
- Project file listing and content retrieval
- Project member management
- Usage tracking
- Subscription plans and Stripe checkout
- Object storage through MinIO
- PostgreSQL with pgvector support
- Kubernetes-based project execution and deployment
- OpenAPI documentation through Swagger UI

## Technology stack

- Java 17
- Spring Boot 4
- Spring MVC and WebFlux
- Spring Data JPA
- PostgreSQL
- Redis
- MinIO
- Spring AI with an OpenAI-compatible provider
- JWT authentication
- Stripe
- Maven
- Docker
- Kubernetes

## Requirements

Install the following tools before running the project:

- JDK 17
- Maven, or use the included Maven Wrapper
- Docker Desktop
- Docker Compose
- PostgreSQL with pgvector
- Redis
- MinIO

## Configuration

The default configuration reads sensitive values from environment variables. Do not commit API keys, passwords, JWT secrets, or cloud credentials to the repository.

Required environment variables:

```text
DB_URL=jdbc:postgresql://localhost:9010/<database-name>
DB_USERNAME=<database-user>
DB_PASSWORD=<database-password>
JWT_SECRET=<long-random-secret>
OPENAI_API_KEY=<openrouter-or-openai-api-key>
STRIPE_SECRET_KEY=<stripe-secret-key>
STRIPE_WEBHOOK_SECRET_KEY=<stripe-webhook-secret>
MINIO_URL=http://localhost:9000
MINIO_ACCESS_KEY=<minio-access-key>
MINIO_SECRET_KEY=<minio-secret-key>
MINIO_PROJECT_BUCKET=<bucket-name>
```

The application uses port `8080` by default. Set `PORT` to use another port.

For local development, create a `.env` file for Docker Compose and provide the PostgreSQL and MinIO values used by `services.docker-compose.yml`. Keep that file local and confirm it is ignored by Git.

## Start supporting services

Start PostgreSQL with pgvector and MinIO:

```bash
docker compose -f services.docker-compose.yml up -d
```

The services use these local ports:

| Service | Address |
| --- | --- |
| PostgreSQL with pgvector | `localhost:9010` |
| MinIO API | `localhost:9000` |
| MinIO console | `localhost:9001` |

Redis is configured at `localhost:6379` and must be running separately.

## Run the application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

To build and run the packaged application:

```bash
./mvnw clean package
java -jar target/CodifyAI-0.0.1-SNAPSHOT.jar
```

The application will be available at:

```text
http://localhost:8080
```

## Docker

Build the application image:

```bash
docker build -t codifyai:latest .
```

Run the container:

```bash
docker run --rm -p 8080:8080 --env-file .env codifyai:latest
```

The container accepts `JAVA_OPTS` for JVM options and `PORT` for the application port.

## API documentation

When the application is running, open Swagger UI at:

```text
http://localhost:8080/swagger-ui/index.html
```

The main API areas are:

| Area | Base path |
| --- | --- |
| Authentication | `/api/auth` |
| Projects | `/api/projects` |
| Project members | `/api/projects/{projectId}/members` |
| Project files | `/api/projects/{projectId}/files` |
| AI chat | `/api/chat` |
| Billing and plans | `/api` |
| Usage | `/api/usage` |

## Dynamic project proxy

The optional Node.js proxy routes local project subdomains to the project runtime service on port `8090`.

Install the proxy dependencies and start it:

```bash
cd proxy
npm install
node index.js
```

The proxy listens on port `8091`. A project can then be accessed with a host such as:

```text
http://project-123.localhost:8091
```

The proxy expects the project runtime service to be available on `localhost:8090`.

## Kubernetes

The `k8s` directory contains manifests for the application infrastructure, proxy, runtime pods, and related policies. Review the manifests and configure the required cluster secrets before applying them to a Kubernetes cluster.

## Testing

Run the test suite with the Maven Wrapper:

On Windows:

```powershell
.\mvnw.cmd test
```

On macOS or Linux:

```bash
./mvnw test
```

## Project structure

```text
src/main/java/com/codifyai/
  config/       Application and infrastructure configuration
  controller/   REST API controllers
  dto/          Request and response models
  entity/       JPA entities
  repository/   Data access repositories
  security/     Authentication and authorization
  service/      Service interfaces
  serviceimpl/  Service implementations
  llm/          AI model integration

src/main/resources/
  application.yaml        Environment-based configuration
  application-local.yaml  Local development configuration

proxy/                    Dynamic project proxy
k8s/                      Kubernetes manifests
documentation/            API, architecture, and project documentation
```

## Security notes

- Use environment variables or a secret manager for all credentials.
- Rotate any credential that has been exposed or committed previously.
- Use a strong, randomly generated JWT secret.
- Configure Stripe webhook signature verification before processing payments.
- Review CORS, database access, object storage access, and Kubernetes permissions before production deployment.

## License

This project is intended for educational and development purposes. Review and add an appropriate license before distributing it.
