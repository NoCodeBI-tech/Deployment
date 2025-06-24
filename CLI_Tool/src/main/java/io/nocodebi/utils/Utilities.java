package io.nocodebi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.beans.Expression;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import io.nocodebi.cookieManager.CookieStoreManager;

public class Utilities {

    public static <T> T fromJson(Object object, Class<T> clazz) {

        Gson gson = new Gson();

        try {

            if (object instanceof String) {

                return gson.fromJson((String) object, clazz);

            } else if (object instanceof JsonNode) {

                return gson.fromJson(new ObjectMapper().writeValueAsString(object), clazz);

            }

        } catch (JsonSyntaxException | JsonProcessingException e) {

            throw new RuntimeException("Error converting Json to Object: " + e.getMessage(), e);

        } finally {

            gson = null;

        }

        return null;

    }

    public static String toJson(Object object) {

        Gson gson = new Gson();

        try {

            return gson.toJson(object);

        } catch (JsonSyntaxException e) {

            throw new RuntimeException("Error converting Object to String: " + e.getMessage(), e);

        } finally {

            gson = null;

        }
    }

    public static HttpResponse<String> apiCall(CookieStoreManager cookieStoreManager,
                                               String url, String endpoint,
                                               String body) {

        HttpResponse<String> response = null;

        String loginUrl = null;

        String cookieHeader = null;

        HttpClient client = null;

        HttpRequest request = null;

        try {

            loginUrl = url.concat(endpoint);

            cookieHeader = cookieStoreManager.getCookieHeader();

            client = cookieStoreManager.getClient();

            request = HttpRequest.newBuilder()
                    .uri(URI.create(loginUrl))
                    .header("Content-Type", "application/json")
                    .header("Cookie", cookieHeader)
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response;

        }catch (Exception e){

            e.printStackTrace();

            return response;

        }

    }


}
