SHELL := /bin/bash

.PHONY: up start build deploy recover status url test-apis show-csv \
  logs-django logs-scala-api logs-scala-cron run-scala \
  shell-django shell-scala-api shell-scala-cron restart down delete-cluster

up:
	./scripts/up.sh

start:
	./scripts/start-minikube.sh

build:
	./scripts/build-images.sh

deploy:
	./scripts/deploy.sh

recover:
	./scripts/recover-after-build-failure.sh

status:
	./scripts/status.sh

url:
	./scripts/open-service.sh

test-apis:
	./scripts/test-apis.sh

show-csv:
	./scripts/show-csv.sh

logs-django:
	kubectl logs -n django-scala-local deployment/django-scala -c django --follow

logs-scala-api:
	kubectl logs -n django-scala-local deployment/django-scala -c scala-api --follow

logs-scala-cron:
	kubectl logs -n django-scala-local deployment/django-scala -c scala-cron --follow

run-scala:
	./scripts/run-scala-now.sh

shell-django:
	./scripts/shell.sh django

shell-scala-api:
	./scripts/shell.sh scala-api

shell-scala-cron:
	./scripts/shell.sh scala-cron

restart:
	kubectl rollout restart deployment/django-scala -n django-scala-local
	kubectl rollout status deployment/django-scala -n django-scala-local --timeout=300s

down:
	kubectl delete namespace django-scala-local --ignore-not-found

delete-cluster:
	minikube delete
