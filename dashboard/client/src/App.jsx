import { useState, useEffect, useCallback, useRef } from 'react'
import ContainerGrid from './components/ContainerGrid'
import ServiceLinks from './components/ServiceLinks'
import SystemOverview from './components/SystemOverview'
import { api, pollingFunction } from "./api/requests";
import { getDate, getTime } from "./api/helper"
import './style/App.css'

// This will be the admin page but for now it is just the main page.
// Will need to add authentication soon but later.
function App() {
  // SystemOverview information is stored here
  const [serverInfo, setServerInfo] = useState({});
  const [metricsInfo, setMetricsInfo] = useState({});
  const [systemOverviewError, setSystemOverviewError] = useState(false);

  // ContainerGrid information is stored here
  const [containers, setContainers] = useState([]);
  const [containerError, setContainerError] = useState(false);


  // refs to hold the "stop polling" functions returned by pollingFunction,
  // so an error handler or the retry button can call them directly
  const stopContainerPollingRef = useRef(null);
  const stopMetricsPollingRef = useRef(null);

  useEffect(() => {
    const clockId = setInterval(() => setNow(new Date()), 1000);
    return () => clearInterval(clockId);
  }, []);

  // fetch information that only needs to be obtained once
  useEffect(() => {
    async function fetchData(apiCall, setState) {
      try {
        const response = await apiCall();
        setState(response);
      } catch (err) {
        console.error(err);
        setSystemOverviewError(true);
      }
    }

    fetchData(api.getSystemInfo, setServerInfo);
  }, []);

  // container fetch, reusable so the retry button can call it directly
  const fetchContainers = useCallback(async () => {
    try {
      const data = await api.getApplicationContainers();
      setContainers(data);
      setContainerError(false);
    } catch (err) {
      console.error(err);
      setContainerError(true);
      // stop polling as soon as we hit an error
      stopContainerPollingRef.current?.();
    }
  }, []);

  // metrics fetch, reusable for the same reason
  const fetchMetrics = useCallback(async () => {
    try {
      const data = await api.getSystemMetrics();
      setMetricsInfo(data);
      setSystemOverviewError(false);
    } catch (err) {
      console.error(err);
      setSystemOverviewError(true);
      stopMetricsPollingRef.current?.();
    }
  }, []);

  // getting the container information with this polling
  useEffect(() => {
    stopContainerPollingRef.current = pollingFunction(fetchContainers, () => {}, 5000);
    return () => stopContainerPollingRef.current?.();
  }, [fetchContainers]);

  // getting the system metrics with this polling
  useEffect(() => {
    stopMetricsPollingRef.current = pollingFunction(fetchMetrics, () => {}, 5000);
    return () => stopMetricsPollingRef.current?.();
  }, [fetchMetrics]);

  // retrying the polling if the polling fails for some reason
  const retryContainerPolling = useCallback(() => {
    // in case a stale poller is somehow still running, stop it first
    stopContainerPollingRef.current?.();
    fetchContainers(); // immediate refetch
    stopContainerPollingRef.current = pollingFunction(fetchContainers, () => {}, 5000);
  }, [fetchContainers]);

  const retryMetricsPolling = useCallback(() => {
    stopMetricsPollingRef.current?.();
    fetchMetrics();
    stopMetricsPollingRef.current = pollingFunction(fetchMetrics, () => {}, 5000);
  }, [fetchMetrics]);

  const date = getDate();
  const time = getTime();

  return (
    <div className="homeserver">
      <header className='homeserver-sidebar'>
        <h1>HomeServer</h1>
        <div>
          <p>{date}</p>
          <p>{time}</p>
        </div>
      </header>
      <main className='homeserver-main'>
        <SystemOverview
          serverInfo={serverInfo}
          metricsInfo={metricsInfo}
          error={systemOverviewError}
        />

        <div className="features-panel">
          <ServiceLinks />
          <ContainerGrid
            containers={containers}
            error={containerError}
            retryPolling={retryContainerPolling}
          />
        </div>
      </main>
    </div>
  )
}

export default App