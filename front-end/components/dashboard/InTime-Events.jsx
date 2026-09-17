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
        <div className="w-full h-full rounded-md bg-(--card) border-r border-(--border)">
                <div className="flex justify-between  p-4 border rounded-t-md border-(--cardBord)">
                    <h1 className="text-foreground text-lg">Eventos Ao vivo</h1>
                    <span className={`inline w-2 h-2 bg-green-500 rounded-lg`}></span>
                </div>
                 {dados.map((section) => (
                <div key={section.title} className="flex">
                    <ul className="h-full w-full">
                            {section.items.map(item => (
                        <li key={section.title} className="flex border border-(--cardBord) pr-4 pl-4 px-3 py-2 justify-between gap-4">
                                <span className="text-sm text-(--muted)">
                                    {item.timeStamp}
                                </span>
                                <span className={`px-3 py-1 rounded-full ${severidadeStyle[item.level]}`}>
                                    {item.event}
                                </span>
                                <span className="text-sm text-(--muted)">
                                    {item.user}
                                </span>
                                <span className="text-sm text-(--muted)">
                                    @ {item.active}
                                </span>
                                <span className="text-sm text-(--muted)">
                                    tentativa {item.attempts}
                                </span>
                        </li>
                            ))}
                    </ul>
                </div>
                 ))}
        </div>
    )
}