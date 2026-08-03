import { useState, useEffect } from 'react'
import ContainerGrid from './components/ContainerGrid'
import ServiceLinks from './components/ServiceLinks'
import SystemOverview from './components/SystemOverview'
import { api, pollingFunction } from "./api/requests";
import './style/App.css'

// This will be the admin page but for now it is just the main page.
// Will need to add authentication soon but later.
function App() {
  // SystemOverview information is stored here
  const [serverInfo, setServerInfo] = useState({});
  const [metricsInfo, setMetricsInfo] = useState({});
  const [systemOverviewError, setSystemOverviewError] = useState(false);


  // ContainerGrid information is stored here
  const [containers, setContainers] = useState([]); // will contain the list of containers
  const [containerError, setContainerError] = useState(false); // flag to set for any errors that happens
  const [loading, setLoading] = useState(false); // flag if it is currently loading


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

  // other informations that are fetched each polling

  // getting the container information with this polling
  useEffect(() => {
    const stopPolling = pollingFunction(
    api.getContainers, // function to call
    (data) => setContainers(data), // what to do with response
    5000 // interval
    );

    return () => {
    stopPolling(); // cleanup when component unmounts
    };
  }, []);

  //getting the system metrics with this polling
  useEffect(() => {
    const stopPolling = pollingFunction(
    api.getSystemMetrics, // function to call
    (data) => setMetricsInfo(data), // what to do with response
    5000 // interval
    );

    return () => {
    stopPolling(); // cleanup when component unmounts
    };
  }, []);


  return (
    <div className="homeserver">
      <header className='homeserver-sidebar'>
        <h1>HomeServer</h1>
        <div>
          <p>10/24/2026</p>
          <p>1:42 PM</p>
        </div>
      </header>
      <main className='homeserver-main'>
        <SystemOverview serverInfo={serverInfo} metricsInfo={metricsInfo} error={systemOverviewError} /> 

        <div className="features-panel">
          <ServiceLinks />
          <ContainerGrid containers={containers} error={containerError} />
        </div>
      </main>
    </div>
  )
}

export default App