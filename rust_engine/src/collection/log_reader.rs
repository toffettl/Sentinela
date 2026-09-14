use std::io;
use tokio::fs::File;
use tokio::io::{AsyncBufReadExt, BufReader};
use tokio::sync::mpsc::Sender;

pub async fn read_logs(path: &str, sender: Sender<String>) -> Result<(), io::Error> {
    let file = File::open(path).await?;
    let reader = BufReader::new(file);
    let mut lines = reader.lines();

    while let Some(line) = lines.next_line().await? {
        tracing::info!("Linha lida: {}", line);
        if sender.send(line).await.is_err() {
            break;
        }
    }

    Ok(())
}
