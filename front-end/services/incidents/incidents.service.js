import { apiClient } from "../api/client";
import {
  mockIncidents,
  mockIncidentEvents
} from "../api/mock.js";

export async function getEvents() {
  return apiClient("/api/incidents");
}

export async function getIncidentEvents(id) {
  return apiClient(`/api/incidents/${id}/events`)
}

export async function getIncidentId(id) {
  return apiClient(`/api/incidents/${id}`)
}

export async function getMockIncidents() {
  return mockIncidents;
}

export async function getMockIncidentEvents(id) {
  return mockIncidentEvents[id] ?? [];
}