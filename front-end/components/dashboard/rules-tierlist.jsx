export default function RulesTierList({data}) {
    const maxValue = Math.max(
        ...data.map((item) => item.value)
    );

    const categoryTotals = data.reduce((acc, item) => {
    acc[item.name] = (acc[item.name] || 0) + item.value;
    return acc;
}, {});

        const categoryConfig = {
        Normal: {
            color: "bg-green-500",
        },
        LOW: {
            color: "bg-yellow-300",
        },
        SUSPICIOUS: {
            color: "bg-orange-500",
        },
        CRITICAL: {
            color: "bg-red-600",
        },
    };

     return (
        <div className="w-full h-full rounded-md bg-gray-800 border border-zinc-800">

            <div className="flex justify-between p-4 border rounded-t-md border-gray-700">
                <h1 className="text-white text-lg">
                    Regras mais acionadas
                </h1>

                <span className="text-sm text-zinc-400">
                    Últimas 24h
                </span>
            </div>

            <div className="p-4">
                {data.map((item) => {

                    const percentage =
                        (item.value / maxValue) * 100;

                    return (
                        <div
                            key={item.rule}
                            className="mb-5"
                        >
                            <div className="flex justify-between mb-2">

                                <div className="flex items-center gap-2">
                                    <span
                                        className={`h-2 w-2 rounded-full ${item.color}`}
                                    />

                                    <span className="text-sm text-zinc-300">
                                        {item.rule}
                                    </span>

                                    <span className="text-xs text-zinc-500">
                                        ({item.name})
                                    </span>
                                </div>

                                <span className="text-sm text-zinc-400">
                                    {item.value}
                                </span>

                            </div>

                            <div className="w-full h-2 bg-gray-700 rounded-full">
                                <div
                                    className={`h-2 rounded-full ${item.color}`}
                                    style={{
                                        width: `${percentage}%`
                                    }}
                                />
                            </div>

                        </div>
                    );
                })}
                        <span className="inline-block w-full h-2  rounded-lg bg-gray-900 border-gray-900"></span>
                        <div className="flex flex-wrap  items-center justify-between pt-4 border-t border-gray-700">
                        {Object.entries(categoryTotals).map(([category, total]) => (
                            <div
                                key={category}
                                className="flex items-center gap-2"
                            >
                                <span
                                    className={`h-2 w-2 rounded-full ${categoryConfig[category]?.color}`}
                                />

                                <span className="text-sm text-zinc-400">
                                    {category}
                                </span>

                                <span className="text-sm text-white font-medium">
                                    {total}
                                </span>
                            </div>
                        ))}
                    </div>
                
            </div>

        </div>
    );
};