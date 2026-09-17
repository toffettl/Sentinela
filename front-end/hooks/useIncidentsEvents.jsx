"use client";

import { useEffect, useState } from "react";
import { getIncidentEvents } from "@/services/incidents/incidents.service";

export default function useIncidentEvents(incidentId) {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!incidentId) return;

    async function loadEvents() {
      try {
        setLoading(true);

        const data = await getIncidentEvents(incidentId);

        setEvents(data);
      } catch (error) {
        setError(error);
      } finally {
        setLoading(false);
      }
    }

    loadEvents();
  }, [incidentId]);

  return {
    events,
    loading,
    error,
  };
}