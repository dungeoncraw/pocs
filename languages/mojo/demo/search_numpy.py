\
import json
import time

import numpy as np
import pandas as pd


def print_results(top_ids: np.ndarray, scores: np.ndarray, metadata: pd.DataFrame) -> None:
    for doc_id in top_ids:
        row = metadata.iloc[int(doc_id)]
        print("-" * 90)
        print(f"doc_id:  {int(doc_id)}")
        print(f"score:   {float(scores[doc_id]):.6f}")
        print(f"rating:  {row['Score']}")
        print(f"summary: {row['Summary']}")
        print(f"text:    {str(row['Text'])[:350]}...")


def main() -> None:
    with open("demo_config.json", "r", encoding="utf-8") as f:
        config = json.load(f)

    embeddings = np.load("review_embeddings.npy")
    query = np.load("query_embedding.npy")
    metadata = pd.read_csv("reviews_metadata.csv")

    top_k = int(config["top_k"])

    print(f"Embeddings shape: {embeddings.shape}")
    print(f"Query: {config['query']}")
    print("\nRunning NumPy vectorized search...")

    start = time.perf_counter()

    # Embeddings and query are normalized, so cosine similarity is a dot product.
    scores = embeddings @ query

    top_ids = np.argpartition(scores, -top_k)[-top_k:]
    top_ids = top_ids[np.argsort(scores[top_ids])[::-1]]

    elapsed = time.perf_counter() - start

    print(f"\nNumPy search time: {elapsed:.6f}s")
    print_results(top_ids, scores, metadata)


if __name__ == "__main__":
    main()
