from datetime import datetime, timezone
import os
import socket

from django.http import JsonResponse


def home(request):
    return JsonResponse(
        {
            "application": "django-local-minikube",
            "message": "Django is running on Kubernetes.",
            "pod": os.getenv("POD_NAME", socket.gethostname()),
            "endpoints": [
                "/health/live/",
                "/health/ready/",
                "/api/ping/",
            ],
        }
    )


def live(request):
    return JsonResponse({"status": "alive"})


def ready(request):
    return JsonResponse({"status": "ready"})


def ping(request):
    return JsonResponse(
        {
            "status": "pong",
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "pod": os.getenv("POD_NAME", socket.gethostname()),
            "caller": request.headers.get("User-Agent", "unknown"),
        }
    )
