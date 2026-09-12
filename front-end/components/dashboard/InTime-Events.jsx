const dados = [
    {
        items: [
            {
                key: "LoginFelipe",
                timeStamp: "10:04:12",
                event: "LOGIN_SUCESS",
                user: "Felipe",
                active: "server-01",
                attempts: "5"
            }
        ]
    }
]

export default function InTimeEvent() {
    return(
        <div className="w-full h-full rounded-md bg-gray-800 border-r border-zinc-800">
            <div className="w-full h-full">
                <div className="flex flex-row justify-between pr-6 pl-6 border rounded-t-md border-gray-700">
                    <h1 className="text-white text-lg">Eventos Ao vivo</h1>
                    <span className={`inline-block w-2 h-2`}></span>
                </div>
                 {dados.map((section) => (
                <div className="flex flex-row w-full h-full ">
                    <li className="gap-2" >
                        {section.items.map(item => (
                        <al key={item.key} className="px-3 py-2 border border-gray-700 pr-4 pl-4">
                            <span>
                                {item.timeStamp}
                            </span>
                            <span>
                                {item.event}
                            </span>
                            <span>
                                {item.user}
                            </span>
                            <span>
                                @ {item.active}
                            </span>
                            <span>
                                tentativa {item.attempts}
                            </span>
                        </al>
                        ))}
                    </li>
                </div>
                 ))}
            </div>

        </div>
    )
}