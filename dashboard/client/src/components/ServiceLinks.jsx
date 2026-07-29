import { useState, useEffect } from "react";
import { api } from "../api/requests";
import "../style/ServiceLinks.css";


function applicationButton({ application }){
    // This will create the button which is unique to each application which is any application container
    
    // need an async function buttonAction(containerID) {}


    return (
        <button>click</button>
    );
}


function ServiceLinks () {
    // fetch the endpoint to get all the applications and its info.


    // remove the h2 line and just have buttons inside the one section tag should be good
    return (
        <section className="ServiceLinks-panel">
            <h2 className="panel-title">SerivceLinks</h2>

            
        </section>
    )
}

export default ServiceLinks;