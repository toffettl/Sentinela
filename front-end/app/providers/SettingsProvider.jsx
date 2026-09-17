"use client"


import { createContext, useEffect, useState , useContext } from "react";

const SettingsContext = createContext();

export function SettingsProvider({children}){
    const [settings, setSettings] = useState({
        darkMode: false,
        largeText: false,
        highContrast: false,
        colorBlindMode: false,
        animations: true,
        sound: true,
    });

    useEffect(() => {
        const savedSettings = localStorage.getItem("app_settings");
        if (savedSettings) {
            try {
                setSettings(JSON.parse(savedSettings));
            } catch (e) {
                console.error("Erro ao carregar configurações do localStorage", e);
            }
        }
    }, []);

     function updateSetting(name, value) {
        setSettings(prev => {
            const updated = { ...prev, [name]: value };
            localStorage.setItem("app_settings", JSON.stringify(updated)); // Salva a mudança
            return updated;
        });
    }

     useEffect(() => { const html = document.documentElement; 
        html.classList.toggle( "dark", settings.darkMode ); 
        html.classList.toggle( "large-text", settings.largeText ); 
        html.classList.toggle( "high-contrast", settings.highContrast ); 
        html.classList.toggle( "color-blind", settings.colorBlindMode ); 
        html.classList.toggle( "reduce-motion", settings.reduceMotion ); 
    }, [settings]);

    return (
    <SettingsContext.Provider value={{ settings, updateSetting }} >
    {children}
    </SettingsContext.Provider> 
    );
}

export function useSettings() {
    return useContext(SettingsContext);
}