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
        <h1>HomeServer</h1>
        <div>
          <p>{date}</p>
          <p>{time}</p>
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