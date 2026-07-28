import { useState, useEffect } from "react";
import { api } from "../api/requests";
import "../style/SystemOverview.css";


function ServerInfo({ info }) {
    return (
        <section className="serverInfo">
            <h3>Server Info</h3>
            <dl>
                <dt>CPU Model: </dt>
                <dd>{info.cpuModel || "Unknown"}</dd>

                <dt>CPU Cores: </dt>
                <dd>{info.cpuCore || "Unknown"}</dd>

                <dt>Total Memory: </dt>
                <dd>{info.totalMemory || "Unknown"}</dd>

                <dt>OS Name: </dt>
                <dd>{info.osName || "Unknown"}</dd>

                <dt>OS Version: </dt>
                <dd>{info.osVersion || "Unknown"}</dd>
            </dl>
        </section>
    )
}

function SystemMetrics({ metrics }) {
    console.group(metrics.cpuMetric);
    return(
        <section className="SystemMetrics">
            <h3>System Metrics</h3>
            <dl>
                <dt>CPU Usuage: </dt>
                <dd>{metrics.cpuMetric}</dd>

                <dt>Memory Usuage: </dt>
                <dd>{metrics.memoryMetric}</dd>
            </dl>
        </section>
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
            <h2 className="panel-title">SystemOverview</h2>
            <ServerInfo info={serverInfo}/>
            <SystemMetrics metrics={metricsInfo}/>
        </section>
    )
}

export default SystemOverview;