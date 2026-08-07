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

  return (
    <div className="homeserver">
      <header className='homeserver-sidebar'>
        <div className='homeserver-name'>
          <h3>HomeServer</h3>
          <span>·</span>
          <p>Ubuntu 24.04.4 LTS</p>
        </div>

        <div className='date-time'>
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