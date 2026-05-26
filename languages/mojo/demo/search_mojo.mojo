from std.collections import List
from std.time import perf_counter_ns

from demo_config import NUM_DOCS, DIM, TOP_K


def read_float32_file(path: String, count: Int) raises -> List[Float32]:
    var data = List[Float32](length=count, fill=0.0)

    var f = open(path, "r")

    var bytes_read = f.read(data[:])
    var expected = count * 4

    if bytes_read != expected:
        print("Bad file size for:", path)
        print("Expected bytes:", expected)
        print("Actual bytes:", bytes_read)
        raise Error("Bad file size while reading float32 data.")

    return data^


def insert_top_k(
    score: Float32,
    doc_id: Int,
    mut top_scores: List[Float32],
    mut top_ids: List[Int],
):
    if score <= top_scores[TOP_K - 1]:
        return

    var pos = TOP_K - 1

    while pos > 0 and score > top_scores[pos - 1]:
        top_scores[pos] = top_scores[pos - 1]
        top_ids[pos] = top_ids[pos - 1]
        pos -= 1

    top_scores[pos] = score
    top_ids[pos] = doc_id


def main() raises:
    print("Loading raw float32 files...")
    print("NUM_DOCS:", NUM_DOCS)
    print("DIM:", DIM)
    print("TOP_K:", TOP_K)

    var embeddings = read_float32_file("review_embeddings.f32", NUM_DOCS * DIM)
    var query = read_float32_file("query_embedding.f32", DIM)

    var top_scores = List[Float32](length=TOP_K, fill=-1000000000.0)
    var top_ids = List[Int](length=TOP_K, fill=-1)

    print("\nRunning Mojo scalar compiled search...")

    var start_time = perf_counter_ns()

    for doc_id in range(NUM_DOCS):
        var total: Float32 = 0.0
        var base = doc_id * DIM

        for j in range(DIM):
            total += query[j] * embeddings[base + j]

        insert_top_k(total, doc_id, top_scores, top_ids)

    var end_time = perf_counter_ns()
    var elapsed_ns = end_time - start_time
    var elapsed_seconds = Float64(elapsed_ns) / 1_000_000_000.0

    print("\nMojo search time:", elapsed_seconds, "s")

    print("\nTop results:")
    for i in range(TOP_K):
        print("doc_id:", top_ids[i], "score:", top_scores[i])