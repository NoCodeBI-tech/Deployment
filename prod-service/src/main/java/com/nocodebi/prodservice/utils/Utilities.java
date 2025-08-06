package com.nocodebi.prodservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nocodebi.prodservice.constant.Constant;
import io.fabric8.kubernetes.api.model.Pod;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.time.Instant;

public class Utilities {

    public static boolean isNotNullOrBlank(String input) {

        return input != null && !input.trim().isEmpty();

    }

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

    public static String calculatePodAge(Instant startTime) {
        Duration duration = Duration.between(startTime, Instant.now());
        long days = duration.toDays();
        return days + "d";
    }

    public static String formatReady(Pod pod) {
        long readyCount = pod.getStatus().getContainerStatuses().stream().filter(cs -> cs.getReady()).count();
        int total = pod.getStatus().getContainerStatuses().size();
        return readyCount + "/" + total;
    }

    public static String getLinuxStyleDataPath() {
        String userHome = System.getProperty("user.home");
        String osName = System.getProperty("os.name").toLowerCase();

        String path = userHome;

        // Convert Windows-style paths (e.g., C:\\Users\\user\\data) to Linux-style (e.g., /c/Users/user/data)
        if (osName.contains("win")) {
            path = path.replace("\\", "/"); // Normalize slashes
            if (path.length() > 2 && path.charAt(1) == ':') {
                // Convert C:/Users/... to /c/Users/...
                char driveLetter = Character.toLowerCase(path.charAt(0));
                path = "/" + driveLetter + path.substring(2);
            }
        }

        return path;
    }

    public static boolean validateRequest(HttpServletRequest request) {

        JwtUtil jwtUtil = null;

        try {

            jwtUtil = new JwtUtil();

            return jwtUtil.validateToken(request.getHeader(Constant.AUTHORIZATION));

        } catch (Exception e) {

            e.printStackTrace();

            return false;

        } finally {

            jwtUtil = null;

        }

    }

}
