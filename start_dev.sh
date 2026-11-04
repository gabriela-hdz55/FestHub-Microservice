#!/bin/bash

echo "Starting databases in Docker..."
docker compose up -d artist-db category-db event-db media-db

echo "Booting microservices and UI in the background..."

# Catches Ctrl+C to cleanly kill all background processes
trap 'echo "Shutting down FestHub..."; kill $(jobs -p); exit' INT EXIT

# Live-hosts the frontend UI folder using Python 3
(cd client/src && python3 -m http.server 8000 > /dev/null 2>&1) &

# Boot backend services
(cd artist && SERVER_PORT=8081 DB_URL="jdbc:postgresql://localhost:5433/artistdb" DB_USER="festadmin" DB_PASSWORD="localpass123" ./mvnw spring-boot:run) &
(cd booking-category && SERVER_PORT=8082 DB_URL="jdbc:postgresql://localhost:5434/categorydb" DB_USER="festadmin" DB_PASSWORD="localpass123" ./mvnw spring-boot:run) &
(cd concert-event && SERVER_PORT=8083 DB_URL="jdbc:postgresql://localhost:5435/eventdb" DB_USER="festadmin" DB_PASSWORD="localpass123" ./mvnw spring-boot:run) &
(cd promo-media && SERVER_PORT=8084 DB_URL="jdbc:postgresql://localhost:5436/mediadb" DB_USER="festadmin" DB_PASSWORD="localpass123" ./mvnw spring-boot:run) &

echo "Booting API Gateway..."
# The CLIENT_URL variable overrides the Docker default and points the Gateway to your local Python server
(cd gateway && SPRING_PROFILES_ACTIVE=local CLIENT_URL="http://localhost:8000" ./mvnw spring-boot:run) &

echo "========================================================"
echo "FestHub is booting! Wait about 30 seconds for Spring to initialize."
echo "API Gateway and UI will be available at http://localhost:8080"
echo "Press Ctrl+C to shut everything down."
echo "========================================================"

wait
EOF