const dados = [
    {
        title: "LoginFelipe",
        items: [
            {
                timeStamp: "10:04:12",
                event: "LOGIN_SUCESS",
                user: "Felipe",
                active: "server-01",
                attempts: "5",
                level: "LOW",
            }
        ],
    },
    {   
        title: "BruteForce",
        items: [
            {
                timeStamp: "10:04:12",
                event: "BRUTE_FORCE",
                user: "Felipe",
                active: "server-01",
                attempts: "5",
                level: "CRITICAL",
            }
        ],
    },
]

const severidadeStyle = {
    LOW: "bg-green-900 text-green-400",
    MEDIUM: "bg-yellow-900 text-yellow-400",
    HIGH: "bg-orange-900 text-orange-400",
    CRITICAL: "bg-red-900 text-red-400",
};

export default function InTimeEvent() {
    return(
        <div className="w-full h-full min-w-130 rounded-md bg-gray-800 border-r border-zinc-800">
            <div className="w-full h-full">
                <div className="flex flex-row justify-between  p-4 border rounded-t-md border-gray-700">
                    <h1 className="text-white text-lg">Eventos Ao vivo</h1>
                    <span className={`inline w-2 h-2`}></span>
                </div>
                 {dados.map((section) => (
                <div key={section.title} className="flex flex-row">
                    <ul className="h-full w-full">
                            {section.items.map(item => (
                        <li key={section.title} className="flex flex-row border border-gray-700 pr-4 pl-4 px-3 py-2  justify-between">
                                <span className="text-sm text-zinc-400">
                                    {item.timeStamp}
                                </span>
                                <span className={`px-3 py-1 rounded-full ${severidadeStyle[item.level]}`}>
                                    {item.event}
                                </span>
                                <span className="text-sm text-zinc-400">
                                    {item.user}
                                </span>
                                <span className="text-sm text-zinc-400">
                                    @ {item.active}
                                </span>
                                <span className="text-sm text-zinc-400">
                                    tentativa {item.attempts}
                                </span>
                        </li>
                            ))}
                    </ul>
                </div>
                 ))}
            </div>

        </div>
    )
}