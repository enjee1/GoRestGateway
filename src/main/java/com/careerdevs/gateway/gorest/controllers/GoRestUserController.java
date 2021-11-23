package com.careerdevs.gateway.gorest.controllers;

import com.careerdevs.gateway.gorest.models.GoRestResponse;
import com.careerdevs.gateway.gorest.models.GoRestResponseMulti;
import com.careerdevs.gateway.gorest.models.GoRestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;

@RestController
@RequestMapping("/api/gorest/users")
public class GoRestUserController {
    @Autowired
    private Environment env;

    /*
    TODO: Make GoRest URL a constant instead of environment variable.
    TODO: Make v2 and v3 of ("/put")
    TODO: Make most advanced version's of put, post, and delete (video from 11/22/2021)
    */

    // This is the root route handler for all HTTP methods in the RequestMapping annotation for /api/gorest/users
    @RequestMapping("/")
    public String test() {
        return "Default response for all HTTP methods for root of \"/api/gorest/users\"";
    }

    @GetMapping("/pageone")
    public GoRestResponseMulti pageOne(RestTemplate restTemplate) {
        return restTemplate.getForObject(env.getProperty("gorest.url") + "/users/", GoRestResponseMulti.class);

    }

    @GetMapping("/allusers")
    public Object allUsers(RestTemplate restTemplate) {
        GoRestResponseMulti res = pageOne(restTemplate);
        int pageLimit = res.getMeta().getPagination().getPages();

        ArrayList<GoRestUser> allUsers = new ArrayList<>();
        Collections.addAll(allUsers, res.getData());

        for (int i = 2; i <= pageLimit; i++) {
            String tempURL = env.getProperty("gorest.url") + "/users/?page=";
            GoRestResponseMulti tempRes = restTemplate.getForObject(tempURL + i, GoRestResponseMulti.class);
            Collections.addAll(allUsers, tempRes.getData());
        }

        return allUsers;
    }

    @GetMapping("/page/{page}")
    public Object usersByPage(RestTemplate restTemplate,
                              @PathVariable(name = "page") String pageNum) {
        String tempURL = env.getProperty("gorest.url") + "/users/?page=";

        GoRestUser[] users = restTemplate.getForObject(tempURL + pageNum, GoRestResponseMulti.class).getData();

        return users;
    }

    @GetMapping("/get")
    public Object getUser
            (RestTemplate restTemplate,
             @RequestParam(name = "id") String id) {

        try {
            return restTemplate.getForObject(env.getProperty("gorest.url") + "/users/" + id, GoRestResponse.class).getData();
        } catch (HttpClientErrorException.NotFound exc) {
            return "ID did not a match a user in the database";
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
            return exc.getMessage();
        }

    }

    @PostMapping("/post")
    public Object postUser(
            RestTemplate restTemplate,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "gender") String gender,
            @RequestParam(name = "status") String status) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("gorest.token"));

            GoRestUser newUser = new GoRestUser(name, email, gender, status);
            HttpEntity request = new HttpEntity(newUser, headers);

            return restTemplate.exchange(env.getProperty("gorest.url") + "/users/", HttpMethod.POST, request, GoRestResponse.class);
        } catch (Exception exc) {
            System.out.println(exc.getClass());
            System.out.println(exc.getMessage());
            return exc.getMessage();
        }
    }

    @PostMapping("/post/v2")
    public Object postUserv2(
            RestTemplate restTemplate,
            @RequestBody GoRestUser user) {

        try {
            if (!user.getGender().equals("male") && !user.getGender().equals("female")) {
                return "Gender must be entered as male or female";
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("gorest.token"));

            HttpEntity<GoRestUser> request = new HttpEntity(user, headers);

            return restTemplate.exchange(env.getProperty("gorest.url") + "/users/", HttpMethod.POST, request, GoRestResponse.class);
        } catch (Exception exc) {
            System.out.println(exc.getClass());
            System.out.println(exc.getMessage());
            return exc.getMessage();
        }
    }

    @PutMapping("/put")
    public Object putUser(RestTemplate restTemplate,
                          @RequestParam(name = "name", required = false) String name,
                          @RequestParam(name = "email", required = false) String email,
                          @RequestParam(name = "gender", required = false) String gender,
                          @RequestParam(name = "status", required = false) String status,
                          @RequestParam(name = "id") String id) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("gorest.token"));

            GoRestUser user = new GoRestUser(name, email, gender, status);

            HttpEntity<GoRestUser> request = new HttpEntity(user, headers);

            System.out.println("Entry for " + id + " updated successfully.");
            return restTemplate.exchange(env.getProperty("gorest.url") + "/users/" + id, HttpMethod.PUT, request, GoRestResponse.class);


        } catch (HttpClientErrorException.NotFound exc) {
            return "ID did not a match a user in the database";
        } catch (Exception exc) {
            return exc.getMessage();
        }

    }


    @DeleteMapping("/delete")
    public String deleteUser(
            RestTemplate restTemplate,
            @RequestParam(name = "id") String id) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("gorest.token"));

            HttpEntity request = new HttpEntity(headers);

            restTemplate.exchange((env.getProperty("gorest.url") + "/users/" + id), HttpMethod.DELETE, request, GoRestResponse.class);
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


    @DeleteMapping("/deletealt/{id}")
    public String deleteUserAlt(
            RestTemplate restTemplate,
            @PathVariable(name = "id") String userID) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("gorest.token"));

            HttpEntity request = new HttpEntity(headers);

            restTemplate.exchange((env.getProperty("gorest.url") + "/users/" + userID), HttpMethod.DELETE, request, GoRestResponse.class);
            return "Successfully deleted user " + userID;
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



}
