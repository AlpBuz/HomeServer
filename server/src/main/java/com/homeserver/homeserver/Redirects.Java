package com.homeserver.homeserver.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.HashMap;
/*
Handles all the redirects that happens to the different applcaiton running on the server
*/


@RestController
@RequestMapping("/redirect")
public class Redirects {
    // HashMap of all the redirect links for each current container in the system
    private final HashMap<String, String> redirects = new HashMap<>();

    public Redirects() {
        redirects.put("jellyfin", "http://jellyfin:8096");
        redirects.put("nextcloud", "http://nextcloud:9000");
    }

    @GetMapping("/{id}")
    public String redirect(@PathVariable String id) {
        // check if the container exists
        
        // check if the container is currently running

        // redirect the user or send an error message depending on the error
        return "redirecting to the jellyfin application";
    }
}