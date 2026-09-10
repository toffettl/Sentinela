const navigation = [
  {
    title: "Overview",
    items: [
      { name: "Dashboard", href: "/" },
    ],
  },
  {
    title: "Monitores",
    items: [
      { name: "Alertas", href: "/alerts" },
      { name: "Eventos", href: "/events" },
      { name: "Servers", href: "/servers" },
      { name: "Endpoints", href: "/endpoints" },
      { name: "IP Endereços", href: "/ips" },
    ],
  },
  {
    title: "Sistema",
    items: [
      { name: "Configurações", href: "/alerts" },
    ],
  },
];

export default function Sidebar() {
  return (
    <aside className="fixed inset-y-0 left-0 z-40 w-64 border-r border-zinc-800 bg-zinc-950">
      <div className="flex h-16 items-center border-b border-zinc-800 px-6">
        <div>
          <h1 className="text-lg font-semibold tracking-tight">
            SENTINELA
          </h1>

          <p className="text-xs text-zinc-500">
            Detecção de risco
          </p>
        </div>
      </div>

      <nav className="p-4">
        {navigation.map((section) => (
          <div key={section.title} className="mb-6">
            <p className="mb-2 px-3 text-xs font-medium uppercase tracking-wider text-zinc-500">
              {section.title}
            </p>

            <div className="space-y-1">
              {section.items.map((item) => (
                <a
                  key={item.name}
                  href={item.href}
                  className="block rounded-md px-3 py-2 text-sm text-zinc-400 transition hover:bg-zinc-900 hover:text-zinc-100"
                >
                  {item.name}
                </a>
              ))}
            </div>
          </div>
        ))}
      </nav>
    </aside>
  );
}