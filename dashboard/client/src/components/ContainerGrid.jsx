import { useState, useEffect } from "react";
import { api } from "../api/requests";
// TODO: 
// Need a useEffect to fetch the inital container information
// Need to establish a blank grid for no containers in the system
// Need to finish container cards and create the buttons
// need to establish the api calls

function useContainers () {
    // function is called to fetch the containers
    const [containers, setContainers] = useState([]); // will contain the list of containers
    const [error, setError] = useState(null); // flag to set for any errors that happens
}



// component for each container card being created
function ContainerCard({ containerID, name, state, status, cpuUsage, memoryUsage }) {
    return (
        <li className="Container-Card">
            <div className="Card-Info">
                <p>ID: {containerID}</p>
                <p>Name: {name}</p>
                <p>State: {state}</p>
                <p>Status: {status}</p>
                <p>CPU: {cpuUsage}%</p>
                <p>Memory: {memoryUsage} MB</p>
            </div>

            <div className="container-Actions">
                <button>Start</button>
                <button>Stop</button>
                <button>Restart</button>
            </div>
        </li>
    )
}

function ContainerGrid() {
    return (
        <section>
            <h2 className="panel-title">Containers</h2>
        </section>
    )
}

export default ContainerGrid;