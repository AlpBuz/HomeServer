package com.homeserver.util.alert;
import com.homeserver.homeserver.util.docker.DockerService;
import com.homeserver.homeserver.util.docker.ContainerInfo;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlertService {
    private final DockerService dockerService;

    public AlertService(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    // method to check on the status of the containers and return the ids of any containers that are down
    public void checkContainers() {
        // any containers that are currently down will be inserted into a list.
        // this list will be used with alertMessage to send me a message of downed containers
        
        // get the list of containers currently
        List<ContainerInfo> containers = dockerService.getContainers();
        List<String> downContainers = new ArrayList<String>();
        // loop through each container and check the status of each container, if one is down store id inside the return list
        for (ContainerInfo c : containers){
            if (!"running".equalsIgnoreCase(c.getState())){
                // save into the list else ignore
                downContainers.add(c.getId());
            }
        }

        // if there are any containers that are down send an alert message
        alertMessage(downContainers);
    }

    // method to send a response to me about a down container
    public void alertMessage(List<String> downContainers) {
        // email me of downed containers, or containers that are running at very high capacity for a while and need to be checked
    }
}