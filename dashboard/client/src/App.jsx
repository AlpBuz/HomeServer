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
  const date = getDate();
  const time = getTime();
  const [currentTheme, setCurrentTheme] = useState('dark-theme')
  
  // fetch some of the info once
  const [info, setInfo] = useState(null);

  useEffect(() => {
    async function fetchData () {
      const response = await api.getSystemInfo();
      setInfo(response);
    }

    fetchData();
  }, []);

  // button function for switching between light mode and dark mode
  function switchTheme () {
    if (currentTheme == 'dark-theme'){
      // switch to light mode
      setCurrentTheme("light-theme");
      document.documentElement.setAttribute("data-theme", "light-theme");
    }else{
      // switching to dark mode
      setCurrentTheme("dark-theme");
      document.documentElement.setAttribute("data-theme", "dark-theme");
    }
  }


  function LightButton () {
    return (
      <button className='theme-button' onClick={switchTheme}>&#x2600; Light</button>
    )
  }

  function DarkButton () {
    return(
      <button className='theme-button' onClick={switchTheme}>&#x263E; Dark</button>
    )
  }

  return (
    <div className="homeserver">
      <header className='homeserver-sidebar'>
        <div className='homeserver-name'>
          <h3>HomeServer</h3>
          <span>·</span>
          <p>{info?.osName} {info?.osVersion}</p>
        </div>

        <div className='date-time'>
          {currentTheme == 'dark-theme' ? <DarkButton /> : <LightButton />}
          <p className='p-time'>{time}</p>
          <p className='p-date'>{date}</p>
        </div>
      </header>
      <main className='homeserver-main'>
        <SystemOverview />

        <div className="features-panel">
          <ServiceLinks />
          <ContainerGrid />
        </div>
      </main>
    </div>
  )
}

export default App