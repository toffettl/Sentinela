"use client";

import { useEffect, useState } from "react";
import { getMockDashboard } from "@/services/dashboard/dashboard.service";

export function useMockDashboard() {
    const [dashboard, setDashboard] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        async function loadDashboard() {
            try {
                setLoading(true);

                const data = await getMockDashboard();

                setDashboard(data);
            } catch (error) {
                setError(error);
            } finally {
                setLoading(false);
            }
        }

        loadDashboard();
    }, []);

    return {
        dashboard,
        loading,
        error,
    };
}