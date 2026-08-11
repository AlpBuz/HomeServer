import { useState, useEffect, useRef } from "react";
import { FaPlay, FaStop, FaRedo } from "react-icons/fa";
import { api, pollingFunction } from "../api/requests";
import { getContainers } from "../api/getContainers"
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

    function StartButton() {
        return(
            <button className="container-button start-button" onClick={() => buttonAction("Start")}> <span className="button-icon"><FaPlay /></span></button>
        )
    }

    function StopButton() {
        return (
            <button className="container-button stop-button" onClick={() => buttonAction("Stop")}> <span className="button-icon"><FaStop /></span></button>
        )
    }

    return (
        <li className={`Container-Card ${state}`}>
            <div className="Card-Info">
                <p className="Container-Name">{name}</p>
                <p className="Container-Status">{status}</p>

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
                    <p className={`Container-State ${state === "running" ? "on" : "off"}`}>{state}</p>
                    {state == "running" ? <StopButton /> : <StartButton />}
                    <button className="restart-button" onClick={() => buttonAction("Restart")}><span className="button-icon"><FaRedo /></span></button>
                </div>
            </div>
        </li>
    );
}



function ContainerGrid() {
    const {containers, error, retryPolling} = getContainers();

    const activeContainers = containers.filter(c => c.state === "running").length;
    const downContainers = containers.length - activeContainers;

    // get the number of up and downed containers


    if (error) {
        return(
            <section className="container-grid-error">
                <p className="container-grid-error-message">An error occurred while fetching the containers</p>
                <button className="retry-button" onClick={retryPolling}>Retry request</button>
            </section>
        )
    }

    if (containers.length === 0) {
        return(
            <section className="container-grid-empty">
                <p className="container-grid-empty-message">No containers found</p>
                <button className="retry-button" onClick={retryPolling}>Retry request</button>
            </section>
        )
    }

    return (
        <section className="container-panel">
            <div className="container-panel-header">
                <h4 className="panel-title">CONTAINERS</h4>
                <div className="state-counter">
                    <p className="up-counter"><span>·</span> {activeContainers} up</p>
                    <p className="down-counter"><span>·</span> {downContainers} down</p>
                </div>
            </div>

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