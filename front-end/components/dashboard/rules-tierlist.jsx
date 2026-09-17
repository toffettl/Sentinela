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
        <div className="w-full h-full rounded-md bg-(--card) border border-(--border)">

            <div className="flex justify-between p-4 border rounded-t-md border-(--cardBord)">
                <h1 className="text-foreground text-lg">
                    Regras mais acionadas
                </h1>

                <span className="text-sm text-(--muted)">
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

                                    <span className="text-sm text-foreground">
                                        {item.rule}
                                    </span>

                                    <span className="text-xs text-(--muted)">
                                        ({item.name})
                                    </span>
                                </div>

                                <span className="text-sm text-(--muted)">
                                    {item.value}
                                </span>

                            </div>

                            <div className="w-full h-2 bg-(--cardBord) rounded-full">
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
                        <span className="inline w-full h-2  rounded-lg bg-gray-900 border-gray-900"></span>
                        <div className="flex flex-wrap  items-center justify-between pt-4 border-t border-(--cardBord)">
                        {Object.entries(categoryTotals).map(([category, total]) => (
                            <div
                                key={category}
                                className="flex items-center gap-2"
                            >
                                <span
                                    className={`h-2 w-2 rounded-full ${categoryConfig[category]?.color}`}
                                />

                                <span className="text-sm text-(--muted)">
                                    {category}
                                </span>

                                <span className="text-sm text-foreground font-medium">
                                    {total}
                                </span>
                            </div>
                        ))}
                    </div>

            </div>

        </div>
    );
};