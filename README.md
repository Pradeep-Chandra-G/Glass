# Quiz Application Backend

A comprehensive Spring Boot backend for a quiz application with real-time timer synchronization, auto-submission, session security, and advanced analytics.

## Features

### Core Features
- **Multiple Question Types**: MCQ, True/False, Numerical
- **Real-time Timer**: WebSocket-based central server timer synchronized across all quiz takers
- **Auto-submission**: Automatic quiz submission on timer expiry
- **Session Security**: Secure session management with Spring Session JDBC
- **Auto-save**: Per-question answer auto-save with debouncing
- **Pagination**: Efficient pagination for questions, quizzes, and results

### Advanced Features
- **Scoring & Grading**: Automatic answer grading with point calculation
- **Analytics**: Comprehensive quiz and question-level analytics
- **Leaderboard**: Real-time leaderboard with rankings
- **User Statistics**: Personal performance tracking
- **Quiz Management**: Create, publish, and manage quizzes
- **Flexible Configuration**: Configurable timers, pagination, security settings

## Tech Stack

- **Spring Boot 3.2.0**
- **PostgreSQL** - Primary database
- **Spring Data JPA** - ORM and data access
- **Spring Security** - Authentication and authorization
- **Spring Session JDBC** - Distributed session management
- **WebSocket (STOMP)** - Real-time timer synchronization
- **Lombok** - Boilerplate reduction
- **Maven** - Dependency management

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Client Layer                         │
│              (Web/Mobile Frontend)                      │
└─────────────────────┬───────────────────────────────────┘
                      │
                      │ REST API / WebSocket
                      │
┌─────────────────────▼───────────────────────────────────┐
│                Controller Layer                         │
│  AuthController | QuizController | AttemptController    │
│  AnalyticsController                                    │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│                 Service Layer                           │
│  AuthService | QuizService | QuizAttemptService         │
│  TimerService | AnalyticsService                        │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│              Repository Layer                           │
│  UserRepo | QuizRepo | QuestionRepo | AttemptRepo       │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│                   PostgreSQL                            │
│     (Users, Quizzes, Questions, Attempts, Answers)      │
└─────────────────────────────────────────────────────────┘
```

## Database Schema

### Key Entities
- **User**: Authentication and user management
- **Quiz**: Quiz metadata and configuration
- **Question**: Quiz questions with type and options
- **QuestionOption**: MCQ/T-F answer options
- **QuizAttempt**: User quiz sessions with timing
- **Answer**: User responses with grading

## Setup Instructions

### Prerequisites
- Java 17+
- PostgreSQL 14+
- Maven 3.8+

### 1. Database Setup

```sql
CREATE DATABASE quizdb;
CREATE USER quizuser WITH PASSWORD 'quizpass';
GRANT ALL PRIVILEGES ON DATABASE quizdb TO quizuser;
```

### 2. Configure Application

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/quizdb
    username: quizuser
    password: quizpass
```

### 3. Build & Run

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the JAR
java -jar target/quiz-backend-1.0.0.jar
```

The application will start at `http://localhost:8080/api`

### 4. Default Users

After startup, the following users are created:

| Email | Password | Role |
|-------|----------|------|
| admin@quiz.com | admin123 | ADMIN |
| instructor@quiz.com | instructor123 | INSTRUCTOR |
| student@quiz.com | student123 | STUDENT |

## API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "John Doe"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Get Current User
```http
GET /api/auth/me
```

### Quiz Management Endpoints

#### Create Quiz
```http
POST /api/quizzes
Content-Type: application/json

{
  "title": "Mathematics Quiz",
  "description": "Basic algebra and geometry",
  "durationMinutes": 30,
  "startTime": "2024-01-20T10:00:00",
  "endTime": "2024-01-20T18:00:00",
  "passingScore": 60,
  "shuffleQuestions": true,
  "shuffleOptions": true
}
```

#### Get Active Quizzes
```http
GET /api/quizzes?page=0&size=10
```

#### Add Question to Quiz
```http
POST /api/quizzes/{quizId}/questions
Content-Type: application/json

{
  "type": "MCQ",
  "questionText": "What is 2 + 2?",
  "explanation": "Basic addition",
  "points": 5,
  "options": [
    {"optionText": "3", "isCorrect": false},
    {"optionText": "4", "isCorrect": true},
    {"optionText": "5", "isCorrect": false}
  ]
}
```

#### Publish Quiz
```http
POST /api/quizzes/{quizId}/publish
```

### Quiz Attempt Endpoints

#### Start Quiz
```http
POST /api/attempts/start/{quizId}
```

Response:
```json
{
  "attemptId": 123,
  "quizId": 1,
  "title": "Mathematics Quiz",
  "durationMinutes": 30,
  "startedAt": "2024-01-20T10:30:00",
  "expiresAt": "2024-01-20T11:00:00",
  "totalQuestions": 10,
  "firstQuestion": {...}
}
```

#### Get Question
```http
GET /api/attempts/{attemptId}/questions/{questionIndex}
```

#### Submit Answer (Auto-save)
```http
POST /api/attempts/answer
Content-Type: application/json

{
  "attemptId": 123,
  "questionId": 45,
  "selectedOptionId": 178  // For MCQ/True-False
}
```

For numerical questions:
```json
{
  "attemptId": 123,
  "questionId": 46,
  "numericalAnswer": 42.5
}
```

#### Submit Quiz
```http
POST /api/attempts/{attemptId}/submit
```

#### Get Results
```http
GET /api/attempts/{attemptId}/result
```

### Analytics Endpoints

#### Quiz Analytics
```http
GET /api/analytics/quiz/{quizId}
```

Response:
```json
{
  "quizId": 1,
  "quizTitle": "Mathematics Quiz",
  "totalAttempts": 50,
  "submittedAttempts": 45,
  "inProgressAttempts": 5,
  "averageScore": 75.5,
  "passRate": 82.5,
  "highestScore": 98,
  "lowestScore": 45,
  "questionAnalytics": [...]
}
```

#### Leaderboard
```http
GET /api/analytics/quiz/{quizId}/leaderboard?limit=10
```

#### User Statistics
```http
GET /api/analytics/user/stats
```

## WebSocket Timer Integration

### Connect to WebSocket
```javascript
const socket = new SockJS('http://localhost:8080/api/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, (frame) => {
  // Subscribe to timer updates
  stompClient.subscribe(`/topic/timer/${attemptId}`, (message) => {
    const timerUpdate = JSON.parse(message.body);
    console.log('Remaining seconds:', timerUpdate.remainingSeconds);
    
    if (timerUpdate.expired) {
      // Quiz has expired and been auto-submitted
      alert('Quiz time expired!');
      window.location.href = '/result';
    }
  });
});
```

### Timer Update Message Format
```json
{
  "attemptId": 123,
  "remainingSeconds": 450,
  "serverTime": "2024-01-20T10:37:30",
  "expired": false
}
```

## Configuration Options

### Timer Configuration
```yaml
quiz:
  timer:
    sync-interval: 1000  # Broadcast interval in milliseconds
```

### Pagination Configuration
```yaml
quiz:
  pagination:
    default-page-size: 10
    max-page-size: 100
```

### Auto-save Configuration
```yaml
quiz:
  auto-save:
    enabled: true
    debounce-ms: 2000  # Debounce delay for auto-save
```

### Security Configuration
```yaml
quiz:
  security:
    password:
      min-length: 8
    session:
      max-concurrent: 1  # Max concurrent sessions per user
```

## Testing

### Run Tests
```bash
mvn test
```

### Integration Test Example

```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@quiz.com","password":"student123"}' \
  -c cookies.txt

# 2. Get Active Quizzes
curl -X GET http://localhost:8080/api/quizzes \
  -b cookies.txt

# 3. Start Quiz
curl -X POST http://localhost:8080/api/attempts/start/1 \
  -b cookies.txt

# 4. Submit Answer
curl -X POST http://localhost:8080/api/attempts/answer \
  -H "Content-Type: application/json" \
  -d '{"attemptId":1,"questionId":1,"selectedOptionId":1}' \
  -b cookies.txt

# 5. Submit Quiz
curl -X POST http://localhost:8080/api/attempts/1/submit \
  -b cookies.txt
```

## Docker Deployment

### docker-compose.yml
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: quizdb
      POSTGRES_USER: quizuser
      POSTGRES_PASSWORD: quizpass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  quiz-app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/quizdb
      SPRING_DATASOURCE_USERNAME: quizuser
      SPRING_DATASOURCE_PASSWORD: quizpass
    depends_on:
      - postgres

volumes:
  postgres_data:
```

### Dockerfile
```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Run with Docker
```bash
docker-compose up -d
```

## Scalability Considerations

### Horizontal Scaling
- **Stateless Design**: All session state stored in database
- **Load Balancer Compatible**: Can run multiple instances behind load balancer
- **WebSocket Sticky Sessions**: Configure load balancer for WebSocket sticky sessions

### Performance Optimization
- **Database Indexing**: Indexes on frequently queried columns
- **Connection Pooling**: HikariCP configuration for optimal database connections
- **Batch Processing**: Hibernate batch inserts/updates enabled
- **Scheduled Tasks**: Background jobs for expired attempt processing

### Production Recommendations
1. Use Redis for Spring Session (instead of JDBC)
2. Enable database replication (read replicas)
3. Configure CDN for static assets
4. Enable gzip compression
5. Set up monitoring (Actuator + Prometheus)
6. Configure proper logging (ELK stack)
7. Use external message broker for WebSocket (RabbitMQ/Redis)

## Security Features

- **Password Encryption**: BCrypt password hashing
- **Session Management**: Secure session cookies with HttpOnly and SameSite
- **CORS Configuration**: Configurable allowed origins
- **SQL Injection Protection**: Parameterized queries via JPA
- **CSRF Protection**: Can be enabled for web clients
- **Role-based Access Control**: Admin, Instructor, Student roles

## License

MIT License

## Support

For issues and questions, please open an issue on the GitHub repository.