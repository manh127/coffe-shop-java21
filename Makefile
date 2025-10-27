.PHONY: help build test run dev clean docker-build docker-up docker-down format check

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'

build: ## Build the application
	./mvnw clean package -DskipTests

test: ## Run all tests
	./mvnw test

verify: ## Run tests with verification
	./mvnw verify

format: ## Format code with Spotless
	./mvnw spotless:apply

check: ## Check code formatting and style
	./mvnw spotless:check checkstyle:check

run: ## Run the application locally
	./mvnw spring-boot:run

dev: ## Start PostgreSQL and run the application
	docker compose up -d postgres
	@echo "Waiting for PostgreSQL to be ready..."
	@sleep 5
	./mvnw spring-boot:run

clean: ## Clean build artifacts
	./mvnw clean
	docker compose down -v

docker-build: ## Build Docker image
	docker build -t coffee-shop:latest .

docker-up: ## Start all services with Docker Compose
	docker compose up -d

docker-down: ## Stop all services
	docker compose down

docker-logs: ## Show logs from all services
	docker compose logs -f

docker-restart: ## Restart all services
	docker compose restart

integration-test: ## Run integration tests
	./mvnw verify -Pci
