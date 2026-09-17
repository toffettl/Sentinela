use std::io::{self, SeekFrom};
use std::os::unix::fs::MetadataExt;
use std::time::Duration;

use tokio::fs::File;
use tokio::io::{AsyncReadExt, AsyncSeekExt};
use tokio::sync::mpsc::Sender;

const POLL_INTERVAL: Duration = Duration::from_millis(200);
const READ_BUFFER_SIZE: usize = 8192;

pub async fn read_logs(path: &str, sender: Sender<String>) -> Result<(), io::Error> {
    let mut file = File::open(path).await?;
    let mut cursor: u64 = 0;
    let mut buffer = vec![0u8; READ_BUFFER_SIZE];
    let mut pending = String::new();

    loop {
        let (dev, ino) = {
            let meta = file.metadata().await?;
            (meta.dev(), meta.ino())
        };

        match tokio::fs::metadata(path).await {
            Ok(path_meta) => {
                if (path_meta.dev(), path_meta.ino()) != (dev, ino) {
                    tracing::warn!(path, "Arquivo substituído; reabrindo do início");
                    file = File::open(path).await?;
                    cursor = 0;
                    pending.clear();
                }
            }
            Err(_) => {
                tokio::time::sleep(POLL_INTERVAL).await;
                continue;
            }
        }

        let len = file.metadata().await?.len();

        if len < cursor {
            tracing::warn!(path, "Arquivo truncado; lendo novamente do início");
            cursor = 0;
            pending.clear();
            file.seek(SeekFrom::Start(0)).await?;
        }

        if len > cursor {
            let read = file.read(&mut buffer).await?;
            cursor += read as u64;
            pending.push_str(&String::from_utf8_lossy(&buffer[..read]));

            let mut start = 0;
            while let Some(pos) = pending[start..].find('\n') {
                let end = start + pos;
                let line = pending[start..end]
                    .strip_suffix('\r')
                    .unwrap_or(&pending[start..end]);

                if line.trim().is_empty() {
                    start = end + 1;
                    continue;
                }

                tracing::info!("Linha lida: {}", line);
                if sender.send(line.to_string()).await.is_err() {
                    return Ok(());
                }
                start = end + 1;
            }
            pending.drain(..start);
        }

        tokio::time::sleep(POLL_INTERVAL).await;
    }
}