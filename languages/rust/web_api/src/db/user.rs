use crate::controllers::auth::SignUpRequest;
use crate::controllers::user::UpdateProfileRequest;
use bcrypt::{hash, DEFAULT_COST};
use serde::Serialize;
use sqlx::types::chrono;

pub async fn has_user_with_email(db: &sqlx::PgPool, email: &str) -> bool {
    sqlx::query!("SELECT * FROM users WHERE email = $1", email)
        .fetch_optional(db)
        .await
        .unwrap()
        .is_some()
}
pub async fn create(db: &sqlx::PgPool, user: &SignUpRequest) {
    // hash user password
    let hashed_password = hash(&user.password, DEFAULT_COST).unwrap();
    sqlx::query!(
        "INSERT INTO users (email, password, firstname, lastname) VALUES ($1, $2, $3, $4)",
        &user.email,
        &hashed_password,
        &user.first_name,
        &user.last_name
    )
    .execute(db)
    .await
    .is_ok();
}
#[derive(Serialize)]
pub struct User {
    pub id: i32,
    pub email: String,
    #[serde(skip_serializing)]
    pub password: String,
    pub firstname: String,
    pub lastname: String,
    pub balance: i64,
    pub created_at: chrono::NaiveDateTime,
    pub updated_at: chrono::NaiveDateTime,
}
pub async fn get_by_email(db: &sqlx::PgPool, email: &str) -> Option<User> {
    sqlx::query_as!(User, "SELECT * FROM users WHERE email = $1", email)
        .fetch_optional(db)
        .await
        .unwrap()
}

pub async fn get_by_id(db: &sqlx::PgPool, id: &i32) -> Option<User> {
    sqlx::query_as!(User, "SELECT * FROM users WHERE id = $1", id)
        .fetch_optional(db)
        .await
        .unwrap()
}

pub async fn update(db: &sqlx::PgPool, id: i32, user: &UpdateProfileRequest) {
    sqlx::query!(
        "UPDATE  users SET firstname = $1, lastname = $2 WHERE id = $3",
        &user.first_name,
        &user.last_name,
        &id
    )
    .execute(db)
    .await
    .unwrap();
}

pub async fn update_balance(db: &sqlx::PgPool, user_id: i32, balance: i64) {
    sqlx::query!(
        "UPDATE users SET balance = $1 WHERE id = $2",
        balance,
        user_id
    )
    .execute(db)
    .await
    .unwrap();
}
