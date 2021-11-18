package com.careerdevs.gateway.gorest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gorest")
public class GoRestController {

    @GetMapping("/test")
    public String testMapping() {
        return "Test worked";
    }
}
