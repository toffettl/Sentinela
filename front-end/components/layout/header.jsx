export default function Header() {
  return (
    <header className="flex h-16 items-center justify-between border-b border-zinc-800 bg-zinc-950 px-6">
      <div>
        <p className="text-sm text-zinc-500">
          Operações do Sistema
        </p>

        <h2 className="text-sm font-medium text-zinc-200">
          Overview
        </h2>
      </div>

      <div className="flex items-center gap-4">
        <div className="flex items-center gap-2 text-xs text-zinc-400">
          <span className="h-2 w-2 rounded-full bg-emerald-500" />
          Agente Online
        </div>

        <div className="h-8 w-8 rounded-full bg-zinc-800" />
      </div>
    </header>
  );
}