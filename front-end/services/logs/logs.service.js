import { apiClient } from "../api/client";

export async function getLogs() {
  return apiClient("/logs");
}

export async function getLogById(id) {
  return apiClient(`/logs/${id}`);
}