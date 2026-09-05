# ⚡ CodeForge — Online Coding Practice Platform & Judge Engine

A high-performance, full-stack online coding practice platform and automated judge engine built with **Java 17**, **Spring Boot 3**, **Spring Security 6 (JWT)**, **MySQL**, and **React (Vite + Vanilla CSS)**.

Designed with clean layered architecture, dynamic SQL criteria filtering, isolated subprocess code execution for Java & Python, database-level analytics, and an administrative portal.

---

## 📑 Table of Contents
- [1. System Architecture](#1-system-architecture)
- [2. Key Features](#2-key-features)
- [3. Technology Stack](#3-technology-stack)
- [4. System Workflows](#4-system-workflows)
- [5. Database Schema & Indexing](#5-database-schema--indexing)
- [6. REST API Documentation](#6-rest-api-documentation)
- [7. Getting Started & Setup](#7-getting-started--setup)
- [8. Environment Variables](#8-environment-variables)
- [9. Running the Application](#9-running-the-application)
- [10. Automated & Manual Testing](#10-automated--manual-testing)
- [11. User Interface & Visual Showcase](#11-user-interface--visual-showcase)
- [12. Roadmap & Future Enhancements](#12-roadmap--future-enhancements)

---

## 1. System Architecture

CodeForge uses a decoupled client-server architecture with an asynchronous judge worker pool:

```
┌─────────────────────────────────────────────────────────────┐
│                   React 19 Frontend (SPA)                   │
│   (Vite + React Router + Axios Interceptors + Vanilla CSS)  │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTPS / JSON (JWT Bearer Token)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                 Spring Boot 3 REST API Layer                │
│   ┌───────────────────────┬─────────────────────────────┐   │
│   │  Spring Security 6    │  Global Exception Advice    │   │
│   │  (Stateless + RBAC)   │  (Unified ApiResponse / DTO)│   │
│   └───────────────────────┴─────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────┐   │
│   │  Service Layer (Business Logic & Transactions)      │   │
│   └───────────────────────┬─────────────────────────────┘   │
│   ┌───────────────────────┴─────────────────────────────┐   │
│   │  Spring Data JPA / Hibernate (Criteria + Indexing)  │   │
│   └───────────────────────┬─────────────────────────────┘   │
└───────────────────────────┼─────────────────────────────────┘
                            │
            ┌───────────────┴───────────────┐
            ▼                               ▼
┌───────────────────────┐       ┌───────────────────────────────┐
│     MySQL 8.0 DB      │       │     Judge Worker Pool         │
│ (Indexed Relational)  │       │   ┌───────────────────────┐   │
│  - users              │       │   │  ProcessBuilder       │   │
│  - problems           │       │   │  Isolated Sandbox     │   │
│  - test_cases         │       │   │  Java (javac/java)    │   │
│  - submissions        │       │   │  Python (python -u)   │   │
└───────────────────────┘       │   │  Timeout Watchdogs    │   │
                                │   │  Output Normalizer    │   │
                                │   └───────────────────────┘   │
                                └───────────────────────────────┘
```

---

## 2. Key Features

### 👤 User Experience
- **Secure Authentication & RBAC:** User registration and login powered by BCrypt hashing and stateless HMAC-SHA256 JWT tokens.
- **Advanced Problem Discovery:** Multi-criteria search, difficulty filtering (`EASY`, `MEDIUM`, `HARD`), category filtering, server-side sorting, and pagination executed via dynamic SQL criteria.
- **In-Browser Code Editor:** Multi-language syntax highlighting for Java 17 and Python 3, smart `Tab`/`Shift+Tab` indentation, auto-indent on `Enter`, `Ctrl+Enter` submit shortcut, font size controls, and local draft persistence via `localStorage`.
- **Real-Time Execution Feedback:** Interactive verdict cards displaying verdict (`ACCEPTED`, `WRONG_ANSWER`, `COMPILATION_ERROR`, `RUNTIME_ERROR`, `TIME_LIMIT_EXCEEDED`), execution time ($ms$), memory consumed ($KB$), test cases passed, and diagnostic logs.
- **User Analytics & Statistics:** Solved tally by difficulty, acceptance rate percentage, and submission history.

### 🛡️ Secure Code Execution Engine
- **Subprocess Isolation:** Submitted programs run inside dedicated temporary sandboxes via `ProcessBuilder` with no shared state.
- **Resource Constraints:** Java heap capped with `-Xmx256m`; execution time governed by watchdog timers that forcefully terminate runaway processes (`destroyForcibly()`).
- **Memory & Stream Guards:** Output capture streams are bounded ($1\text{MB}$ max) to prevent memory-exhaustion attacks from infinite print loops.
- **Whitespace-Normalized Evaluator:** Compares test case outputs ignoring trailing whitespace and newline variations (`\r\n` vs `\n`).

### ⚙️ Administrator Management Portal
- **Platform Analytics Dashboard:** Real-time metrics for total registered users, problem counts, and global submission volumes.
- **Problem CRUD:** Create, update, and delete programming challenges with customized time and memory limits.
- **Test Case Management:** Add public sample test cases (visible to users) and hidden validation test cases (judge-only).
- **User Directory & RBAC:** View registered users and elevate accounts between `ROLE_USER` and `ROLE_ADMIN`.
- **Global Submissions Audit:** Real-time inspectable audit log of all code submissions across the platform.

---

## 3. Technology Stack

### Backend
- **Language & Framework:** Java 17, Spring Boot 3.2.4
- **Security:** Spring Security 6, JJWT 0.12.5 (HMAC-SHA256)
- **Persistence & ORM:** Spring Data JPA, Hibernate 6, HikariCP
- **Database:** MySQL 8.0 (Production), H2 In-Memory (Test Suite)
- **Validation:** Hibernate Validator (JSR-380 Bean Validation)
- **Build Tool:** Apache Maven 3.9+

### Frontend
- **Framework & Tooling:** React 19, Vite 8, React Router 7
- **HTTP Client:** Axios 1.20 (with JWT interceptors and error normalization)
- **Styling:** Vanilla CSS design system (Dark Slate theme, responsive CSS Grid/Flexbox)
- **Typography:** Google Fonts (`Inter` for UI, `Fira Code` for code workspace)

### Testing & Quality Assurance
- **Automated Testing:** JUnit 5, Mockito, Spring Boot MockMvc, AssertJ (79 passing tests)
- **API Testing:** Postman Collection with automated test scripts

---

## 4. System Workflows

### Authentication & Authorization Flow
```
Client                      Server (Spring Security)              Database
  │                                    │                              │
  ├─── POST /api/auth/login ──────────>│                              │
  │    { usernameOrEmail, password }   ├─── Query user by username ──>│
  │                                    │<── Returns hashed password ──┤
  │                                    │                              │
  │                                    ├─── BCrypt password check     │
  │                                    ├─── Generate signed JWT token │
  │<── Returns 200 OK + JWT Token ─────┤                              │
  │                                    │                              │
  ├─── GET /api/admin/stats ──────────>│                              │
  │    Header: Bearer <token>          ├─── JwtAuthFilter validates   │
  │                                    ├─── Verify Role == ADMIN      │
  │<── Returns 200 OK + Admin Data ────┤                              │
```

### Problem Submission & Execution Flow
```
Client (CodeEditor)            Spring Boot API           Judge Engine Worker
  │                                   │                           │
  ├─── POST /api/submissions ────────>│                           │
  │    { problemId, lang, code }      ├── Save PENDING row to DB  │
  │<── Returns 201 Created (ID) ──────┤                           │
  │                                   ├── Dispatch async task ───>│
  │                                   │                           ├── Create sandbox dir
  │                                   │                           ├── Compile source (javac)
  │                                   │                           ├── For each test case:
  │                                   │                           │     Run subprocess
  │                                   │                           │     Feed input stream
  │                                   │                           │     Enforce timeout
  │                                   │                           │     Compare output
  │                                   │<── Update Verdict in DB ──┤
  ├─── GET /api/submissions/{id} ────>│                           │
  │    (Poll every 800ms)             │                           │
  │<── Returns Verdict (ACCEPTED) ────┤                           │
```

---

## 5. Database Schema & Indexing

```
 ┌────────────────────────┐         1:N         ┌────────────────────────┐
 │         users          │────────────────────<│      submissions       │
 ├────────────────────────┤                     ├────────────────────────┤
 │ id (PK, BIGINT)        │                     │ id (PK, BIGINT)        │
 │ username (VARCHAR, UQ) │                     │ user_id (FK, BIGINT)   │
 │ email (VARCHAR, UQ)    │                     │ problem_id (FK, BIGINT)│
 │ password (VARCHAR)     │                     │ source_code (LONGTEXT) │
 │ role (VARCHAR)         │                     │ language (VARCHAR)     │
 │ created_at (TIMESTAMP) │                     │ status (VARCHAR)       │
 └────────────────────────┘                     │ execution_time_ms (INT)│
                                                │ memory_used_kb (BIGINT)│
                                                │ error_message (TEXT)   │
 ┌────────────────────────┐         1:N         │ passed_test_cases (INT)│
 │        problems        │────────────────────<│ total_test_cases (INT) │
 ├────────────────────────┤                     │ submitted_at (TIMESTMP)│
 │ id (PK, BIGINT)        │                     └────────────────────────┘
 │ title (VARCHAR, UQ)    │                                 │
 │ description (LONGTEXT) │                                 │
 │ difficulty (VARCHAR)   │         1:N                     │
 │ category (VARCHAR)     │────────────────────┐            │
 │ time_limit_ms (INT)    │                    │            │
 │ memory_limit_mb (INT)  │                    ▼            │
 │ created_at (TIMESTAMP) │         ┌────────────────────────┐
 └────────────────────────┘         │       test_cases       │
                                    ├────────────────────────┤
                                    │ id (PK, BIGINT)        │
                                    │ problem_id (FK, BIGINT)│
                                    │ input_data (LONGTEXT)  │
                                    │ expected_output (LONG) │
                                    │ is_hidden (BOOLEAN)    │
                                    │ created_at (TIMESTAMP) │
                                    └────────────────────────┘
```

### Performance Indexes
- **`users`:** `idx_users_username` (unique), `idx_users_email` (unique), `idx_users_role`
- **`problems`:** `idx_problems_title` (unique), `idx_problems_difficulty`, `idx_problems_category`, `idx_problems_created_at`
- **`submissions`:** `idx_submissions_user_id`, `idx_submissions_problem_id`, `idx_submissions_status`, `idx_submissions_user_problem`, `idx_submissions_submitted_at`
- **`test_cases`:** `idx_test_cases_problem_id`, `idx_test_cases_is_hidden`

---

## 6. REST API Documentation

### Authentication
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Register new user account |
| `POST` | `/api/auth/login` | Public | Authenticate user & receive JWT token |

### Problem Management
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/problems` | Public | Browse problems with search, filters, sort, pagination |
| `GET` | `/api/problems/categories` | Public | Get all distinct problem categories |
| `GET` | `/api/problems/{id}` | Public | Get problem details and sample test cases |
| `POST` | `/api/problems` | Admin | Create a new coding problem |
| `PUT` | `/api/problems/{id}` | Admin | Update existing coding problem |
| `DELETE` | `/api/problems/{id}` | Admin | Delete a coding problem |

### Test Cases
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/problems/{id}/testcases/sample` | Public | Fetch sample test cases for problem workspace |
| `GET` | `/api/problems/{id}/testcases` | Admin | Fetch all test cases (including hidden) |
| `POST` | `/api/problems/{id}/testcases` | Admin | Create new test case for problem |
| `PUT` | `/api/testcases/{id}` | Admin | Update existing test case |
| `DELETE` | `/api/testcases/{id}` | Admin | Delete a test case |

### Submissions & Judge
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/submissions` | Authenticated | Submit code solution for asynchronous judge |
| `GET` | `/api/submissions/{id}` | Authenticated | Get submission status & verdict (Owner/Admin) |
| `GET` | `/api/submissions/my` | Authenticated | View authenticated user's submission history |
| `GET` | `/api/submissions/problem/{id}`| Authenticated | View user submissions for a specific problem |

### User Statistics & Profiles
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/users/me` | Authenticated | Get current user profile |
| `GET` | `/api/users/me/stats` | Authenticated | Get current user's problem solving metrics |
| `GET` | `/api/users/{id}/stats` | Public | Get public statistics for a specific user ID |
| `PUT` | `/api/users/me` | Authenticated | Update user profile |
| `PUT` | `/api/users/me/password` | Authenticated | Change user password |

### Admin Operations
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/admin/stats` | Admin | Get platform overview & analytics summary |
| `GET` | `/api/admin/submissions` | Admin | Global audit log of all platform submissions |
| `GET` | `/api/users` | Admin | List all registered users (paginated) |
| `PUT` | `/api/users/{id}/role` | Admin | Update user role (`ROLE_USER` / `ROLE_ADMIN`) |

---

## 7. Getting Started & Setup

### Prerequisites
- **JDK 17+** (Recommended: OpenJDK 17 or Oracle JDK 23)
- **Node.js 18+ & npm** (Recommended: Node 20+)
- **MySQL 8.0+**
- **Python 3.8+** (for Python code evaluation)

### Clone the Repository
```bash
git clone https://github.com/anusha975/online-coding-practice-platform.git
cd online-coding-practice-platform
```

### Initial Git Push Setup
If initializing and pushing to your remote repository:
```bash
git init
git add .
git commit -m "feat: complete online coding practice platform with judge engine and admin suite"
git branch -M main
git remote add origin https://github.com/anusha975/online-coding-practice-platform.git
git push -u origin main
```

---

## 8. Environment Variables

### Backend (`backend/.env` or OS Environment)
```properties
SERVER_PORT=8080
DB_URL=jdbc:mysql://localhost:3306/coding_platform_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_MS=86400000
```

### Frontend (`frontend/.env`)
```properties
VITE_API_BASE_URL=/api
```

---

## 9. Running the Application

### 1. Start the Backend (Spring Boot)
```bash
cd backend
# Using Maven wrapper (Linux/macOS)
./mvnw spring-boot:run

# Using Maven wrapper (Windows PowerShell / CMD)
.\mvnw.cmd spring-boot:run
```
Backend will start on `http://localhost:8080`.

### 2. Start the Frontend (Vite React)
```bash
cd frontend
npm install
npm run dev
```
Frontend development server will start on `http://localhost:5173`.

---

## 10. Automated & Manual Testing

### Running Backend Automated Tests
The backend test suite contains **79 automated unit and integration tests**:
```bash
cd backend
.\mvnw.cmd test
```

### Manual API Testing via Postman
A pre-configured Postman Collection with test assertions is included in the project root:
- [`Online_Coding_Platform_Postman_Collection.json`](file:///c:/Users/DELL/OneDrive/Desktop/online%20coding%20practice%20platform/Online_Coding_Platform_Postman_Collection.json)

**How to Import:**
1. Open Postman.
2. Click **Import** $\rightarrow$ select `Online_Coding_Platform_Postman_Collection.json`.
3. Run `1. Authentication -> Login User` to automatically populate the `{{userToken}}` variable for all subsequent requests.

---

## 11. User Interface & Visual Showcase

### Problem Discovery & Multi-Filter Catalog
- Live keyword search across titles and descriptions.
- Instant difficulty pills and dynamic category dropdowns.
- Interactive table column sorting with asc/desc toggles.

### Split-Screen Problem Workspace
- **Left Panel:** Problem statement, input/output specifications, constraints box, sample test cases, and past submissions tab.
- **Right Panel:** Dark-themed code editor with line gutters, language selector, auto-save status, and live verdict notifications.

### Administrator Control Center
- High-level metric cards for users, problems, and submission throughput.
- Full modal-driven Problem and Test Case editors.
- User management table with one-click role promotions.

---

## 12. Roadmap & Future Enhancements

- [ ] **Docker Container Isolation:** Enhance judge security by executing untrusted user code inside isolated Docker micro-containers.
- [ ] **WebSocket Verdict Streams:** Stream real-time test case execution progress over STOMP/WebSockets instead of HTTP polling.
- [ ] **Contest & Leaderboard Engine:** Timed programming contests with real-time scoreboards and penalty calculation.
- [ ] **Additional Language Support:** Expand judge compilers to support C++ (GCC), Go, and Rust.

---

## 📄 License
This project is open-source and available under the [MIT License](LICENSE).
