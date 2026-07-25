SHELL := /bin/bash

.PHONY: up start build deploy status url logs-django logs-scala run-scala shell-django shell-scala restart down delete-cluster

up:
	./scripts/up.sh

start:
	./scripts/start-minikube.sh

build:
	./scripts/build-images.sh

deploy:
	./scripts/deploy.sh

status:
	./scripts/status.sh

url:
	./scripts/open-service.sh

logs-django:
	kubectl logs -n django-scala-local deployment/django-scala -c django --follow

logs-scala:
	kubectl logs -n django-scala-local deployment/django-scala -c scala-cron --follow

run-scala:
	./scripts/run-scala-now.sh

shell-django:
	./scripts/shell.sh django

shell-scala:
	./scripts/shell.sh scala-cron

restart:
	kubectl rollout restart deployment/django-scala -n django-scala-local
	kubectl rollout status deployment/django-scala -n django-scala-local --timeout=180s

down:
	kubectl delete namespace django-scala-local --ignore-not-found

delete-cluster:
	minikube delete
