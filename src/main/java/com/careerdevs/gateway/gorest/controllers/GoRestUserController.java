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
    private final static String GRURL = "https://gorest.co.in/public/v1";

    /*
    TODO: (DONE 11/23/2021) Make GoRest URL a constant instead of environment variable.
    TODO: (DONE 11/26/2021) Make v2 and v3 of ("/put")
    TODO: (DONE 11/26/2021)Make most advanced versions of put, post, and delete (video from 11/22/2021)
    */

    // This is the root route handler for all HTTP methods in the RequestMapping annotation for /api/gorest/users
    @RequestMapping("/")
    public String test() {
        return "Default response for all HTTP methods for root of \"/api/gorest/users\"";
    }

    @GetMapping("/pageone")
    public GoRestResponseMulti pageOne(RestTemplate restTemplate) {
        return restTemplate.getForObject(GRURL + "/users/", GoRestResponseMulti.class);

    }

    @GetMapping("/allusers")
    public Object allUsers(RestTemplate restTemplate) {
        GoRestResponseMulti res = pageOne(restTemplate);
        int pageLimit = res.getMeta().getPagination().getPages();

        ArrayList<GoRestUser> allUsers = new ArrayList<>();
        Collections.addAll(allUsers, res.getData());

        String tempURL = GRURL + "/users?page=";
        // loop starts at 2 since pageOne() was called prior to iterating
        for (int i = 2; i <= pageLimit; i++) {
            GoRestResponseMulti tempRes = restTemplate.getForObject(tempURL + i, GoRestResponseMulti.class);
            Collections.addAll(allUsers, tempRes.getData());
        }

        return allUsers;
    }

    @GetMapping("/firstthreepages")
    public Object someUsers(RestTemplate restTemplate) {

        ArrayList<GoRestUser> someUsers = new ArrayList<>();

        String tempURL = GRURL + "/users?page=";
        for (int i = 1; i <= 3; i++) {
            GoRestResponseMulti tempRes = restTemplate.getForObject(tempURL + i, GoRestResponseMulti.class);
            Collections.addAll(someUsers, tempRes.getData());
        }

        return someUsers;
    }

    @GetMapping("/page/{page}")
    public Object usersByPage(RestTemplate restTemplate,
                              @PathVariable(name = "page") String pageNum) {
        GoRestResponseMulti res = pageOne(restTemplate);
        int pageLimit = res.getMeta().getPagination().getPages();
        String errorMsg = "Please enter a number greater than 0 and less than " + pageLimit + ".";
        GoRestUser[] users = null;
        String tempURL = GRURL + "/users?page=";

        try {
            if (Integer.parseInt(pageNum) > 0 && Integer.parseInt(pageNum) < pageLimit ) {
                users = restTemplate.getForObject(tempURL + pageNum, GoRestResponseMulti.class).getData();
            } else {
                return errorMsg;
            }
        } catch (NumberFormatException exc) {
            return errorMsg;
        } catch (Exception exc) {
            return errorMsg;
        }
        return users;
    }

    @GetMapping("/get")
    public Object getUser
            (RestTemplate restTemplate,
             @RequestParam(name = "id") String id) {

        try {
            return restTemplate.getForObject(GRURL + "/users/" + id, GoRestResponse.class).getData();
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

            return restTemplate.exchange(GRURL + "/users/", HttpMethod.POST, request, GoRestResponse.class);
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
            //The below two if blocks need to be combined to improve user experience.
            if (!user.getGender().equals("male") && !user.getGender().equals("female")) {
                return "Gender must be entered as male or female.";
            }

            if (!user.getStatus().equals("active") && !user.getStatus().equals("inactive")) {
                return "Gender must be entered as active or inactive.";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("gorest.token"));

            HttpEntity<GoRestUser> request = new HttpEntity(user, headers);

            return restTemplate.exchange(GRURL + "/users/", HttpMethod.POST, request, GoRestResponse.class);
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

            return restTemplate.exchange(GRURL + "/users/" + id, HttpMethod.PUT, request, GoRestResponse.class);


        } catch (HttpClientErrorException.NotFound exc) {
            return "ID did not a match a user in the database";
        } catch (Exception exc) {
            return exc.getMessage();
        }

    }


    @PutMapping("/putalt/{id}")
    public Object putUserAlt(RestTemplate restTemplate,
                             @PathVariable(name = "id") String id,
                             @RequestParam(name = "name", required = false) String name,
                             @RequestParam(name = "email", required = false) String email,
                             @RequestParam(name = "gender", required = false) String gender,
                             @RequestParam(name = "status", required = false) String status
                             ) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("gorest.token"));

            GoRestUser user = new GoRestUser(name, email, gender, status);

            HttpEntity<GoRestUser> request = new HttpEntity(user, headers);

            return restTemplate.exchange(GRURL + "/users/" + id, HttpMethod.PUT, request, GoRestResponse.class);


        } catch (HttpClientErrorException.NotFound exc) {
            return "ID did not a match a user in the database";
        } catch (Exception exc) {
            return exc.getMessage();
        }

    }

    @PutMapping("/putalt2/{id}")
    public Object putUserAlt2(RestTemplate restTemplate,
                             @PathVariable(name = "id") String id,
                             @RequestBody GoRestUser user) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("gorest.token"));

            HttpEntity<GoRestUser> request = new HttpEntity(user, headers);

            return restTemplate.exchange(GRURL + "/users/" + id, HttpMethod.PUT, request, GoRestResponse.class);


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
        String tempURL = GRURL + "/users/" + id;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("gorest.token"));

            HttpEntity request = new HttpEntity(headers);

            restTemplate.exchange(tempURL, HttpMethod.DELETE, request, GoRestResponse.class);
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

        String tempURL = GRURL + "/users/" + userID;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("gorest.token"));

            HttpEntity request = new HttpEntity(headers);

            restTemplate.exchange(tempURL, HttpMethod.DELETE, request, GoRestResponse.class);
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
