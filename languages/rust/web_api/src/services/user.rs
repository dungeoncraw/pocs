use actix_web::{HttpMessage, HttpRequest};
use crate::db::user::{get_by_id, User};

pub fn get_user_id(req: &HttpRequest) -> i32 {
    req.extensions().get::<String>().unwrap().to_owned().parse().unwrap()
}

pub async fn get_authenticated_user(req: &HttpRequest, db: &sqlx::PgPool) -> User {
    get_by_id(db, &get_user_id(req)).await.unwrap()
}