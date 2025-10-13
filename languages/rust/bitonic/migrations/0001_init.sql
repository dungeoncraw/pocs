-- Create the table to store bitonic runs
CREATE TABLE IF NOT EXISTS bitonic_runs (
  id UUID PRIMARY KEY,
  n INTEGER NOT NULL,
  l BIGINT NOT NULL,
  r BIGINT NOT NULL,
  sequence BIGINT[] NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Helpful index for listing by most recent
CREATE INDEX IF NOT EXISTS idx_bitonic_runs_created_at ON bitonic_runs (created_at DESC);