# IndiChess - Real-Time Multiplayer Chess

IndiChess is a full-stack real-time multiplayer chess application that allows users to create accounts, chat, and play chess friends or random opponents.

## Features

- **Real-time Gameplay**: Seamless chess moves synchronized via WebSockets.
- **Multiplayer Matchmaking**: Play against random opponents or challenge friends.
- **In-Game Chat**: Chat with your opponent during the match.
- **Move Validation**: Full implementation of chess rules (including castling, en passant, promotion).
- **User Authentication**: Secure login and registration.

## Tech Stack

### Backend
- **Java 17**
- **Spring Boot 3** (Web, Security, WebSocket, Data JPA)
- **H2 Database** (In-memory database for rapid development)
- **Maven** (Build tool)

### Frontend
- **React.js**
- **Chess.js** (Game logic)
- **React-Chessboard** (UI Board)
- **Stomp.js & SockJS** (WebSocket client)

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js & npm
- Maven (optional, wrapper included)

### 1. Backend Setup
Navigate to the backend directory and run the Spring Boot application.

```bash
cd backend
./mvnw spring-boot:run
```
The server will start on `http://localhost:8080`.

### 2. Frontend Setup
Open a new terminal, navigate to the frontend directory, install dependencies, and start the React app.

```bash
cd frontend
npm install
npm start
```
The application will open at `http://localhost:3000`.

## Directory Structure

- `backend/`: Spring Boot server code.
- `frontend/`: React application code.

## Note
Some changes are still required to make the project fully functional and to enhance its overall performance and quality.
