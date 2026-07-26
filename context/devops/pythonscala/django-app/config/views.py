from __future__ import annotations

import json
import os
import socket

from django.http import HttpRequest, JsonResponse
from django.views.decorators.csrf import csrf_exempt

from config.csv_store import (
    CsvStoreError,
    CsvValidationError,
    ensure_storage_ready,
    list_records,
    upsert_record,
    validate_payload,
)


def _method_not_allowed(*allowed_methods: str) -> JsonResponse:
    response = JsonResponse(
        {
            "implementation": "python",
            "error": f"Method not allowed. Use: {', '.join(allowed_methods)}",
        },
        status=405,
    )
    response["Allow"] = ", ".join(allowed_methods)
    return response


def home(request: HttpRequest) -> JsonResponse:
    return JsonResponse(
        {
            "application": "django-csv-api",
            "message": "Django is running on Kubernetes.",
            "pod": os.getenv("POD_NAME", socket.gethostname()),
            "csv_path": os.getenv("CSV_PATH", "/data/records.csv"),
            "endpoints": {
                "upsert": "POST /api/csv/upsert/",
                "list": "GET /api/csv/records/",
                "live": "GET /health/live/",
                "ready": "GET /health/ready/",
            },
        }
    )


def live(request: HttpRequest) -> JsonResponse:
    return JsonResponse({"status": "alive", "implementation": "python"})


def ready(request: HttpRequest) -> JsonResponse:
    try:
        ensure_storage_ready()
        return JsonResponse({"status": "ready", "implementation": "python"})
    except CsvStoreError as error:
        return JsonResponse(
            {
                "status": "not-ready",
                "implementation": "python",
                "error": str(error),
            },
            status=503,
        )


@csrf_exempt
def csv_upsert(request: HttpRequest) -> JsonResponse:
    if request.method != "POST":
        return _method_not_allowed("POST")

    try:
        payload = json.loads(request.body.decode("utf-8"))
    except (UnicodeDecodeError, json.JSONDecodeError):
        return JsonResponse(
            {
                "implementation": "python",
                "error": "The request body must contain a valid JSON object",
            },
            status=400,
        )

    if not isinstance(payload, dict):
        return JsonResponse(
            {
                "implementation": "python",
                "error": "The request body must contain a JSON object",
            },
            status=400,
        )

    try:
        record_id, name, value = validate_payload(payload)
        record, created, total = upsert_record(
            record_id=record_id,
            name=name,
            value=value,
            updated_by="python-api",
        )
    except CsvValidationError as error:
        return JsonResponse(
            {"implementation": "python", "error": str(error)},
            status=400,
        )
    except (CsvStoreError, OSError) as error:
        return JsonResponse(
            {"implementation": "python", "error": str(error)},
            status=500,
        )

    return JsonResponse(
        {
            "implementation": "python",
            "created": created,
            "total": total,
            "record": record,
        },
        status=201 if created else 200,
    )


def csv_records(request: HttpRequest) -> JsonResponse:
    if request.method != "GET":
        return _method_not_allowed("GET")

    try:
        records = list_records()
    except (CsvStoreError, OSError) as error:
        return JsonResponse(
            {"implementation": "python", "error": str(error)},
            status=500,
        )

    return JsonResponse(
        {
            "implementation": "python",
            "count": len(records),
            "records": records,
        }
    )
