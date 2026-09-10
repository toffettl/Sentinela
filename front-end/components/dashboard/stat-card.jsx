export default function statCard({title, value}) {
    return(
        <div className="max-w-60 min-h-25 w-50 rounded-md bg-gray-800 border-r border-zinc-800">
            <div className=" flex-auto justify-start m-2">
                    <a className="block px-3 py-2 text-sm text-zinc-400">
                        {title}
                    </a>
                    <p className="block px-3 py-2 text-2xl text-white font-sans">
                        {value}
                    </p>
            </div>
        </div>
    )

}