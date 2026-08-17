import { useState, useEffect } from "react";
import { api } from "../api/requests";
import { getContainers } from "../api/getContainers";
import "../style/ServiceLinks.css";

const protoccol = window.location.protocol;
const hostName = window.location.hostname;


function ApplicationButton({ containerID, applicationName, port }){
    // This will create the button which is unique to each application which is any application container
    
    // need an async function buttonAction(containerID) {}

    // need to get the first letter of the name of the application
    let letter;
    if (applicationName === "" || applicationName == " "){
        letter = "?";
    }else{
        letter = applicationName[0];
    }

    const redirectLink = `${protoccol}//${hostName}:${port}`;
    return (
        <li>
            <button onClick={() => window.location.href=redirectLink} className="application-button">
                <div className="application-button-info">
                    <div className="application-button-logo">
                        <span className="">{letter}</span>
                    </div>
                    <p className="application-name">{applicationName}</p>
                    <p className="open-tag">Open &rarr;</p>
                </div>

                <div className="port-info">
                    {port === null ? <p>No Port</p> : <p>{port}</p>}
                </div>
            </button>
        </li>
    );
}


function ServiceLinks () {
    // fetch the endpoint to get all the applications and its info.
    const {containers, error, retry} = getContainers();

    // remove the h2 line and just have buttons inside the one section tag should be good
    return (
        <section className="ServiceLinks-panel">
            <h2 className="panel-title">SerivceLinks</h2>

            <ul className="serviceLinks-list">
                {containers.map(container => {
                    return (
                        <ApplicationButton 
                            key={container.id}
                            containerID={container.id}
                            applicationName={container.name} 
                            port={container.port}/>
                    );
                })}

            </ul>
            
        </section>
    )
}

export default ServiceLinks;