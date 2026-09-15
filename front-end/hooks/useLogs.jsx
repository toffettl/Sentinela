"use client";

import { useEffect, useState } from "react";
import { getLogs } from "@/services/logs/logs.service";

export default function useLogs() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadLogs() {
      try {
        setLoading(true);

        const data = await getLogs();

        setLogs(data);
      } catch (error) {
        setError(error);
      } finally {
        setLoading(false);
      }
    }

    loadLogs();
  }, []);

  return {
    logs,
    loading,
    error,
  };
}