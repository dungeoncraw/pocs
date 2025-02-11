use crate::db::user::get_by_id;
use crate::{db, AppState};
use crate::services::user::{get_authenticated_user, get_user_id};
use actix_web::{get, post, web, HttpRequest, HttpResponse, Responder};

#[get("/me")]
pub async fn get_profile(state: web::Data<AppState>, req: HttpRequest) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_by_id(&db, &get_user_id(&req)).await.unwrap();
    HttpResponse::Ok().json(user)
}
#[derive(serde::Deserialize, Debug)]
pub struct UpdateProfileRequest {
    pub first_name: String,
    pub last_name: String,
}
#[post("/me")]
pub async fn update_profile(
    state: web::Data<AppState>,
    req: HttpRequest,
    data: web::Json<UpdateProfileRequest>,
) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_authenticated_user(&req, &db).await;
    db::user::update(&db, user.id, &data).await;
    let user = get_authenticated_user(&req, &db).await;
    HttpResponse::Ok().json(user)
}
