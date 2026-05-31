from std.collections import List
from std.time import perf_counter_ns

from demo_config import NUM_DOCS, DIM, TOP_K

# - 4 = 128-bit SIMD
# - 8 = 256-bit SIMD
# - 16 =  512-bit SIMD
comptime SIMD_WIDTH = 4


def read_float32_file(path: String, count: Int) raises -> List[Float32]:
    var data = List[Float32](length=count, fill=0.0)

    var f = open(path, "r")

    var bytes_read = f.read(data[:])
    var expected = count * 4

    if bytes_read != expected:
        raise Error("Bad file size while reading float32 data.")

    return data^


def insert_top_k(
    score: Float32,
    doc_id: Int,
    mut top_scores: List[Float32],
    mut top_ids: List[Int],
):
    # When score is less than the minimum in top_k, skip
    if score < top_scores[TOP_K - 1]:
        return
    
    # When scores are exactly equal, use doc_id as tie-breaker
    # Keep the one with LARGER doc_id first (matches Python's reverse sort behavior)
    if score == top_scores[TOP_K - 1]:
        if doc_id <= top_ids[TOP_K - 1]:
            return

    var pos = TOP_K - 1

    # Move items down to make space, considering both score and doc_id for ties
    while pos > 0 and (score > top_scores[pos - 1] or (score == top_scores[pos - 1] and doc_id > top_ids[pos - 1])):
        top_scores[pos] = top_scores[pos - 1]
        top_ids[pos] = top_ids[pos - 1]
        pos -= 1

    top_scores[pos] = score
    top_ids[pos] = doc_id


def calculate_score(
    query: List[Float32],
    embeddings: List[Float32],
    doc_id: Int,
) -> Float32:
    var base = doc_id * DIM

    var query_ptr = query.unsafe_ptr()
    var embeddings_ptr = embeddings.unsafe_ptr()
    
    # SIMD_WIDTH lanes.
    var acc = SIMD[DType.float32, SIMD_WIDTH](0.0)

    var j = 0
    
    while j + SIMD_WIDTH <= DIM:
        # Load new vectors at each iteration
        var query_vector = query_ptr.load[width=SIMD_WIDTH](j)
        var data_vector = embeddings_ptr.load[width=SIMD_WIDTH](base + j)
        
        acc += query_vector * data_vector
        j += SIMD_WIDTH

    var total = acc.reduce_add()
    # if amount if items is not divisible by SIMD_WIDTH, this while loop will run and get the remaining elements.
    while j < DIM:
        total += query[j] * embeddings[base + j]
        j += 1

    return total


def main() raises:

    var embeddings = read_float32_file("review_embeddings.f32", NUM_DOCS * DIM)
    var query = read_float32_file("query_embedding.f32", DIM)

    var top_scores = List[Float32](length=TOP_K, fill=-1000000000.0)
    var top_ids = List[Int](length=TOP_K, fill=-1)

    print("\nRunning Mojo SIMD search...")

    var start_time = perf_counter_ns()

    for doc_id in range(NUM_DOCS):
        var score = calculate_score(query, embeddings, doc_id)
        insert_top_k(score, doc_id, top_scores, top_ids)

    var end_time = perf_counter_ns()
    var elapsed_ns = end_time - start_time
    var elapsed_seconds = Float64(elapsed_ns) / 1_000_000_000.0

    print("\nMojo SIMD search time:", elapsed_seconds, "s")

    print("\nTop results:")
    for i in range(TOP_K):
        print("doc_id:", top_ids[i], "score:", top_scores[i])