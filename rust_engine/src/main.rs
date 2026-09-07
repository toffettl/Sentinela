mod models;

use axum::{
    http::StatusCode,
    routing::{get, post},
    Json, Router,
};
use models::security_event::SecurityEvent;

async fn post_events(Json(event): Json<SecurityEvent>) -> StatusCode {
    tracing::info!("Creating event: {}", event.id);
    StatusCode::CREATED
}

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt().init();

    let app = Router::new()
        .route("/health", get(|| async { "OK" }))
        .route("/events", post(post_events));

    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000")
        .await
        .unwrap();

    axum::serve(listener, app).await.unwrap();
}
