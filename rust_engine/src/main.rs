mod api;
mod collection;
mod models;
mod processing;

use api::handlers::{events, health};
use axum::{
    routing::{get, post},
    Router,
};
use tokio::sync::mpsc;

use crate::collection::log_reader::read_logs;
use crate::processing::event_processor::process_events;

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt().init();

    let (tx, rx) = mpsc::channel(100);

    tokio::spawn(async {
        read_logs("logs/app.log", tx).await.unwrap();
    });

    tokio::spawn(process_events(rx));

    let app = Router::new()
        .route("/health", get(health::health))
        .route("/events", post(events::post_events));

    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000").await.unwrap();

    axum::serve(listener, app).await.unwrap();
}

