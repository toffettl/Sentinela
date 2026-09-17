use crate::models::detection::DetectionRequest;
use crate::models::security_event::SecurityEvent;

pub struct JavaClient {
    client: reqwest::Client,
    base_url: String,
    api_key: String,
}

impl JavaClient {
    pub fn new(base_url: String, api_key: String) -> Self {
        Self {
            client: reqwest::Client::new(),
            base_url,
            api_key,
        }
    }

    pub async fn send_event(&self, event: &SecurityEvent) -> Result<(), reqwest::Error> {
        let url = format!("{}/api/v1/events", self.base_url);

        self.client
            .post(url)
            .header("Authorization", format!("Bearer {}", self.api_key))
            .json(event)
            .send()
            .await?
            .error_for_status()?;

        Ok(())
    }

    pub async fn send_detection(
        &self,
        detection: &DetectionRequest,
    ) -> Result<(), reqwest::Error> {
        let url = format!("{}/api/v1/detections", self.base_url);

        self.client
            .post(url)
            .header("Authorization", format!("Bearer {}", self.api_key))
            .json(detection)
            .send()
            .await?
            .error_for_status()?;

        Ok(())
    }
}

impl std::fmt::Debug for JavaClient {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        f.debug_struct("JavaClient")
            .field("base_url", &self.base_url)
            .field("api_key", &"****")
            .finish()
    }
}