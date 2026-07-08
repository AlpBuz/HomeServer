package com.homeserver.homeserver.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
Handles all the redirects that happens to the different applcaiton running on the server
*/


@RestController
@RequestMapping("/redirect")
public class Redirects {
    @GetMapping("/jellyfin")
    public String redirect() {
        return "redirecting to the jellyfin application";
    }
}