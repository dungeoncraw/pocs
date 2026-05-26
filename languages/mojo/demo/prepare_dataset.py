\
import glob
import json
import os
import time
from pathlib import Path

import kagglehub
import numpy as np
import pandas as pd
from sentence_transformers import SentenceTransformer


DATASET = "snap/amazon-fine-food-reviews"
MODEL_NAME = "sentence-transformers/all-MiniLM-L6-v2"

N_ROWS = int(os.environ.get("N_ROWS", "100000"))
QUERY = os.environ.get("QUERY", "healthy snack with chocolate and low sugar")
TOP_K = int(os.environ.get("TOP_K", "5"))

OUT_DIR = Path(".")


def find_reviews_csv(dataset_path: str) -> str:
    matches = glob.glob(os.path.join(dataset_path, "**", "Reviews.csv"), recursive=True)
    if not matches:
        raise FileNotFoundError("Could not find Reviews.csv inside Kaggle dataset.")
    return matches[0]


def clean_text(value) -> str:
    if pd.isna(value):
        return ""
    return str(value).replace("\n", " ").replace("\r", " ").strip()


def main() -> None:
    print(f"Downloading Kaggle dataset: {DATASET}")
    dataset_path = kagglehub.dataset_download(DATASET)
    reviews_csv = find_reviews_csv(dataset_path)

    print(f"Loading {N_ROWS:,} rows from: {reviews_csv}")
    df = pd.read_csv(reviews_csv, nrows=N_ROWS)

    required = ["Id", "ProductId", "Score", "Summary", "Text"]
    missing = [c for c in required if c not in df.columns]
    if missing:
        raise RuntimeError(f"Dataset is missing expected columns: {missing}")

    df["Summary"] = df["Summary"].apply(clean_text)
    df["Text"] = df["Text"].apply(clean_text)
    df["document"] = (df["Summary"] + ". " + df["Text"]).str.strip()

    metadata = df[["Id", "ProductId", "Score", "Summary", "Text"]].copy()
    metadata.to_csv(OUT_DIR / "reviews_metadata.csv", index=False)

    texts = df["document"].tolist()

    print(f"Loading embedding model: {MODEL_NAME}")
    model = SentenceTransformer(MODEL_NAME)

    print("Generating normalized review embeddings...")
    start = time.perf_counter()

    embeddings = model.encode(
        texts,
        batch_size=128,
        show_progress_bar=True,
        normalize_embeddings=True,
    ).astype(np.float32)

    query_embedding = model.encode(
        [QUERY],
        normalize_embeddings=True,
    ).astype(np.float32)[0]

    elapsed = time.perf_counter() - start

    if embeddings.ndim != 2:
        raise RuntimeError(f"Expected 2D embeddings, got shape {embeddings.shape}")

    num_docs, dim = embeddings.shape

    np.save(OUT_DIR / "review_embeddings.npy", embeddings)
    np.save(OUT_DIR / "query_embedding.npy", query_embedding)

    # Raw little-endian float32 files for Mojo.
    # Mojo does not need to parse .npy headers; it reads plain float32 data.
    embeddings.tofile(OUT_DIR / "review_embeddings.f32")
    query_embedding.tofile(OUT_DIR / "query_embedding.f32")

    with open(OUT_DIR / "demo_config.json", "w", encoding="utf-8") as f:
        json.dump(
            {
                "dataset": DATASET,
                "model": MODEL_NAME,
                "query": QUERY,
                "num_docs": int(num_docs),
                "dim": int(dim),
                "top_k": int(TOP_K),
            },
            f,
            indent=2,
        )

    # Mojo config module generated from the actual embedding shape.
    with open(OUT_DIR / "demo_config.mojo", "w", encoding="utf-8") as f:
        f.write(
            f"""\
            comptime NUM_DOCS = {int(num_docs)}
            comptime DIM = {int(dim)}
            comptime TOP_K = {int(TOP_K)}
            """
        )

    print("\nCreated files:")
    print(f"- review_embeddings.npy      shape={embeddings.shape}")
    print(f"- query_embedding.npy        shape={query_embedding.shape}")
    print("- review_embeddings.f32      raw float32 file for Mojo")
    print("- query_embedding.f32        raw float32 file for Mojo")
    print("- reviews_metadata.csv       text metadata for displaying results")
    print("- demo_config.mojo           constants for Mojo")
    print(f"\nEmbedding preparation time: {elapsed:.2f}s")
    print(f"Query: {QUERY}")


if __name__ == "__main__":
    main()
