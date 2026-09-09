mod api;
mod models;
mod processing;

use api::handlers::{events, health};
use axum::{
    routing::{get, post},
    Router,
};

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt().init();

    let app = Router::new()
        .route("/health", get(health::health))
        .route("/events", post(events::post_events));

    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000").await.unwrap();

    axum::serve(listener, app).await.unwrap();
}
