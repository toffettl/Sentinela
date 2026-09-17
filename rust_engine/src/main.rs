mod api;
mod collection;
mod communication;
mod models;
mod processing;
use crate::collection::log_reader::read_logs;
use crate::communication::java_client::JavaClient;
use crate::processing::correlation::CorrelationState;
use crate::processing::event_processor::process_events;
use api::handlers::{events, health};
use axum::{
    routing::{get, post},
    Router,
};
use tokio::sync::mpsc;

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt().init();

    let (tx, rx) = mpsc::channel(100);
    let java_client = JavaClient::new(
        "http://localhost:8080".to_string(),
        "minha-api-key".to_string(),
    );

    tokio::spawn(async {
        read_logs(
            "/home/felipe/RiderProjects/Sentinela/API CRUD Certo/API CRUD Certo/logs/logs.log",
            tx,
        )
        .await
        .unwrap();
    });

    tokio::spawn(process_events(rx, CorrelationState::new(), java_client));

    let app = Router::new()
        .route("/health", get(health::health))
        .route("/events", post(events::post_events));

    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000").await.unwrap();

    axum::serve(listener, app).await.unwrap();
}
