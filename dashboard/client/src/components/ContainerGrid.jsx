import { useState, useEffect, useRef } from "react";
import { FaPlay, FaStop, FaRedo } from "react-icons/fa";
import { api, pollingFunction } from "../api/requests";
const ACTIONS = ["Start", "Stop", "Restart"];
import "../style/ContainerGrid.css";


// component for each container card being created
function ContainerCard({ containerID, name, state, status }) {
    const [actionMessage, setActionMessage] = useState("");
    const [fadeOut, setFadeOut] = useState(false);
    const [error, setError] = useState(false);

    const fadeTimer = useRef(null);
    const removeTimer = useRef(null);



    useEffect(() => {
        return () => {
            clearTimeout(fadeTimer.current);
            clearTimeout(removeTimer.current);
        };
    }, []);

    async function buttonAction(action) {
        setError(false);

        const actions = {
            Start: api.startContainer,
            Stop: api.stopContainer,
            Restart: api.restartContainer,
        };

        const actionFunction = actions[action];

        if (!actionFunction) {
            setError(true);
            return;
        }

        try {
            const { message } = await actionFunction(containerID);

            // Cancel any previous timers
            clearTimeout(fadeTimer.current);
            clearTimeout(removeTimer.current);

            setActionMessage(message);
            setFadeOut(false);

            // Fade after 2.5 seconds
            fadeTimer.current = setTimeout(() => {
                setFadeOut(true);
            }, 2500);

            // Remove after 3 seconds
            removeTimer.current = setTimeout(() => {
                setActionMessage("");
                setFadeOut(false);
            }, 3000);

        } catch (err) {
            console.error(err);
            setError(true);
        }
    }

    return (
        <li className={`Container-Card ${state}`}>
            <div className="Card-Info">
                <div>
                    <p className="Container-Name">Name: {name}</p>
                    <p className="Container-Status">Status: {status}</p>
                </div>

                <p className="Container-State">State: {state}</p>
            </div>

            <div className="container-Actions">
                {actionMessage && (
                    <p className={`action-message ${fadeOut ? "fade-out" : ""}`}>
                        {actionMessage}
                    </p>
                )}

                {error && (
                    <p className="error-message">
                        Something went wrong.
                    </p>
                )}

                <div className="buttons">
                    <button className="container-button" onClick={() => buttonAction("Start")}> <span className="button-icon"><FaPlay /></span> Start</button>
                    <button className="container-button" onClick={() => buttonAction("Stop")}> <span className="button-icon"><FaStop /></span> Stop</button>
                    <button className="container-button" onClick={() => buttonAction("Restart")}><span className="button-icon"><FaRedo /></span> Restart</button>
                </div>
            </div>
        </li>
    );
}



function ContainerGrid({containers, error}) {
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