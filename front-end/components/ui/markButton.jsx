"use client"

export default function MarkButton ({title, description, selected , onChange}) {

    return(
        <div className="flex p-2 gap-6">
           <button onClick={() => onChange(!selected)} className={`w-12 h-6 rounded-full p-1 transition-all mt-4 ${selected ? "bg-(--selectButtonBg)" :  "bg-(--notSelectButtonBg)"}`}>
                <div className={`w-4 h-4 rounded-full transition-all ${selected ? "bg-(--selectBallButton) translate-x-6":"bg-(--notSelectBallButton) translate-x-0"}`}/>
            </button>
            <div className="flex-col gap-2">
            <h1 className="text-foreground text-left text-lg">{title}</h1>
            <span className="text-(--muted) text-md text-left">• {description}</span>
            </div>
        </div>
    )
};