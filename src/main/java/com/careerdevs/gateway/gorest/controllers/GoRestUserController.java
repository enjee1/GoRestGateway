package com.careerdevs.gateway.gorest.controllers;

import com.careerdevs.gateway.gorest.models.GoRestResponse;
import com.careerdevs.gateway.gorest.models.GoRestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@RestController
@RequestMapping("/api/gorest/user")
public class GoRestUserController {
    @Autowired
    private Environment env;

    @GetMapping("/pageone")
    public Object allUsers(RestTemplate restTemplate) {
        return restTemplate.getForObject("https://gorest.co.in/public/v1/users/", GoRestResponse.class).getData();

    }

    @PostMapping("/post")
    public Object postUser(
            RestTemplate restTemplate,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "gender") String gender,
            @RequestParam(name = "status") String status) {

        String url = "https://gorest.co.in/public/v1/users/";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("gorest.token"));

            GoRestUser newUser = new GoRestUser(name, email, gender, status);
            HttpEntity request = new HttpEntity(newUser, headers);

            return restTemplate.exchange(url, HttpMethod.POST, request, GoRestResponse.class);
        } catch (Exception exc) {
            System.out.println(exc.getClass());
            System.out.println(exc.getMessage());
            return exc.getMessage();
        }
    }

    @GetMapping("/get")
    public Object getUser(RestTemplate restTemplate, @RequestParam(name = "id") String id) {
        String url = "https://gorest.co.in/public/v1/users/" + id;
        try {
            return restTemplate.getForObject(url, GoRestResponse.class).getData();
        } catch (HttpClientErrorException.NotFound exc) {
            return "ID did not a match a user in the database";
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
            return exc.getMessage();
        }


    }

    @DeleteMapping("/delete")
    public String deleteUser(
            RestTemplate restTemplate,
            @RequestParam(name = "id") String id) {
        String url = "https://gorest.co.in/public/v1/users/" + id;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("gorest.token"));

            HttpEntity request = new HttpEntity(headers);

            restTemplate.exchange(url, HttpMethod.DELETE, request, GoRestResponse.class);
            return "Successfully deleted user " + id;
        } catch (HttpClientErrorException.Unauthorized exc) {
            return "You need to have authorization";
        } catch (HttpClientErrorException.NotFound exc) {
            return "ID did not a match a user in the database";
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
            return exc.getMessage();
        }
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
