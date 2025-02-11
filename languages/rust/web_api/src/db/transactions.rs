use crate::controllers::transactions::{CreateTransactionRequest, UpdateTransactionRequest};
use serde::Serialize;

#[derive(Serialize)]
pub struct Transaction {
    pub id: i32,
    pub user_id: i32,
    pub category_id: i32,
    // using r# for reserved words so would be treat as literal, strange
    pub r#type: String,
    pub amount: i64,
    pub memo: String,
    pub description: Option<String>,
    pub created_at: chrono::NaiveDateTime,
    pub updated_at: chrono::NaiveDateTime,
}
pub async fn get_all(db: &sqlx::PgPool, user_id: &i32) -> Vec<Transaction> {
    sqlx::query_as!(
        Transaction,
        "SELECT * FROM transactions WHERE user_id = $1",
        user_id
    )
    .fetch_all(db)
    .await
    .unwrap()
}

pub async fn create(db: &sqlx::PgPool, user_id: &i32, transaction: &CreateTransactionRequest) -> Transaction {
    sqlx::query_as!(
        Transaction,
        "INSERT INTO transactions (category_id, user_id, type, amount, memo, description) values ($1, $2, $3, $4, $5, $6) RETURNING *",
        &transaction.category_id,
        &user_id,
        &transaction.r#type,
        &transaction.amount,
        &transaction.memo,
        transaction.description,
    ).fetch_one(db).await.unwrap()
}

pub async fn get(db: &sqlx::PgPool, transaction_id: i32) -> Option<Transaction> {
    sqlx::query_as!(
        Transaction,
        "SELECT * FROM transactions WHERE id = $1",
        transaction_id
    )
    .fetch_one(db)
    .await
    .ok()
}

pub async fn update(
    db: &sqlx::PgPool,
    transaction_id: i32,
    data: &UpdateTransactionRequest,
) -> Transaction {
    sqlx::query_as!(
        Transaction,
        "UPDATE transactions SET memo = $1, description = $2 WHERE id = $3 RETURNING *",
        &data.memo,
        data.description,
        &transaction_id
    )
    .fetch_one(db)
    .await
    .unwrap()
}
pub async fn delete(db: &sqlx::PgPool, transaction_id: i32) {
    sqlx::query!("DELETE FROM transactions WHERE id = $1", transaction_id)
        .execute(db)
        .await
        .unwrap();
}

pub async  fn get_all_by_category(db: &sqlx::PgPool, category_id: i32) -> Vec<Transaction> {
    sqlx::query_as!(
        Transaction,
        "SELECT * FROM transactions WHERE category_id = $1",
        category_id
    )
    .fetch_all(db).await.unwrap()
}