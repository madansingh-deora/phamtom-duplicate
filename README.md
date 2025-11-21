📌 Phantom Duplicate — Idempotent Submission API (Spring Boot + PostgreSQL + Docker)

This project demonstrates how to build an idempotent, high-concurrency submission API using
Java 17
Spring Boot
PostgreSQL
Docker Compose
PostgreSQL advisory locks to prevent duplicate inserts
Database-level unique constraints
Structured logging with request IDs

It solves the classic “phantom duplicate” problem that occurs when multiple workers process the same event at the same time, causing duplicate DB inserts despite unique keys in code.

🐳 Running the Project with Docker Compose

1️⃣ Build & start services
docker compose up --build -d
This:
Builds the Spring Boot app
Starts PostgreSQL
Starts the app container

2️⃣ Check running containers
docker compose ps

3️⃣ View logs
docker compose logs -f app

4️⃣ Stop containers
docker compose down

🔍 Testing the API
Run this sample cURL:
curl -X POST http://localhost:8080/submit ^
  -H "Content-Type: application/json" ^
  -d "{ \"submissionId\":\"batch-100\", \"payload\": {\"invoice\":1} }"


Expected:
First request → 201 Created
Next requests → 200 OK with status "already_exists"
