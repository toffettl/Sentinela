import { apiClient } from "../api/client";
import { mockDashboard } from "../api/mock";

export async function getDashboard() {
  return apiClient("/api/dashboard/summary");
}

export async function getMockDashboard() {
  return mockDashboard;
}