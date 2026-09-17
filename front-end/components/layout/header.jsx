export default function Header() {
  return (
    <header className="flex h-16 items-center justify-between border-b border-border bg-background px-6">
      <div>
        <p className="text-sm text-muted">
          Operações do Sistema
        </p>

        <h2 className="text-sm font-medium text-foreground">
          Overview
        </h2>
      </div>

      <div className="flex items-center gap-4">
        <div className="flex items-center gap-2 text-xs text-muted">
          <span className="h-2 w-2 rounded-full bg-emerald-500" />
          Agente Online
        </div>
      </div>
    </header>
  );
}