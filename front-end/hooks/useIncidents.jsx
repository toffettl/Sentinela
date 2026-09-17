"use client";

import { useEffect, useState } from "react";
import { getEvents } from "@/services/events/events.service";

export default function useEvents() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadEvents() {
      try {
        setLoading(true);

        const data = await getEvents();

        setEvents(data);
      } catch (error) {
        setError(error);
      } finally {
        setLoading(false);
      }
    }

    loadEvents();
  }, []);

  return {
    events,
    loading,
    error,
  };
}