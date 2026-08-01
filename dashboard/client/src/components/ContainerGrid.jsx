import { useState, useEffect } from "react";
import { api } from "../api/requests";
const ACTIONS = ["Start", "Stop", "Restart"];
import "../style/ContainerGrid.css";





// component for each container card being created
function ContainerCard({ containerID, name, state, status}) {
    const [actionMessage, setActionMessage] = useState("");
    const [error, setError] = useState(false);

    async function buttonAction(action) {
        // console.log(`Action: ${action} performed on container ID: ${containerID}`);
        setError(false);
        const actions = {
            Start: api.startContainer,
            Stop: api.stopContainer,
            Restart: api.restartContainer,
        };

        const actionFunction = actions[action];

        if (!actionFunction) {
            console.log("not a valid action");
            setError(true);
            return;
        }

        try {
            const {success, message} = await actionFunction(containerID);
            //console.log(success, message);
            setActionMessage(message);
            
        } catch (err) {
            console.error(err);
            setError(true);
        }
    }


    return (
        <li className="Container-Card">
            <div className="Card-Info">
                <p className="Container-ID">ID: {containerID}</p>
                <p className="Container-Name">Name: {name}</p>
                <p className="Container-State">State: {state}</p>
                <p className="Container-Status">Status: {status}</p>
            </div>

            <div className="container-Actions">
                <p id="actionMessage">{actionMessage}</p>
                <button onClick={() => buttonAction("Start")}>Start</button>
                <button onClick={() => buttonAction("Stop")}>Stop</button>
                <button onClick={() => buttonAction("Restart")}>Restart</button>
            </div>
        </li>
    )
}

function ContainerGrid() {
    const [containers, setContainers] = useState([]); // will contain the list of containers
    const [error, setError] = useState(false); // flag to set for any errors that happens
    const [loading, setLoading] = useState(false); // flag if it is currently loading

    useEffect(() => {
        async function loadContainers() {
            try {
                setLoading(true);
                const response = await api.getContainers();
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
        <section className="container-panel">
            <h2 className="panel-title">Containers</h2>
            <ul className="container-list">
                {containers.map(container => {
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
            </ul>
        </section>
    )
}

export default ContainerGrid;