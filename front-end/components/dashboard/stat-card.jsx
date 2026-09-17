export default function statCard({title, value}) {
    return(
        <div className="min-w-60 min-h-25 w-full rounded-md bg-(--card) border-r border-(--border)">
            <div className=" flex-auto justify-start m-2">
                    <a className="block px-3 py-2 text-sm text-muted">
                        {title}
                    </a>
                    <p className="block px-3 py-2 text-2xl text-foreground font-sans">
                        {value}
                    </p>
            </div>
        </div>
    )

}