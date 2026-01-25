# makefile
COMPOSE = docker compose

.PHONY: up down build ps logs restart prune \
        shell-billing shell-patrons \
        migrate-billing migrate-patrons migrate \
        makemigrations-billing makemigrations-patrons \
        superuser-billing superuser-patrons \
        test-billing test-patrons

up:
	$(COMPOSE) up --build

down:
	$(COMPOSE) down

build:
	$(COMPOSE) build

ps:
	$(COMPOSE) ps

logs:
	$(COMPOSE) logs -f --tail=200

restart:
	$(COMPOSE) restart

prune:
	$(COMPOSE) down -v

shell-billing:
	$(COMPOSE) exec billing-api sh

shell-patrons:
	$(COMPOSE) exec patrons-api sh

migrate-billing:
	$(COMPOSE) exec billing-api poetry run python manage.py migrate

migrate-patrons:
	$(COMPOSE) exec patrons-api poetry run python manage.py migrate

migrate: migrate-billing migrate-patrons

makemigrations-billing:
	$(COMPOSE) exec billing-api poetry run python manage.py makemigrations

makemigrations-patrons:
	$(COMPOSE) exec patrons-api poetry run python manage.py makemigrations

superuser-billing:
	$(COMPOSE) exec billing-api poetry run python manage.py createsuperuser

superuser-patrons:
	$(COMPOSE) exec patrons-api poetry run python manage.py createsuperuser

test-billing:
	$(COMPOSE) exec billing-api poetry run python manage.py test

test-patrons:
	$(COMPOSE) exec patrons-api poetry run python manage.py test