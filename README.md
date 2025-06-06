# 🧩 Project Tracker API

A full-stack task and project management REST API built using **Spring Boot**, **PostgreSQL**, **MongoDB (for audit logging)**, with **caching**, **Swagger UI**, **pagination**, and **sorting**.

---

## 🧰 Tech Stack

- **Backend**: Java 17, Spring Boot
- **Database**: PostgreSQL (Projects, Tasks, Developers, Skills)
- **Audit Logs**: MongoDB
- **Docs**: Swagger UI
- **DevOps**: Docker Compose
- **Cache**: Spring Cache (Simple in-memory)
# 📌 Project Tracker API

A task and project management API built with **Spring Boot**, **PostgreSQL**, **MongoDB (for logs)**, with support for **caching**, **pagination**, **sorting**, **audit logs**, and **Swagger UI**.

---

## 🧰 Tech Stack

* Java 17+
* Spring Boot
* PostgreSQL (Project data)
* MongoDB (Audit Logs)
* Spring Data JPA + MongoTemplate
* Spring Cache (Simple in-memory caching)
* Swagger UI (API docs)
* Docker Compose

---

## 🚀 Getting Started

### 🐳 Docker Compose Setup

Ensure you have Docker and Docker Compose installed on your system.

Create a file named `docker-compose.yml` in your project root and paste the following content:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16
    container_name: mypostgres
    restart: always
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: mysecretpassword
      POSTGRES_DB: projecttracker
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  mongo:
    image: mongo:6
    container_name: mymongo
    restart: always
    environment:
      MONGO_INITDB_ROOT_USERNAME: # You can set these if you need authentication
      MONGO_INITDB_ROOT_PASSWORD: # Leave empty for no auth by default
    ports:
      - "27017:27017"
    volumes:
      - mongo-data:/data/db

  app:
    build: .
    container_name: springboot-app
    depends_on:
      - postgres
      - mongo
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/projecttracker
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: mysecretpassword
      SPRING_DATA_MONGODB_URI: mongodb://mongo:27017/mydb
volumes:
  postgres-data:
  mongo-data:
  ```

## Run the App

Navigate to your project root in your terminal and run:
docker-compose up --build