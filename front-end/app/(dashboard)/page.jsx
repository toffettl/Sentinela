import StatCard from "@/components/dashboard/stat-card";
import RecentAlerts from "@/components/dashboard/recent-alerts";
import InTimeEvent from "@/components/dashboard/InTime-Events";
import RulesTierList from "@/components/dashboard/rules-tierlist";
import EmpashisAlert from "@/components/dashboard/emphasis-alert";

export default function DashboardPage() {
  return (
    <div className="flex flex-col gap-4 h-full w-full min-w-250 min-h-250">
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