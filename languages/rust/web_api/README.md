# Simple Rust Web API

This is a simple API made with Actix+SQLX for learning Rust, so is not ready for production, there's some [Todos](#todo-key-improvements) to improve it further. 
The API collection for testing is available under the `bruno` folder for use with [www.usebruno.com](https://www.usebruno.com).

---

## Table of Contents

1. [Introduction](#introduction)
2. [Codebase Context](#codebase-context)
3. [Installation Steps](#installation-steps)
4. [API Endpoints](#api-endpoints)
5. [Usebruno API Collection](#usebruno-api-collection)
6. [TODO: Key Improvements](#todo-key-improvements)
7. [Contribution](#contribution)
8. [License](#license)

---

## Introduction

This project is a backend API implemented in Rust using `actix-web` for building and handling RESTful endpoints, `sqlx` for database interaction, and JWT for authentication. The API provides functionalities for user management, categories, and transactions.

---

## Codebase Context

This project uses a modular structure with the following key components:

- **Controllers**: Handle HTTP requests and responses for various resources.
- **Database Module**: Contains database models and functions for interacting with PostgreSQL.
- **Middleware**: Provides JWT-based authentication for protected routes.
- **Migrations**: Contains SQL scripts for managing the database schema.

---

## Installation Steps

### 1. Install Rust
Install Rust using `rustup`:
```bash
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
```

Check installation:
```bash
rustc --version
```

### 2. Configure Environment Variables
Create a `.env` file with the following variables:

`DATABASE_URL=postgres://username:password@localhost/database_name JWT_SECRET=your_secret_key`

### 3. Install Dependencies
```bash
cargo install sqlx-cli --no-default-features --features native-tls,postgres
cargo build
```

### 4. Migrate the Database
Run migrations:
```bash
sqlx migrate run
```

### 5. Run the Application
Start the server:
```bash
cargo run
```
The application will be available at `http://127.0.0.1:8080`.

---

## API Endpoints

### Authentication
- **POST** `/auth/sign-up` | Sign up a new user.
- **POST** `/auth/sign-in` | Sign in and retrieve a JWT token.

### User Profile
- **GET** `/api/me` | Retrieve the profile of the signed-in user.
- **POST** `/api/me` | Update the profile of the signed-in user.

### Categories
- **GET** `/api/categories` | Retrieve all categories.
- **POST** `/api/categories` | Create a new category.
- **GET** `/api/categories/{category_id}` | Get details of a specific category.
- **PUT** `/api/categories/{category_id}` | Update a category.
- **DELETE** `/api/categories/{category_id}` | Delete a category.
- **GET** `/api/categories/{category_id}/transactions` | Retrieve transactions by category.

### Transactions
- **GET** `/api/transactions` | Retrieve all transactions.
- **POST** `/api/transactions` | Create a new transaction.
- **GET** `/api/transactions/{transaction_id}` | Retrieve a specific transaction.
- **PUT** `/api/transactions/{transaction_id}` | Update a transaction.
- **DELETE** `/api/transactions/{transaction_id}` | Delete a transaction.

---

## Usebruno API Collection

To simplify API testing, a collection is available under the `bruno` folder. Use it with [www.usebruno.com](https://www.usebruno.com):

1. Install Bruno as per their [documentation](https://www.usebruno.com).
2. Import the `bruno` folder from this repository.
3. Explore and test all API endpoints via Bruno’s user-friendly interface.

---

## TODO: Key Improvements

The following are planned areas of improvement for the project:

The following are planned areas of improvement for the project:

- [ ] **Error Handling**
    - Implement consistent error handling with custom error types.
    - Replace `unwrap()` and similar calls with safe error handling strategies.

- [ ] **Input Validation**
    - Add validation for incoming payloads (e.g., email, password strength, numeric constraints) using libraries like `validator`.

- [ ] **Environment Configurations**
    - Centralize configuration handling with a structured library such as `config`.
    - Add validation for required environment variables to fail-fast on misconfigurations.

- [ ] **Database Transactions**
    - Introduce transaction support for database operations needing atomicity.

- [ ] **Codebase Refactoring**
    - Separate business logic, request parsing, and response generation in controllers.
    - Modularize services and shared utilities for better code organization.

- [ ] **Testing Infrastructure**
    - Add unit tests for controllers and database functions.
    - Introduce integration testing with a dedicated test database.

- [ ] **Logging**
    - Implement structured request and error logging using libraries like `tracing` or `log`.

- [ ] **Role-Based Authorization**
    - Extend the JWT claims to include authorization roles (e.g., `admin`, `user`).
    - Enforce role-based access controls for sensitive operations.

- [ ] **Pagination**
    - Add support for paginated responses for category and transaction listing endpoints.

- [ ] **Security Enhancements**
    - Add CSRF protection for state-changing operations, where applicable.
    - Strengthen password policies for user sign-up.

- [ ] **OpenAPI Documentation**
    - Implement OpenAPI documentation generation (e.g., `utoipa`, `paperclip`) and host it under `/docs`.

- [ ] **Soft Deletes**
    - Add support for soft deletes (e.g., `is_deleted` flag) to allow recovering deleted records.

- [ ] **Rate Limiting**
    - Add rate-limiting middleware to protect against brute-force and abuse.

---

## License

This project is licensed under the MIT License. For more details, see the [LICENSE](./LICENSE) file.