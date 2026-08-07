import { useState, useEffect } from "react";
import { api, pollingFunction } from "../api/requests";
import { useSystemInfo } from '../api/useSystemInfo'
import { useSystemMetrics } from '../api/useSystemMetrics'
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


// function SystemViewSection ({info, metrics}) {
//     return (
//         <div className="stat-grid">
//             <CpuUsuageCard metrics={metrics} />
//             <MemoryUsuageCard metrics={metrics} />
//             <CpuInfoCard info={info} />
//             <MemoryInfoCard info={info} />
//         </div>
//     )

// }


function SystemOverview () {
    const { serverInfo, error: infoError } = useSystemInfo();
    const { metricsInfo, error: metricsError, retry } = useSystemMetrics();

    return (
        <section className="System-panel">
            <CpuUsuageCard metrics={metricsInfo} />
            <MemoryUsuageCard metrics={metricsInfo} />
            <CpuInfoCard info={serverInfo} />
            <MemoryInfoCard info={serverInfo} />
        </section>
    )
}

export default SystemOverview;