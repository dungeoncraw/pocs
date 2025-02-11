use crate::services::user::get_authenticated_user;
use crate::{db, AppState};
use actix_web::{delete, get, post, put, web, HttpRequest, HttpResponse, Responder};
use serde::Deserialize;
use serde_json::json;

#[get("/transactions")]
pub async fn index(state: web::Data<AppState>, req: HttpRequest) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_authenticated_user(&req, &db).await;
    let transactions = db::transactions::get_all(&db, &user.id).await;
    HttpResponse::Ok().json(transactions)
}

#[derive(Deserialize)]
pub struct CreateTransactionRequest {
    pub category_id: i32,
    pub r#type: String,
    pub amount: i64,
    pub memo: String,
    pub description: Option<String>,
}
#[post("/transactions")]
pub async fn create(
    state: web::Data<AppState>,
    req: HttpRequest,
    data: web::Json<CreateTransactionRequest>,
) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_authenticated_user(&req, &db).await;
    let Some(category) = db::categories::get_by_id(&db, data.category_id).await else {
        return HttpResponse::NotFound().json(json!({"message": "Not found"}));
    };
    if category.user_id != user.id {
        return HttpResponse::Forbidden().json(json!({"message": "User not allowed"}));
    }
    if data.r#type == "expense" && (user.balance < data.amount || category.balance < data.amount) {
        return HttpResponse::BadRequest().json(json!({"message": "Insufficient balance"}));
    }
    let transaction = db::transactions::create(&db, &user.id,  &data).await;
    let user_balance = if data.r#type == "expense" {
        user.balance - data.amount
    } else {
        user.balance + data.amount
    };
    let category_balance = if data.r#type == "expense" {
        category.balance - data.amount
    } else {
        category.balance + data.amount
    };
    db::user::update_balance(&db, user.id, user_balance).await;
    db::categories::update_balance(&db, category.id, category_balance).await;
    HttpResponse::Ok().json(transaction)
}
#[get("/transactions/{id}")]
pub async fn show(
    state: web::Data<AppState>,
    id: web::Path<i32>,
    req: HttpRequest,
) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_authenticated_user(&req, &db).await;
    let Some(transaction) = db::transactions::get(&db, id.into_inner()).await else {
        return HttpResponse::NotFound().json(json!({"message": "Not found"}));
    };
    if transaction.user_id != user.id {
        return HttpResponse::Forbidden().json(json!({"message": "User not allowed"}));
    }
    HttpResponse::Ok().json(transaction)
}
#[derive(Deserialize)]
pub struct UpdateTransactionRequest {
    pub memo: String,
    pub description: Option<String>,
}
#[put("/transactions/{id}")]
pub async fn update(
    state: web::Data<AppState>,
    id: web::Path<i32>,
    data: web::Json<UpdateTransactionRequest>,
    req: HttpRequest,
) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_authenticated_user(&req, &db).await;

    let Some(transaction) = db::transactions::get(&db, id.into_inner()).await else {
        return HttpResponse::NotFound().json(json!({"message": "Not found"}));
    };
    if transaction.user_id != user.id {
        return HttpResponse::Forbidden().json(json!({"message": "User not allowed"}));
    }
    let updated_transaction = db::transactions::update(&db, transaction.id, &data).await;
    HttpResponse::Ok().json(updated_transaction)
}
#[delete("/transactions/{id}")]
pub async fn destroy(
    state: web::Data<AppState>,
    id: web::Path<i32>,
    req: HttpRequest,
) -> impl Responder {
    let db = state.db.lock().await;
    let user = get_authenticated_user(&req, &db).await;
    let Some(transaction) = db::transactions::get(&db, *id).await else {
        return HttpResponse::NotFound().json(json!({"message": "Not found"}));
    };
    let Some(category) = db::categories::get_by_id(&db, transaction.category_id).await else {
        return HttpResponse::NotFound().json(json!({"message": "Not found"}));
    };
    if user.id != transaction.user_id {
        return HttpResponse::Forbidden().json(json!({"message": "User not allowed"}));
    }
    if transaction.r#type == "expense"
        && (transaction.amount > user.balance || transaction.amount > category.balance)
    {
        return HttpResponse::BadRequest().json(json!({"message": "Insufficient balance"}));
    }
    db::transactions::delete(&db, *id).await;
    HttpResponse::Ok().json(json!({"message": "success"}))
}
