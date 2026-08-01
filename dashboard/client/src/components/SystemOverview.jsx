import { useState, useEffect } from "react";
import { api } from "../api/requests";
import "../style/SystemOverview.css";

function CpuInfoCard ({ info }) {
    return (
        <div className="stat-card">
            <p className="stat-label">CPU Cores: </p>
            <p className="stat-value">{info.cpuCore || "Unknown"}</p>
            <p className="stat-subtext">CPU: {info.cpuModel || "Unknown"}</p>
        </div>
    )
}

function MemoryInfoCard ({ info }) {
    return (
        <div className="stat-card">
            <p className="stat-label">Total Memory: </p>
            <p className="stat-value">
                {info.totalMemory || "Unknown"} <span className="stat-unit">GB</span>
            </p>
        </div>
    )
}

function CpuUsuageCard({ metrics }) {
  return (
    <div className="stat-card">
      <p className="stat-label">CPU usage</p>
      <p className="stat-value">{metrics.cpuMetric}%</p>

      <div className="stat-bar">
        <div className="stat-bar-fill" style={{ width: `${metrics.cpuMetric}%` }} />
      </div>
    </div>
  );
}

function MemoryUsuageCard ({ metrics }) {
    return(
        <div className="stat-card">
            <p className="stat-label">Memory Usuage: </p>
            <p className="stat-value">{metrics.memoryMetric}</p>

            <div className="stat-bar">
                <div className="stat-bar-fill" style={{ width: `${metrics.memoryMetric}%` }} />
            </div>
        </div>
    )
}


function SystemViewSection ({info, metrics}) {
    return (
        <div className="stat-grid">
            <CpuUsuageCard metrics={metrics} />
            <MemoryUsuageCard metrics={metrics} />
            <CpuInfoCard info={info} />
            <MemoryInfoCard info={info} />
        </div>
    )

}


function SystemOverview () {
    const [serverInfo, setServerInfo] = useState({});
    const [metricsInfo, setMetricsInfo] = useState({});
    const [error, setError] = useState(false);

    // fetching the serverInfo and the metrics info
    useEffect(() => {
        let isMounted = true;

        async function fetchData(apiCall, setState) {
            try {
                const response = await apiCall();
                if (isMounted) setState(response);
            } catch (err) {
                console.error(err);
                if (isMounted) setError(true);
            }
        }

        fetchData(api.getSystemInfo, setServerInfo);
        fetchData(api.getSystemMetrics, setMetricsInfo);

        return () => {
            isMounted = false;
        };
    }, []);
    
    return (
        <section className="System-panel">
            <h3>{serverInfo.osName} {serverInfo.osVersion}</h3>
            <SystemViewSection info={serverInfo} metrics={metricsInfo} />
        </section>
    )
}

export default SystemOverview;