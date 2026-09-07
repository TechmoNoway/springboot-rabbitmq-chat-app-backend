.DEFAULT_GOAL := help

ifeq ($(OS),Windows_NT)
GRADLE := gradlew.bat
COPY_ENV := if not exist .env copy .env.example .env
else
GRADLE := ./gradlew
COPY_ENV := test -f .env || cp .env.example .env
endif

GRADLE_FLAGS ?= --no-daemon --console=plain
SERVICE ?= backend

.PHONY: help env doctor run test test-security build jar clean check dependencies \
	compose-check infra-up up rebuild down restart ps logs

help: ## Show available commands
	@echo Lynqo Backend commands:
	@echo.
	@echo   make env             Create .env from .env.example when missing
	@echo   make doctor          Print Java, Gradle and Docker versions
	@echo   make run             Run the backend with the dev profile
	@echo   make test            Run all tests
	@echo   make test-security   Run only the security boundary tests
	@echo   make check           Run Gradle verification and validate Compose
	@echo   make build           Clean, test and build the executable JAR
	@echo   make jar             Build the executable JAR without cleaning
	@echo   make clean           Remove Gradle build output
	@echo   make dependencies    Print the runtime dependency tree
	@echo   make infra-up        Start MySQL, RabbitMQ and Redis only
	@echo   make up              Build and start the complete Docker stack
	@echo   make rebuild         Rebuild and recreate the complete Docker stack
	@echo   make down            Stop the Docker stack without deleting volumes
	@echo   make restart         Restart SERVICE (default: backend)
	@echo   make logs            Follow logs for SERVICE (default: backend)
	@echo   make ps              Show Compose service status

env: ## Create local environment file without overwriting an existing one
	@$(COPY_ENV)

doctor: ## Show required tool versions
	@java --version
	@$(GRADLE) --version
	@docker --version
	@docker compose version

run: ## Run the application locally
	@$(GRADLE) bootRun $(GRADLE_FLAGS)

test: ## Run all tests
	@$(GRADLE) test $(GRADLE_FLAGS)

test-security: ## Run security boundary tests
	@$(GRADLE) test --tests "com.lynqo.backend.SecurityBoundaryTests" $(GRADLE_FLAGS)

build: ## Produce a verified executable JAR
	@$(GRADLE) clean test bootJar $(GRADLE_FLAGS)

jar: ## Produce the executable JAR
	@$(GRADLE) bootJar $(GRADLE_FLAGS)

clean: ## Remove build output
	@$(GRADLE) clean $(GRADLE_FLAGS)

check: test compose-check ## Run source and Compose verification

dependencies: ## Show resolved runtime dependencies
	@$(GRADLE) dependencies --configuration runtimeClasspath $(GRADLE_FLAGS)

compose-check: ## Validate compose.yaml
	@docker compose config --quiet

infra-up: env ## Start local infrastructure for make run
	@docker compose up -d mysql rabbitmq redis

up: env compose-check ## Start the full Docker stack
	@docker compose up -d --build

rebuild: env compose-check ## Rebuild images and recreate containers
	@docker compose up -d --build --force-recreate

down: ## Stop containers and preserve database/Redis volumes
	@docker compose down

restart: ## Restart one service; override with SERVICE=name
	@docker compose restart $(SERVICE)

ps: ## Show service state
	@docker compose ps

logs: ## Follow one service; override with SERVICE=name
	@docker compose logs -f --tail=200 $(SERVICE)
