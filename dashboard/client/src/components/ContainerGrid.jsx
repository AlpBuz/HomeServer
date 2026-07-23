import { useState, useEffect } from "react";
import { api } from "../api/requests";
// TODO: 
// Need a useEffect to fetch the inital container information
// Need to establish a blank grid for no containers in the system
// Need to finish container cards and create the buttons
// need to establish the api calls



// component for each container card being created
function ContainerCard({ containerID, name, state, status}) {
    return (
        <li className="Container-Card">
            <div className="Card-Info">
                <p>ID: {containerID}</p>
                <p>Name: {name}</p>
                <p>State: {state}</p>
                <p>Status: {status}</p>
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
    const [containers, setContainers] = useState([]); // will contain the list of containers
    const [error, setError] = useState(false); // flag to set for any errors that happens
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        async function loadContainers() {
            try {
                setLoading(true);
                const response = await api.getContainers();
                console.group(response);
                setContainers(response);
                setError(false);
            } catch (err) {
                console.log(err);
                setError(true);
            } finally {
                setLoading(false);
            }
        }
        loadContainers();
    }, []);

    if (loading) {
        return (
            <section>
                <p>Loading...</p>
            </section>
        )
    }

    if (error) {
        return(
            <section>
                <p>An Error has occured when fetching the containers</p>
            </section>
        )
    }

    if (containers.length === 0) {
        return(
            <section>
                <p>No containers has been found</p>
            </section>
        )
    }

    return (
        <section>
            <h2 className="panel-title">Containers</h2>

            {containers.map(container => {
                console.log(container);

                return (
                    <ContainerCard
                        key={container.id}
                        containerID={container.id}
                        name={container.name}
                        state={container.state}
                        status={container.status}
                    />
                );
            })}
        </section>
    )
}

export default ContainerGrid;