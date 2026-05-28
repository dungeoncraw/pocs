# Mojo vs Python — Kaggle Embedding Similarity Search

This demo uses the Kaggle Amazon Fine Food Reviews dataset and compares:

1. Pure Python loop search
2. NumPy vectorized search
3. Mojo compiled loop search

The benchmark task is semantic search over review embeddings.

## Requirements

- Python 3.10+
- Mojo / MAX installed
- Enough disk/memory for the selected row count

Default row count: 100,000 reviews.

## Install Python dependencies

```bash
python -m venv .venv
source .venv/bin/activate

pip install -r requirements.txt
```
## Install Mojo

```bash
pixi init . -c https://conda.modular.com/max -c conda-forge
pixi add mojo
```

## Prepare data

```bash
python prepare_dataset.py
```

This creates:

- `review_embeddings.npy`
- `query_embedding.npy`
- `review_embeddings.f32`
- `query_embedding.f32`
- `reviews_metadata.csv`
- `demo_config.mojo`

## Run Python loop baseline

```bash
python search_python_loop.py
```

## Run NumPy baseline

```bash
python search_numpy.py
```

## Run Mojo

```bash
pixi run mojo build -O3 search_mojo.mojo -o search_mojo
./search_mojo
```


# Results

search_python_loop.py

    Embeddings shape: (100000, 384)
    Query: healthy snack with chocolate and low sugar

    Running pure Python loop search...

    Pure Python loop search time: 5.317592s

search_numpy.py

    Embeddings shape: (100000, 384)
    Query: healthy snack with chocolate and low sugar

    Running NumPy vectorized search...

    NumPy search time: 0.012511s


search_mojo.mojo

    Loading raw float32 files...
    NUM_DOCS: 100000
    DIM: 384
    TOP_K: 5

    Running Mojo scalar compiled search...

    Mojo search time: 0.06613 s

search_simd.mojo

    Running Mojo SIMD search...

    Mojo SIMD search time: 0.014134 s