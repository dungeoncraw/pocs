use serde::Serialize;

#[derive(Serialize)]
pub struct Category {
    pub id: i32,
    pub user_id: i32,
    pub name: String,
    pub description: String,
    pub balance: i64,
    pub created_at: chrono::NaiveDateTime,
    pub updated_at: chrono::NaiveDateTime,
}

pub async fn get_all_by_user(db: &sqlx::PgPool, user_id: i32) -> Vec<Category> {
    sqlx::query_as!(
        Category,
        "SELECT * FROM categories WHERE user_id = $1",
        user_id
    )
    .fetch_all(db)
    .await
    .unwrap()
}

pub async fn get_by_id(db: &sqlx::PgPool, id: i32) -> Option<Category> {
    sqlx::query_as!(Category, "SELECT * FROM categories WHERE id = $1", id)
        .fetch_one(db)
        .await
        .ok()
}

pub async fn create(db: &sqlx::PgPool, user_id: &i32, name: &String, description: &String) -> Category {
    sqlx::query_as!(
        Category,
        "INSERT INTO categories (user_id, name, description) VALUES ($1, $2, $3) RETURNING *",
        user_id,
        name,
        description,
    ).fetch_one(db).await.unwrap()
}

pub async fn update(db: &sqlx::PgPool, id: i32, name: &String, description: &String) -> Category {
    sqlx::query_as!(
        Category,
        "UPDATE categories SET name = $1, description = $2 WHERE id = $3 RETURNING *",
        name,
        description,
        id,
    ).fetch_one(db).await.unwrap()
}

pub async fn delete(db: &sqlx::PgPool, id: i32) {
    sqlx::query!("DELETE FROM categories WHERE id = $1", id).execute(db).await.unwrap();
}

pub async fn update_balance(db: &sqlx::PgPool, category_id: i32, balance: i64) {
    sqlx::query!("UPDATE categories SET balance = $1 WHERE id = $2", balance, category_id)
        .execute(db)
        .await
        .unwrap();
}