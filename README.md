# MediCare API

A production-grade Patient Appointment and Records Management System built with Java and Spring Boot.

## Overview

MediCare API is a RESTful backend system for managing patients, doctors, appointments, and medical records in a healthcare environment. Built to demonstrate real-world Java backend engineering practices including JWT security, event-driven caching, and containerised deployment.

## Tech Stack

- **Java 17** with **Spring Boot 3.3**
- **Spring Data JPA** + **Hibernate** — ORM and database management
- **Spring Security** + **JWT** — stateless authentication and role-based access
- **Redis** — caching for high-traffic read endpoints
- **H2** — in-memory database for development
- **Docker** + **Docker Compose** — containerised deployment
- **Lombok** — boilerplate reduction
- **Maven** — dependency management

## Features

- Patient management — full CRUD with validation
- Doctor management — specialization-based filtering
- Appointment booking — conflict detection prevents double-booking
- Medical records — patient history linked to appointments
- JWT authentication — register, login, token-based access
- Role-based access control — ADMIN, DOCTOR, PATIENT roles
- Redis caching — with automatic cache invalidation on updates
- Global exception handling — structured error responses
- Dockerised — runs with a single command

## Project Structure

## Getting Started

### Prerequisites

- Docker and Docker Compose installed

### Run With Docker

```bash
git clone https://github.com/samson30/medicare-api.git
cd medicare-api
docker-compose up --build
```

The API will be available at `http://localhost:8080`

### Run Locally

```bash
mvn spring-boot:run
```

H2 Console available at `http://localhost:8080/h2-console`

## API Endpoints

### Authentication (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/v1/auth/register | Register a new user |
| POST | /api/v1/auth/login | Login and get JWT token |

### Patients (Authenticated)

| Method | Endpoint | Role Required |
|--------|----------|--------------|
| POST | /api/v1/patients | ADMIN, DOCTOR |
| GET | /api/v1/patients | ADMIN, DOCTOR, PATIENT |
| GET | /api/v1/patients/{id} | ADMIN, DOCTOR, PATIENT |
| PUT | /api/v1/patients/{id} | ADMIN, DOCTOR |
| DELETE | /api/v1/patients/{id} | ADMIN |

### Doctors (Authenticated)

| Method | Endpoint | Role Required |
|--------|----------|--------------|
| POST | /api/v1/doctors | ADMIN |
| GET | /api/v1/doctors | All authenticated |
| GET | /api/v1/doctors/{id} | All authenticated |
| GET | /api/v1/doctors/specialization/{spec} | All authenticated |
| PUT | /api/v1/doctors/{id} | ADMIN |
| DELETE | /api/v1/doctors/{id} | ADMIN |

### Appointments (Authenticated)

| Method | Endpoint | Role Required |
|--------|----------|--------------|
| POST | /api/v1/appointments | ADMIN, PATIENT |
| GET | /api/v1/appointments | All authenticated |
| GET | /api/v1/appointments/{id} | All authenticated |
| GET | /api/v1/appointments/patient/{id} | All authenticated |
| GET | /api/v1/appointments/doctor/{id} | All authenticated |
| PUT | /api/v1/appointments/{id}/cancel | All authenticated |
| PUT | /api/v1/appointments/{id}/complete | ADMIN, DOCTOR |

### Medical Records (Authenticated)

| Method | Endpoint | Role Required |
|--------|----------|--------------|
| POST | /api/v1/records | ADMIN, DOCTOR |
| GET | /api/v1/records/{id} | All authenticated |
| GET | /api/v1/records/patient/{id} | All authenticated |
| GET | /api/v1/records/doctor/{id} | ADMIN, DOCTOR |
| DELETE | /api/v1/records/{id} | ADMIN |

## Authentication

All protected endpoints require a JWT token in the Authorization header:

Get a token by registering or logging in via the auth endpoints.

## Design Decisions

**Why JWT over sessions?** JWT is stateless — no server-side storage needed. Scales across multiple instances in a microservices environment.

**Why Redis caching?** Doctor lookups are read-heavy. Caching reduces database load on frequently accessed endpoints. Cache is invalidated on every update to prevent stale data.

**Why conflict detection in Java rather than SQL?** Database-agnostic approach works with H2, PostgreSQL, and MySQL without dialect-specific functions. Easier to test and reason about.

## Author

Samson Kanthiah Prem Kumar — [LinkedIn](https://www.linkedin.com/in/samson-kanthiah-prem-kumar-a1086a19b/)