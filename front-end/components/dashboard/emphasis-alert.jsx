export default function empashisAlert ({score, data}) {
    const percentage = Math.min(Math.max(score, 0), 100);
    const degrees = percentage * 3.6;

    const statusConfig = {
    CRITICAL: {
        label: "Aberto",
        className: "bg-red-600",
    },
    MEDIUM: {
        label: "Investigação",
        className: "bg-yellow-600",
    },
    LOW: {
        label: "Resolvido",
        className: "bg-green-600",
    },
};

    return(
        <div className="w-full min-w-150 rounded-md bg-gray-800 border border-zinc-800">
            <div className="flex justify-between p-4 border rounded-t-md border-gray-700">
                <span className="text-white text-lg">Incidente em destaque</span>
                <a href="/investigacao">Abrir Investigação →</a>
            </div>

            <div className="flex flex-row gap-10 content-center-safe w-full h-full">
                     <div
                        className="relative h-56 w-56 rounded-full m-6"
                        style={{
                            background: `conic-gradient(
                                #f43f5e 0deg ${degrees}deg,
                                #272b35 ${degrees}deg 360deg
                            )`,
                        }}
                    >
                        <div className="absolute inset-2 flex flex-col items-center justify-center rounded-full bg-[#11151d]">
                            <span className="text-2xl font-bold text-white">
                                {score}
                            </span>

                            <span className="text-[10px] uppercase text-zinc-500">
                                Risk Score
                            </span>
                        </div>
                    </div>
                <div className="flex flex-col gap-2">
                        <div className="flex flex-col m-2 gap-2">
                            <h1 className="text-white text-lg">Possível comprometimento de conta</h1>
                            <div className="flex flex-row gap-2">
                                <div className="flex flex-row gap-2">
                                <span className="text-zinc-500 text-sm">Usuário</span>
                                 <p className="text-white text-sm">felipe</p>
                                </div>
                                <div className="flex flex-row gap-2">
                                <span className="text-zinc-500 text-sm">IP</span>
                                 <p className="text-white text-sm">192.150.1.50</p>
                                </div>
                                <div className="flex flex-row gap-2">
                                <span className="text-zinc-500 text-sm">Ativo</span>
                                 <p className="text-white text-sm">Server-01</p>
                                </div>
                            </div>
                        </div>
                        <div className="w-full h-full">
                            <ul className="flex flex-col">
                                <li className="py-2 px-3 ">
                                    <span className={`inline-block h-2 w-2 rounded-full ${
                                                    statusConfig["CRITICAL"]?.className
                                                }`}> </span>
                                    <span className="text-gray-300 text-md">  Brute force - 5 tentivas em 14s</span>
                                </li>
                                <li className="py-2 px-3 ">
                                    <span className={`inline-block h-2 w-2 rounded-full ${
                                                    statusConfig["CRITICAL"]?.className
                                                }`}></span>
                                    <span className="text-gray-300 text-md">  Brute force - 5 tentivas em 14s</span>
                                </li>
                                <li className="py-2 px-3 ">
                                    <span className={`inline-block h-2 w-2 rounded-full ${
                                                    statusConfig["CRITICAL"]?.className
                                                }`}></span>
                                    <span className="text-gray-300 text-md">  Brute force - 5 tentivas em 14s</span>
                                </li>
                                <li className="py-2 px-3">
                                    <span className={`inline-block h-2 w-2 rounded-full ${
                                                    statusConfig["CRITICAL"]?.className
                                                }`}></span>
                                    <span className="text-gray-300 text-md">  Brute force - 5 tentivas em 14s</span>
                                </li>
                            </ul>
                        </div>
                        <div className="flex flex-row gap-4 w-full h-full">
                            <button className="min-w-35 w-full h-10 rounded-lg bg-blue-400 text-white text-md"> Investigar agora</button>
                            <button className="min-w-45 w-full h-10  rounded-lg bg-black text-white text-md">Falso positivo</button>
                        </div>
                </div>
            </div>
        </div>
    )
}