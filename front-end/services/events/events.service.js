import { apiClient } from "../api/client";

export async function getEvents() {
  return apiClient("/api/events");
}


export async function getEventsId(id) {
  return apiClient(`/api/events/${id}`);
}