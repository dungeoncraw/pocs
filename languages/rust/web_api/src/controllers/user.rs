use actix_web::{post, get, Responder};

#[get("/me")]
pub async fn get_profile() -> impl Responder {
    return "Worked ME"
}

#[post("/me")]
pub async fn update_profile() -> impl Responder {
    return "Update profile"
}