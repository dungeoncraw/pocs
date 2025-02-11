use std::time::SystemTime;
use actix_web::{post, web, HttpResponse, Responder};
use jsonwebtoken::EncodingKey;
use serde::{Deserialize, Serialize};
use serde_json::json;
use crate::{db, AppState};

#[derive(Deserialize, Debug)]
pub struct SignUpRequest {
    pub email: String,
    pub password: String,
    pub first_name: String,
    pub last_name: String,
}

#[post("/auth/sign-up")]
pub async fn sign_up(state: web::Data<AppState>, data: web::Json<SignUpRequest>) -> impl Responder {
    let db = state.db.lock().await;
    if db::user::has_user_with_email(&db, &data.email).await {
        return HttpResponse::UnprocessableEntity().json(json!({
            "status": "error",
            "message": "User already exists"
        }));
    }
    db::user::create(&db, &data).await;
    HttpResponse::Created().json(json!({
        "status": "success",
        "message": "User created"
    }))
}
#[derive(Deserialize, Debug)]
pub struct SignInRequest {
    email: String,
    password: String,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct Claims {
    pub sub: i32,
    pub role: String,
    pub exp: u64,
}
#[post("/auth/sign-in")]
pub async fn sign_in(state: web::Data<AppState>, data: web::Json<SignInRequest>) -> impl Responder {
    let db = state.db.lock().await;
    let Some(user) = db::user::get_by_email(&db, &data.email).await else {
        return HttpResponse::Unauthorized().json(json!({
            "status": "error",
            "message": "Invalid username or password"
        }));
    };
    if !bcrypt::verify(&data.password, &user.password).unwrap() {
        return HttpResponse::Unauthorized().json(json!({
            "status": "error",
            "message": "Invalid username or password"
        }));
    };
    let claims = Claims {
        sub: user.id,
        role: "user".to_string(),
        exp: SystemTime::now().duration_since(SystemTime::UNIX_EPOCH).unwrap().as_secs() + 60 * 60 * 4,
    };
    let token = jsonwebtoken::encode(
        &jsonwebtoken::Header::default(),
        &claims,
        &EncodingKey::from_secret(state.jwt_secret.as_bytes())
    ).unwrap();
    HttpResponse::Ok().json(json!({
        "status": "success",
        "token": token
    }))
}