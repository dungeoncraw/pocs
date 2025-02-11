use crate::controllers::auth::Claims;
use crate::AppState;
use actix_web::error::ErrorUnauthorized;
use actix_web::middleware::Next;
use actix_web::{body::BoxBody, dev::{ServiceRequest, ServiceResponse}, web, Error, HttpMessage};
use jsonwebtoken::{decode, DecodingKey, Validation};
use serde_json::json;

pub async fn verify_jwt(req: ServiceRequest, next: Next<BoxBody>) -> Result<ServiceResponse, Error> {
    let auth_header = req.headers().get("Authorization").ok_or_else(|| {
        ErrorUnauthorized(json!({
            "status": "error",
            "message": "Authorization header is missing"
        }))
    })?;
    let auth_string = auth_header.to_str().map_err(|_| {
        ErrorUnauthorized(json!({
            "status": "error",
            "message": "Authorization header is malformed"
        }))
    })?;
    if !auth_string.starts_with("Bearer ") {
        return Err(ErrorUnauthorized(json!({
            "status": "error",
            "message": "Authorization header is malformed"
        })));
    };

    let token = auth_string.strip_prefix("Bearer ").unwrap();
    let state = req.app_data::<web::Data<AppState>>().unwrap();
    let key = DecodingKey::from_secret(&state.jwt_secret.as_bytes());

    match decode::<Claims>(token, &key, &Validation::default()) {
        Ok(tokenData) => {
            req.extensions_mut().insert::<String>(tokenData.claims.sub.to_string().into());
            next.call(req).await
        }
        Err(_) => {
            return Err(ErrorUnauthorized(json!({
                "status": "error",
                "message": "Invalid token"
            })))
        }
    }
}
