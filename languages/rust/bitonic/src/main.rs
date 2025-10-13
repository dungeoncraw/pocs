use axum::{
    extract::{Path, Query, State},
    http::StatusCode,
    response::IntoResponse,
    routing::{get, post},
    Json, Router,
};
use chrono::{DateTime, Utc};
use serde::{Deserialize, Serialize};
use sqlx::{postgres::PgPoolOptions, FromRow, PgPool};
use std::{env, net::SocketAddr};
use tracing_subscriber::{layer::SubscriberExt, util::SubscriberInitExt};
use uuid::Uuid;

mod lib;
use crate::lib::build_bitonic;

#[derive(Clone)]
struct AppState {
    pool: PgPool,
}

#[derive(Deserialize)]
struct CreateBitonicReq {
    n: usize,
    l: i64,
    r: i64,
}

#[derive(Serialize, FromRow, Debug, Clone)]
struct BitonicRun {
    id: Uuid,
    n: i32,
    l: i64,
    r: i64,
    sequence: Vec<i64>,
    created_at: DateTime<Utc>,
}

#[derive(Serialize)]
struct CreateBitonicResp {
    id: Uuid,
    sequence: Vec<i64>,
    created_at: DateTime<Utc>,
}

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    tracing_subscriber::registry()
        .with(tracing_subscriber::EnvFilter::try_from_default_env()
            .unwrap_or_else(|_| "bitonic_service=info,tower_http=info,axum=info".into()))
        .with(tracing_subscriber::fmt::layer())
        .init();

    dotenvy::dotenv().ok();

    let database_url = env::var("DATABASE_URL")
        .expect("DATABASE_URL must be set, e.g. postgres://user:pw@localhost:5432/bitonic");

    let pool = PgPoolOptions::new()
        .max_connections(10)
        .connect(&database_url)
        .await?;

    sqlx::migrate!("./migrations").run(&pool).await?;

    let state = AppState { pool };

    let app = Router::new()
        .route("/", get(health))
        .route("/bitonic", post(create_bitonic).get(list_bitonic))
        .route("/bitonic/:id", get(get_bitonic_by_id))
        .with_state(state);

    let addr: SocketAddr = "0.0.0.0:8080".parse().unwrap();
    let listener = tokio::net::TcpListener::bind(addr).await?;
    tracing::info!("listening on http://{addr}");
    axum::serve(listener, app).await?;

    Ok(())
}

async fn health() -> &'static str {
    "ok"
}

#[derive(Deserialize)]
struct ListParams {
    limit: Option<i64>,
    offset: Option<i64>,
}

async fn create_bitonic(
    State(state): State<AppState>,
    Json(payload): Json<CreateBitonicReq>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let seq = build_bitonic(payload.n, payload.l, payload.r)
        .map_err(|_| (StatusCode::UNPROCESSABLE_ENTITY, "Impossible with given range".to_string()))?;

    let id = Uuid::new_v4();
    let now = Utc::now();
    let record = sqlx::query_as::<_, BitonicRun>(
        r#"
        INSERT INTO bitonic_runs (id, n, l, r, sequence, created_at)
        VALUES ($1, $2, $3, $4, $5, $6)
        RETURNING id, n, l, r, sequence, created_at
        "#,
    )
    .bind(id)
    .bind(payload.n as i32)
    .bind(payload.l)
    .bind(payload.r)
    .bind(&seq)
    .bind(now)
    .fetch_one(&state.pool)
    .await
    .map_err(internal)?;

    let resp = CreateBitonicResp {
        id: record.id,
        sequence: record.sequence,
        created_at: record.created_at,
    };
    Ok((StatusCode::CREATED, Json(resp)))
}

async fn list_bitonic(
    State(state): State<AppState>,
    Query(params): Query<ListParams>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let limit = params.limit.unwrap_or(50).clamp(1, 200);
    let offset = params.offset.unwrap_or(0).max(0);

    let rows = sqlx::query_as::<_, BitonicRun>(
        r#"
        SELECT id, n, l, r, sequence, created_at
        FROM bitonic_runs
        ORDER BY created_at DESC
        LIMIT $1 OFFSET $2
        "#,
    )
    .bind(limit)
    .bind(offset)
    .fetch_all(&state.pool)
    .await
    .map_err(internal)?;

    Ok(Json(rows))
}

async fn get_bitonic_by_id(
    State(state): State<AppState>,
    Path(id): Path<Uuid>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let row = sqlx::query_as::<_, BitonicRun>(
        r#"
        SELECT id, n, l, r, sequence, created_at
        FROM bitonic_runs
        WHERE id = $1
        "#,
    )
    .bind(id)
    .fetch_optional(&state.pool)
    .await
    .map_err(internal)?;

    match row {
        Some(found) => Ok(Json(found)),
        None => Err((StatusCode::NOT_FOUND, "not found".into())),
    }
}

fn internal<E: std::fmt::Display>(e: E) -> (StatusCode, String) {
    tracing::error!("internal error: {e}");
    (StatusCode::INTERNAL_SERVER_ERROR, "internal error".into())
}