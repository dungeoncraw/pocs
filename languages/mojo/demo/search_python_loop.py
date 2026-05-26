\
import heapq
import json
import time

import numpy as np
import pandas as pd


def dot_product_python(query: np.ndarray, doc: np.ndarray) -> float:
    total = 0.0
    for i in range(query.shape[0]):
        total += float(query[i]) * float(doc[i])
    return total


def top_k_python_loop(query: np.ndarray, embeddings: np.ndarray, top_k: int):
    heap: list[tuple[float, int]] = []

    for doc_id in range(embeddings.shape[0]):
        score = dot_product_python(query, embeddings[doc_id])

        if len(heap) < top_k:
            heapq.heappush(heap, (score, doc_id))
        elif score > heap[0][0]:
            heapq.heapreplace(heap, (score, doc_id))

    return sorted(heap, reverse=True)


def print_results(results, metadata: pd.DataFrame) -> None:
    for score, doc_id in results:
        row = metadata.iloc[doc_id]
        print("-" * 90)
        print(f"doc_id:  {doc_id}")
        print(f"score:   {score:.6f}")
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
    print("\nRunning pure Python loop search...")

    start = time.perf_counter()
    results = top_k_python_loop(query, embeddings, top_k)
    elapsed = time.perf_counter() - start

    print(f"\nPure Python loop search time: {elapsed:.6f}s")
    print_results(results, metadata)


if __name__ == "__main__":
    main()
