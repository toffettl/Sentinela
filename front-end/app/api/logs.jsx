"use client";

import useLogs from "@/hooks/useLogs";

export default function LogsPage() {
  const {
    logs,
    loading,
    error
  } = useLogs();

  if (loading) {
    return <p>Carregando...</p>;
  }

  if (error) {
    return <p>Erro ao carregar os logs.</p>;
  }

  return (
    <div>
      {logs.map((log) => (
        <div key={log.id}>
          {log.message}
        </div>
      ))}
    </div>
  );
}