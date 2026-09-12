const dados = [
    {
        items: [
                {
                    id: "123",
                    title: "123",
                    ativo: "Server",
                    severidade : "CRITICAL",
                    risco: "96",
                    status: "ABERTO"
                },
            ],
    }
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
        <div className="w-full h-100 rounded-md bg-gray-800 border-r border-zinc-800" >
            <div>
                <div className="flex flex-row justify-between p-4 border rounded-t-md border-gray-700">
                <h1 className="text-md text-white">Incidentes recentes</h1>
                <a href="/alerts">Ver todos →</a>
                </div>
                <div>
                    {dados.map((section) => (
                    <table className="w-full p-4 ">
                        <thead>
                            <tr className="flex flex-cols justify-between min-h-10 px-2 py-2 pr-6 pl-6 border border-gray-700">
                                <th className="block  text-sm text-zinc-400">ID </th>
                                <th className="block  text-sm text-zinc-400" >Titulo</th>
                                <th className="block  text-sm text-zinc-400">Ativo</th>
                                <th className="block  text-sm text-zinc-400">Severidade</th>
                                <th className="block  text-sm text-zinc-400">Risco </th>
                                <th className="block  text-sm text-zinc-400">Status </th>
                            </tr>
                        </thead>
                        <tbody className="h-full w-full ">
                             {section.items.map((item) => (
                            <tr className="flex flex-cols justify-between min-h-15 px-2 py-2 pr-4 pl-4 border border-gray-700">
                                <td className="block  text-md text-white">#{item.id}</td>
                                <td className="block  text-md text-white">{item.title}</td>
                                <td className="block  text-md text-white">{item.ativo}</td>
                                <td className="block  text-md">
                                    <span className={`px-3 py-1 rounded-full ${severidadeStyle[item.severidade]}`}>
                                        {item.severidade}
                                        </span>
                                </td>
                                <td className="block  text-md text-white">{item.risco}</td>
                                <td className="block  text-md text-zinc-400">
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
                    </table>
                            ))}
                    <div>
                        <p></p>
                    </div>
                </div>
            </div>
        </div>
    )
}