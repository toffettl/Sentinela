import StatCard from "@/components/dashboard/stat-card";
import RecentAlerts from "../../components/dashboard/recent-alerts";
import InTimeEvent from "../../components/dashboard/InTime-Events";

export default function DashboardPage() {
  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-3xl font-bold">
        Painel de Segurança
      </h1>

      <p className="mt-2 text-zinc-400">
        Sentinela Monitores de segurança
      </p>

        <div className=" flex flex-row gap-4 w-full">
          <StatCard title={"Requisições feitas"} value={21312412412}/>
          <StatCard title={"IPs suspeitos"} value={2}/>
          <StatCard title={"Anomalias possiveis"} value={6}/>
          <StatCard title={"Logs coletados"} value={312323}/>
        </div>

        <div className="flex flex-row gap-2 w-full h-full min-h-100 min-w-200">

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