"use client"

import StatCard from "@/components/dashboard/stat-card";
import RecentAlerts from "@/components/dashboard/recent-alerts";
import InTimeEvent from "@/components/dashboard/InTime-Events";
import RulesTierList from "@/components/dashboard/rules-tierlist";
import EmpashisAlert from "@/components/dashboard/emphasis-alert";

import useEvents from "../../hooks/useEvents";
import useIncidentEvents from "../../hooks/useIncidentsEvents";
import useIncidents from "../../hooks/useIncidents";
import useDashboard from "@/hooks/useDashboard";
import { useMockDashboard } from "../../hooks/mocks/useMockDashboard";


export default function DashboardPage() {
   //const { events } = useEvents();
  // const {incidents} = useIncidents();
   //const { incidentsEvents } = useIncidentEvents();
   //const {dashboardData} = useDashboard();

   const {
        dashboard,
        loading,
        error
    } = useMockDashboard();

    if (loading) {
        return <p>Carregando dashboard...</p>;
    }

    if (error) {
        return <p>Erro ao carregar dashboard.</p>;
    }

    if (!dashboard) {
        return <p>Nenhum dado encontrado.</p>;
    }


  return (
    <div className="flex flex-col gap-4 h-full w-full min-w-250 min-h-250">
      <h1 className="text-3xl font-bold">
        Painel de Segurança
      </h1>

      <p className="mt-2 text-muted">
        Sentinela Monitores de segurança
      </p>

        <div className=" flex flex-row gap-4 w-full">
          <StatCard title={"Requisições feitas"} value={dashboard.totalEvents + dashboard.totalIncidents}/>
          <StatCard title={"IPs suspeitos"} value={dashboard.activeAssets}/>
          <StatCard title={"Anomalias possiveis"} value={dashboard.criticalIncidents}/>
          <StatCard title={"Logs coletados"} value={dashboard.highIncidents}/>
        </div>

        <div className="w-full h-full flex flex-row gap-4">

          <EmpashisAlert score={95}/>

          <RulesTierList
          data={[
              {
                  rule: "BruteForce",
                  name: "Normal",
                  value: 200,
                  color: "bg-green-500",
              },
              {
                  rule: "Login fora do horário",
                  name: "LOW",
                  value: 64,
                  color: "bg-yellow-300",
              },
              {
                  rule: "IP não reconhecido",
                  name: "SUSPICIOUS",
                  value: 10,
                  color: "bg-orange-500",
              },
              {
                  rule: "Download anormal",
                  name: "CRITICAL",
                  value: 4,
                  color: "bg-red-600",
              },
          ]}/>

        </div>
        <div className="flex flex-row gap-2 w-full h-full min-h-100">

          <div>
            <InTimeEvent/>
          </div>

          <div>
            <RecentAlerts/>
          </div>
        </div>
    </div>
  );
}