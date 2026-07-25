# Django + Scala 3 Cron in the Same Pod — Minikube

This project creates an environment close to the production scenario:

```text
Deployment: django-scala
└── Pod (1 replica)
    ├── django container
    │   └── Gunicorn + Django on port 8000
    └── scala-cron container
        ├── Java 21
        ├── Scala 3 application packaged as a fat JAR
        └── Supercronic running the JAR every 10 minutes
```

Both containers run in the same Pod. Therefore, the Scala application calls
Django through `http://127.0.0.1:8000`.

The Deployment uses a single replica because each Pod contains its own scheduler.
With three replicas, the Scala job would run three times for every cron window.

## Requirements

Install:

- Docker;
- Minikube;
- kubectl;
- GNU Make, optional.

You do not need Python, Java, Scala, sbt, or Scala CLI installed locally.
Those tools are used only while building the container images.

## Full startup

From the project root:

```bash
make up
```

Without `make`:

```bash
./scripts/up.sh
```

The script:

1. starts Minikube when needed;
2. builds both images directly inside Minikube;
3. applies the Namespace, ConfigMaps, Secret, Deployment, and Service;
4. waits for the Pod to become ready;
5. shows the Kubernetes resource status.

The first Scala image build downloads Scala CLI and compiler dependencies, so
internet access is required.

## Check the Pod

```bash
make status
```

Expected result:

```text
NAME                            READY   STATUS    RESTARTS
django-scala-xxxxxxxxxx-xxxxx   2/2     Running   0
```

Detailed Pod information:

```bash
kubectl describe pod -n django-scala-local \
  -l app.kubernetes.io/name=django-scala
```

## Access Django

Run:

```bash
make url
```

The `minikube service` command prints the URL. Depending on the Minikube driver
and operating system, it may keep a tunnel open. Keep that terminal running
while using the URL.

Available endpoints:

```text
/
/health/live/
/health/ready/
/api/ping/
```

You can also use port forwarding:

```bash
kubectl port-forward \
  -n django-scala-local \
  service/django \
  8000:80
```

Then open:

```text
http://localhost:8000/
http://localhost:8000/api/ping/
```

## Logs

Django:

```bash
make logs-django
```

Scala scheduler:

```bash
make logs-scala
```

By default, the Scala application runs once when the container starts, after
Django becomes ready. It then continues according to the cron schedule at:

```text
00, 10, 20, 30, 40, and 50 minutes of every hour
```

The Scala logs should contain output similar to:

```text
[scala-job] GET http://127.0.0.1:8000/api/ping/
[scala-job] HTTP 200
[scala-job] Response: {"status": "pong", ...}
```

## Run Scala manually

Run the job without waiting for the next cron window:

```bash
make run-scala
```

This command executes the fat JAR inside the current `scala-cron` container.

## Test the cron every minute

Edit:

```text
kubernetes/10-configmaps.yaml
```

Change:

```cron
*/10 * * * * /opt/scala-job/run-job.sh
```

to:

```cron
* * * * * /opt/scala-job/run-job.sh
```

Apply the change:

```bash
make deploy
make logs-scala
```

Restore `*/10` after testing.

## Change the Django code

Main files:

```text
django-app/config/views.py
django-app/config/urls.py
```

Rebuild and restart:

```bash
minikube image build \
  -t django-scala/django:dev \
  ./django-app

kubectl rollout restart \
  deployment/django-scala \
  -n django-scala-local

kubectl rollout status \
  deployment/django-scala \
  -n django-scala-local
```

## Change the Scala code

Main file:

```text
scala-job/src/Main.scala
```

Rebuild and restart:

```bash
minikube image build \
  -t django-scala/scala-cron:dev \
  ./scala-job

kubectl rollout restart \
  deployment/django-scala \
  -n django-scala-local

kubectl rollout status \
  deployment/django-scala \
  -n django-scala-local
```

## Configuration

### Application settings

Non-sensitive settings:

```text
kubernetes/10-configmaps.yaml
```

Local secrets:

```text
kubernetes/20-secret-local.yaml
```

The Secret is only for local development. Do not commit real production
credentials to a repository.

### Cron schedule

The schedule is stored in the `scala-crontab` ConfigMap:

```cron
CRON_TZ=America/Sao_Paulo
*/10 * * * * /opt/scala-job/run-job.sh
```

Supercronic runs in the foreground as the container's main process. Scheduler
and job logs are written to stdout and stderr.

## Minikube-local images

The manifests use:

```yaml
imagePullPolicy: Never
```

The scripts build these images directly inside Minikube:

```text
django-scala/django:dev
django-scala/scala-cron:dev
```

No external registry is required for this local environment.

List the images:

```bash
minikube image ls | grep django-scala
```

## Open shells inside the containers

Django:

```bash
make shell-django
```

Scala:

```bash
make shell-scala
```

## Remove the application

Keep the Minikube cluster:

```bash
make down
```

Remove the Minikube cluster as well:

```bash
make delete-cluster
```

## Architecture decisions

### Deployment instead of a standalone Pod

The project uses a Deployment, which automatically creates and recovers the Pod.
This is closer to production than applying a standalone `Pod` resource.

### `Recreate` deployment strategy

The Deployment uses:

```yaml
strategy:
  type: Recreate
```

This reduces the chance of temporarily running both an old and a new scheduler
during a normal rollout. The Scala job should still be idempotent in production.

### Scaling

Do not configure an HPA or increase `replicas` while the scheduler remains in
the same Pod.

When Django must scale horizontally, move the Scala application to a separate
Kubernetes `CronJob`.

### Database

This example does not require an external database. Django uses SQLite at
`/tmp/db.sqlite3`.

For production, use PostgreSQL or another external database and inject its
configuration through Secrets and ConfigMaps.
