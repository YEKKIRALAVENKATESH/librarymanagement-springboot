# Library Management System (Spring Boot + Maven)

A beginner-friendly REST API using Java 21, Maven, Spring Boot, Spring Data JPA, validation, and a file-based H2 database.

## Requirements
- JDK 21
- Maven 3.9+ (or use `mvnw.cmd` included by Spring Initializr if present)

## Run in VS Code on Windows
1. Extract the ZIP.
2. In VS Code, select **File → Open Folder** and choose `librarymanagement`.
3. Select **Terminal → New Terminal**.
4. Check that the terminal is in the folder containing `pom.xml`.
5. Run:

   ```bat
   mvn spring-boot:run
   ```

   If Maven is not on PATH and `mvnw.cmd` exists, run:

   ```bat
   .\mvnw.cmd spring-boot:run
   ```

6. Open http://localhost:8082

## API endpoints

### Books
- `GET /api/books` — list all books
- `GET /api/books?q=java` — search title/author
- `GET /api/books/1` — get one book
- `POST /api/books` — create a book
- `POST /api/books/1` — update a book
- `DELETE /api/books/1` — delete a book (not allowed while it is currently issued)

Example JSON for `POST /api/books`:
```json
{
  "title": "Java Basics",
  "author": "A. Kumar",
  "isbn": "9780000000001",
  "totalCopies": 5,
  "availableCopies": 5
}
```

### Members
- `GET /api/members`
- `GET /api/members/1`
- `POST /api/members`
- `PUT /api/members/1`
- `DELETE /api/members/1`

Example JSON for `POST /api/members`:
```json
{
  "name": "Ravi Kumar",
  "email": "ravi@example.com",
  "phone": "9876543210"
}
```

### Loans
- `GET /api/loans` — all loans
- `GET /api/loans?activeOnly=true` — currently issued loans
- `POST /api/loans/issue` — issue a book
- `POST /api/loans/1/return` — return a loan

Example JSON for `POST /api/loans/issue`:
```json
{
  "bookId": 1,
  "memberId": 1,
  "loanDays": 14
}
```

## Test APIs
Use VS Code's Thunder Client extension, Postman, curl, or the built-in Swagger UI:
- **Swagger UI**: http://localhost:8082/swagger-ui/index.html

For example, with curl:
```bat
curl http://localhost:8082/api/books
```

## Database
Configured to use **PostgreSQL** on port 5432 with database `library` (defined in `application.properties`).

## Notes
- Port is configured to **8082** (`server.port=8082` in `application.properties`).
- This is a learning/demo project. It does not include authentication or role-based access.
