package com.ksondzyk.HTTP.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.ksondzyk.HTTP.dto.Response;
import com.ksondzyk.HTTP.models.DateTime;
import com.ksondzyk.HTTP.services.DataTimeService;
import com.ksondzyk.HTTP.views.View;

import java.util.HashMap;
import java.util.Map;

public class ExampleHelloController {
    private static View view;

    public static void setView(View newView) {
        view = newView;
    }

    public static void login(HttpExchange httpExchange) {

        Response response = new Response();

        //response.setTemplate("datetime");
        Map<String, String> params = queryToMap(httpExchange.getRequestURI().getQuery());
        if (params.get("login").equals("me")&&params.get("password").equals("pass")) {
            response.setStatusCode(200);
            response.setData("Authorized");
        }
        else{
            response.setStatusCode(401);
            response.setData("Access denied");
        }
            response.setHttpExchange(httpExchange);

        view.view(response);
    }

    public static void get(HttpExchange httpExchange) {
        DateTime dateTime = DataTimeService.getCurrentDateTimeMinusThreeHours();

        Response response = new Response();

        response.setTemplate("datetime");
        Map<String, String> params = queryToMap(httpExchange.getRequestURI().getQuery());
        if (params.get("login").equals("me")&&params.get("password").equals("pass")) {
            response.setStatusCode(200);
            response.setData(dateTime);
        }
        else{
            response.setStatusCode(401);
        }
        response.setHttpExchange(httpExchange);

        view.view(response);
    }

    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}