const dados = [
    {
        id: "123",
        items: [
                {
                    title: "123",
                    ativo: "Server",
                    severidade : "CRITICAL",
                    risco: "96",
                    status: "ABERTO"
                },
            ],
    },
    {
        id: "124",
        items: [
                {
                    title: "1241231241234123412312412412412342134",
                    ativo: "123.456.789",
                    severidade : "CRITICAL",
                    risco: "96",
                    status: "ABERTO"
                },
            ],
    },
]

const severidadeStyle = {
    LOW: "bg-green-900 text-green-400",
    MEDIUM: "bg-yellow-900 text-yellow-400",
    HIGH: "bg-orange-900 text-orange-400",
    CRITICAL: "bg-red-900 text-red-400",
};

const statusConfig = {
    ABERTO: {
        label: "Aberto",
        className: "bg-red-600",
    },
    EM_ANDAMENTO: {
        label: "Investigação",
        className: "bg-yellow-600",
    },
    RESOLVIDO: {
        label: "Resolvido",
        className: "bg-green-600",
    },
};


export default function recentAlerts() {
    return(
        <div className="w-full h-full min-w-140 rounded-md bg-gray-800 border-r border-zinc-800" >
            <div>
                <div className="flex flex-row justify-between p-4 border rounded-t-md border-gray-700">
                <h1 className="text-lg text-white">Incidentes recentes</h1>
                <a href="/alerts">Ver todos →</a>
                </div>
                <div>
                    <table className="w-full p-4 ">
                        <thead>
                            <tr className="min-h-10 border border-gray-700">
                                <th className="px-4 py-2  text-sm text-zinc-400">ID </th>
                                <th className="px-4 py-2  text-sm text-zinc-400" >Titulo</th>
                                <th className="px-4 py-2  text-sm text-zinc-400">Ativo</th>
                                <th className="px-4 py-2  text-sm text-zinc-400">Severidade</th>
                                <th className="px-4 py-2  text-sm text-zinc-400">Risco </th>
                                <th className="px-4 py-2  text-sm text-zinc-400">Status </th>
                            </tr>
                        </thead>
                    {dados.map((section) => (
                        <tbody key={section.id} className="h-full w-full ">
                            {section.items.map((item) => (
                            <tr key={section.id} className="min-h-10 h-auto border border-gray-700">
                                <td className="px-4 py-2 text-md text-white">#{section.id}</td>
                                <td className="max-w-[150px] px-4 py-2 text-md text-white whitespace-normal break-words">{item.title}</td>
                                <td className="px-4 py-2 text-md text-white ">{item.ativo}</td>
                                <td className="px-4 py-2 text-md">
                                    <span className={`px-4 py-2 rounded-full ${severidadeStyle[item.severidade]}`}>
                                        {item.severidade}
                                        </span>
                                </td>
                                <td className="px-4 py-2  text-md text-white text-wrap">{item.risco}</td>
                                <td className="px-4 py-2  text-md text-zinc-400 text-wrap">
                                    <span className="flex items-center gap-2">
                                        <span className={`inline-block h-2 w-2 rounded-full ${
                                                    statusConfig[item.status]?.className
                                                }`}>
                                        </span>
                                            {statusConfig[item.status]?.label}
                                    </span>
                                </td>
                            </tr>
                             ))}
                        </tbody>
                            ))}
                    </table>
                    <div>
                        <p></p>
                    </div>
                </div>
            </div>
        </div>
    )
}