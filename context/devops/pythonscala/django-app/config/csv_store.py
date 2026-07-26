from __future__ import annotations

import csv
import fcntl
import os
import tempfile
from contextlib import contextmanager
from datetime import datetime, timezone
from pathlib import Path
from typing import Iterator

CSV_HEADERS = ["id", "name", "value", "updated_at", "updated_by"]

CSV_PATH = Path(os.getenv("CSV_PATH", "/data/records.csv"))
CSV_LOCK_PATH = Path(os.getenv("CSV_LOCK_PATH", "/data/records.lock"))


class CsvStoreError(RuntimeError):
    pass


class CsvValidationError(ValueError):
    pass


def _validate_text(field: str, value: object, *, required: bool, max_length: int) -> str:
    if not isinstance(value, str):
        raise CsvValidationError(f"{field} must be a JSON string")

    normalized = value.strip() if field in {"id", "name"} else value

    if required and not normalized:
        raise CsvValidationError(f"{field} must not be empty")

    if len(normalized) > max_length:
        raise CsvValidationError(
            f"{field} must contain at most {max_length} characters"
        )

    if any(character in normalized for character in ("\r", "\n", "\x00")):
        raise CsvValidationError(f"{field} must not contain line breaks or NUL")

    return normalized


def validate_payload(payload: dict[str, object]) -> tuple[str, str, str]:
    record_id = _validate_text("id", payload.get("id"), required=True, max_length=100)
    name = _validate_text("name", payload.get("name"), required=True, max_length=200)
    value = _validate_text("value", payload.get("value"), required=False, max_length=1000)
    return record_id, name, value


def ensure_storage_ready() -> None:
    CSV_PATH.parent.mkdir(parents=True, exist_ok=True)
    CSV_LOCK_PATH.parent.mkdir(parents=True, exist_ok=True)

    if not os.access(CSV_PATH.parent, os.W_OK):
        raise CsvStoreError(f"CSV directory is not writable: {CSV_PATH.parent}")


@contextmanager
def _file_lock(*, shared: bool) -> Iterator[None]:
    ensure_storage_ready()

    with CSV_LOCK_PATH.open("a+", encoding="utf-8") as lock_file:
        operation = fcntl.LOCK_SH if shared else fcntl.LOCK_EX
        fcntl.flock(lock_file.fileno(), operation)
        try:
            yield
        finally:
            fcntl.flock(lock_file.fileno(), fcntl.LOCK_UN)


def _read_records_unlocked() -> list[dict[str, str]]:
    if not CSV_PATH.exists() or CSV_PATH.stat().st_size == 0:
        return []

    with CSV_PATH.open("r", encoding="utf-8", newline="") as csv_file:
        reader = csv.DictReader(csv_file)

        if reader.fieldnames != CSV_HEADERS:
            raise CsvStoreError(
                f"Unexpected CSV header. Expected {CSV_HEADERS}, got {reader.fieldnames}"
            )

        return [
            {header: row.get(header, "") for header in CSV_HEADERS}
            for row in reader
        ]


def _write_records_unlocked(records: list[dict[str, str]]) -> None:
    ensure_storage_ready()

    temporary_path: Path | None = None

    try:
        with tempfile.NamedTemporaryFile(
            mode="w",
            encoding="utf-8",
            newline="",
            dir=CSV_PATH.parent,
            prefix="records-",
            suffix=".tmp",
            delete=False,
        ) as temporary_file:
            temporary_path = Path(temporary_file.name)
            writer = csv.DictWriter(
                temporary_file,
                fieldnames=CSV_HEADERS,
                extrasaction="ignore",
            )
            writer.writeheader()
            writer.writerows(records)
            temporary_file.flush()
            os.fsync(temporary_file.fileno())

        os.replace(temporary_path, CSV_PATH)
    finally:
        if temporary_path is not None and temporary_path.exists():
            temporary_path.unlink(missing_ok=True)


def list_records() -> list[dict[str, str]]:
    with _file_lock(shared=True):
        return _read_records_unlocked()


def upsert_record(
    *,
    record_id: str,
    name: str,
    value: str,
    updated_by: str,
) -> tuple[dict[str, str], bool, int]:
    timestamp = datetime.now(timezone.utc).isoformat()

    new_record = {
        "id": record_id,
        "name": name,
        "value": value,
        "updated_at": timestamp,
        "updated_by": updated_by,
    }

    with _file_lock(shared=False):
        records = _read_records_unlocked()
        created = True

        for index, existing in enumerate(records):
            if existing["id"] == record_id:
                records[index] = new_record
                created = False
                break
        else:
            records.append(new_record)

        _write_records_unlocked(records)
        return new_record, created, len(records)
