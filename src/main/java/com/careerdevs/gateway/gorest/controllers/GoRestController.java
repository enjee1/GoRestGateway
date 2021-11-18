package com.careerdevs.gateway.gorest.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/gorest")
public class GoRestController {

    @GetMapping("/allusers")
    public String allUsers(RestTemplate restTemplate) {
//        return restTemplate.getForObject("https://gorest.co.in/public/v1/users")
        return "temp";
    }


    @GetMapping("/test")
    public String testGetRoute() {
        return "GET route test successful";
    }

    @PostMapping("/test")
    public String testPostRoute() {
        return "POST route test successful";
    }

    @PutMapping("/test")
    public String testPutRoute() {
        return "PUT route test successful";
    }

    @DeleteMapping("/test")
    public String testDeleteRoute() {
        return "DELETE route test successful";
    }


    @RequestMapping("/secondtest")
    public String test() {
        return "Works for all CRUD actions";
    }
}
