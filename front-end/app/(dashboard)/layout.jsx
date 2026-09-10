import Sidebar from "@/components/layout/sidebar";
import Header from "@/components/layout/header";

export default function DashboardLayout({ children }) {
  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-100">
      <Sidebar />

      <div className="ml-64">
        <Header />

        <main className="p-6">
          {children}
        </main>
      </div>
    </div>
  );
}