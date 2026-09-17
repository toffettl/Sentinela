export const mockEvents = [
  {
    id: 1,
    type: "LOGIN",
    timestamp: "2026-09-16T18:30:00",
    description: "Login realizado",
  },
];

export const mockIncidents = [
  {
    id: 10,
    severity: "HIGH",
    status: "OPEN",
    description: "Comportamento anômalo detectado",
  },
];

export const mockIncidentEvents = {
  10: [
    {
      id: 20,
      type: "LOGIN",
      timestamp: "2026-09-16T18:30:00",
    },
  ],
};

export const mockDashboard = {
        totalEvents: 30,
        totalIncidents:33 ,
        criticalIncidents: 4 ,
        highIncidents:10 ,
        activeAssets:11,
    };