use actix_web::{post, Responder};

#[post("/auth/sign-up")]
pub async fn sign_up() -> impl Responder {
    return "Worked"
}

#[post("/auth/sign-in")]
pub async fn sign_in() -> impl Responder {
    return "Worked"
}