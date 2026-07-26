import { useState, useEffect } from "react";
import { api } from "../api/requests";
import "../style/SystemOverview.css";


function ServerInfo({ info }) {
    return (
        <section>
            <h3>Server Info</h3>
            <dl>
                <dt>CPU Model</dt>
                <dd>{info.cpuModel}</dd>

                <dt>CPU Cores</dt>
                <dd>{info.cpuCore}</dd>

                <dt>Total Memory</dt>
                <dd>{info.totalMemory}</dd>

                <dt>OS Name</dt>
                <dd>{info.osName}</dd>

                <dt>OS Version</dt>
                <dd>{info.osVersion}</dd>
            </dl>
        </section>
    )
}

function SystemMetrics({ metrics }) {
    return(
        <section>
            <h3>System Metrics</h3>
            <dl>
                <dt>CPU Usuage</dt>
                <dd>{metrics.cpuMetric}</dd>

                <dt>Memory Usuage</dt>
                <dd>{metrics.memoryMetric}</dd>
            </dl>
        </section>
    )
}


function SystemOverview () {
    return (
        <section className="System-panel">
            <h2 className="panel-title">SystemOverview</h2>

            <div>
                <ServerInfo info={}/>
                <SystemMetrics metrics={}/>
            </div>
        </section>
    )
}

export default SystemOverview;