"use client"

import MarkButton from "../../../components/ui/markButton";
import { useSettings } from "../../providers/SettingsProvider";


export default function ConfigPage(){

    const { settings, updateSetting } = useSettings();

    return(
        <div className="w-full h-full flex flex-col min-w-250 min-h-250 gap-4">
            <div className="flex flex-col text-left">
                <h1 className="text-foreground text-3xl font-bold">Configurações</h1>
                <h2 className="text-(--muted) text-l">Marque as opções que você preferir</h2>
            </div>
            <div className="flex flex-col text-left h-full gap-4">
                <MarkButton title={"Dark theme"} 
                description={"Personlizar o sistema para o tema escuro"} 
                selected={settings.darkMode} 
                onChange={(value) => updateSetting("darkMode", value)}
                />

                <MarkButton title="Texto grande" 
                description="Aumenta o tamanho dos textos" 
                selected={settings.largeText} 
                onChange={(value) => updateSetting("largeText", value) } 
                /> 

                <MarkButton title="Alto contraste" 
                description="Aumenta o contraste dos elementos" 
                selected={settings.highContrast} 
                onChange={(value) => updateSetting("highContrast", value) } 
                /> 

                <MarkButton title="Animações" 
                description="Ativa as animações da aplicação" 
                selected={settings.animations} 
                onChange={(value) => updateSetting("animations", value) } 
                />
            </div>
        </div>
    )
}