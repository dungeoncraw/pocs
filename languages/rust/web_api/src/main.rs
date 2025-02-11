use actix_web::{web, App, HttpServer, middleware::from_fn};
use tokio::sync::Mutex;
mod controllers;
mod db;
mod middleware;
mod services;

struct AppState {
    db: Mutex<sqlx::PgPool>,
    jwt_secret: String,
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    dotenvy::dotenv().ok();
    let state = web::Data::new(AppState {
        db: Mutex::new(
            sqlx::PgPool::connect(
                &std::env::var("DATABASE_URL").unwrap()
            ).await.unwrap()
        ),
        jwt_secret: std::env::var("JWT_SECRET").unwrap(),
    });
    HttpServer::new(move || {
        App::new()
            .app_data(state.clone())
            .service(controllers::auth::sign_up)
            .service(controllers::auth::sign_in)
            .service(web::scope("/api")
                .wrap(from_fn(middleware::auth::verify_jwt))
                .service(controllers::user::get_profile)
                .service(controllers::user::update_profile)
                .service(controllers::categories::index)
                .service(controllers::categories::create)
                .service(controllers::categories::show)
                .service(controllers::categories::update)
                .service(controllers::categories::destroy)
                .service(controllers::transactions::index)
                .service(controllers::transactions::create)
                .service(controllers::transactions::show)
                .service(controllers::transactions::update)
                .service(controllers::transactions::destroy)
                .service(controllers::categories::transactions_by_category)
            )
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}