use crate::services::user::get_authenticated_user;
use crate::{db, AppState};
use actix_web::{delete, get, post, put, web, HttpRequest, HttpResponse, Responder};
use serde::Deserialize;
use serde_json::json;

#[get("/categories")]
pub async fn index(req: HttpRequest, state: web::Data<AppState>) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_authenticated_user(&req, &db).await;
    let categories = db::categories::get_all_by_user(&db, user.id).await;
    HttpResponse::Ok().json(categories)
}

#[derive(Deserialize, Debug)]
pub struct CreateCategoryRequest {
    pub name: String,
    pub description: String,
}

#[post("/categories")]
pub async fn create(
    req: HttpRequest,
    state: web::Data<AppState>,
    data: web::Json<CreateCategoryRequest>,
) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_authenticated_user(&req, &db).await;
    let category = db::categories::create(&db, &user.id, &data.name, &data.description).await;
    HttpResponse::Ok().json(category)
}

#[get("/categories/{id}")]
pub async fn show(
    state: web::Data<AppState>,
    req: HttpRequest,
    id: web::Path<i32>,
) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_authenticated_user(&req, &db).await;
    let Some(category) = db::categories::get_by_id(&db, *id).await else {
        return HttpResponse::NotFound().json(json!({"message": "Not found"}));
    };
    if category.user_id != user.id {
        return HttpResponse::Unauthorized().json(json!({"message": "Unauthorized"}));
    }
    HttpResponse::Ok().json(category)
}
#[derive(Deserialize, Debug)]
pub struct UpdateCategoryRequest {
    pub name: String,
    pub description: String,
}

#[put("/categories/{id}")]
pub async fn update(
    state: web::Data<AppState>,
    req: HttpRequest,
    id: web::Path<i32>,
    data: web::Json<UpdateCategoryRequest>,
) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_authenticated_user(&req, &db).await;
    let Some(category) = db::categories::get_by_id(&db, *id).await else {
        return HttpResponse::NotFound().json(json!({"message": "Not found"}));
    };
    if category.user_id != user.id {
        return HttpResponse::Unauthorized().json(json!({"message": "Unauthorized"}));
    }
    db::categories::update(&db, *id, &data.name, &data.description).await;
    let category = db::categories::get_by_id(&db, category.id).await;
    HttpResponse::Ok().json(category)
}

#[delete("/categories/{id}")]
pub async fn destroy(
    state: web::Data<AppState>,
    req: HttpRequest,
    id: web::Path<i32>,
) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_authenticated_user(&req, &db).await;
    let Some(category) = db::categories::get_by_id(&db, *id).await else {
        return HttpResponse::NotFound().json(json!({"message": "Not found"}));
    };
    if category.user_id != user.id {
        return HttpResponse::Unauthorized().json(json!({"message": "Unauthorized"}));
    }
    db::categories::delete(&db, *id).await;
    HttpResponse::Ok().json(json!({}))
}

#[get("/categories/{id}/transactions")]
pub async fn transactions_by_category(
    state: web::Data<AppState>,
    req: HttpRequest,
    id: web::Path<i32>,
) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_authenticated_user(&req, &db).await;
    let Some(category) = db::categories::get_by_id(&db, *id).await else {
        return HttpResponse::NotFound().json(json!({"message": "Category Not found"}));
    };
    if category.user_id != user.id {
      return  HttpResponse::Unauthorized().json(json!({"message": "Unauthorized"}));
    }
    let transactions = db::transactions::get_all_by_category(&db, *id).await;
    HttpResponse::Ok().json(transactions)
}