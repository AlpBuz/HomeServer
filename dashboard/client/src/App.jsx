import { useState } from 'react'
import ContainerGrid from './components/ContainerGrid'
import ServiceLinks from './components/ServiceLinks'
import SystemOverview from './components/SystemOverview'
import './App.css'

// This will be the admin page but for now it is just the main page.
// Will need to add authentication soon but later.
function App() {
  return (
    <div className="app">
      <header>
        <span>HomeServer</span>
      </header>

      <main>
        <ServiceLinks />

        <SystemOverview />

        <ContainerGrid />
      </main>
    </div>
  )
}

export default App