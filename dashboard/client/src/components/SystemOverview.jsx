import { useState, useEffect } from "react";
import { api, pollingFunction } from "../api/requests";
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
            <p className="stat-subtext">{info.osName} {info.osVersion}</p>
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

    // fetching the serverInfo once
    useEffect(() => {
        async function fetchData(apiCall, setState) {
            try {
                const response = await apiCall();
                setState(response);
            } catch (err) {
                console.error(err);
                setError(true);
            }
        }

        fetchData(api.getSystemInfo, setServerInfo);
    }, []);

    useEffect(() => {
        const stopPolling = pollingFunction(
        api.getSystemMetrics,
        (data) => setMetricsInfo(data),
        5000 // interval
        );

        return () => {
        stopPolling(); // cleanup when component unmounts
        };
    }, []);
    
    return (
        <section className="System-panel">
            <SystemViewSection info={serverInfo} metrics={metricsInfo} />
        </section>
    )
}

export default SystemOverview;