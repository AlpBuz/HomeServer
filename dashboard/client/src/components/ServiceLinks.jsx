import { useState, useEffect } from "react";
import { api } from "../api/requests";
import { getContainers } from "../api/getContainers";
import "../style/ServiceLinks.css";


function ApplicationButton({ application }){
    // This will create the button which is unique to each application which is any application container
    
    // need an async function buttonAction(containerID) {}


    return (
        <li>
            <button>click</button>
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
                        <ApplicationButton key={container.id}
                            application={container}/>
                    );
                })}

            </ul>
            
        </section>
    )
}

export default ServiceLinks;