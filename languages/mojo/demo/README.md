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
