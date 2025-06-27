package io.nocodebi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.beans.Expression;
import java.io.*;
import java.net.URI;
import java.net.URL;
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

    public static void setup_Wild_Card_Certificate(){

        // Resource path inside your JAR/resources folder
        String resourcePath = "scripts/setup-wildcard-cert.ps1";

        // Step 1: Extract resource to temp file
        File psScriptFile = extractResourceToTempFile(resourcePath);

        if( psScriptFile != null ){

            // Step 2: Run PowerShell on the extracted script file
            runPowerShellScript(psScriptFile.getAbsolutePath());

            // Optionally delete temp file on exit
            psScriptFile.deleteOnExit();

        }

    }

    public static void addEntryToHost(String URL){

        // Resource path inside your JAR/resources folder
        String resourcePath = "scripts/add-new-entry-in-host.ps1";

        // Step 1: Extract resource to temp file
        File psScriptFile = extractResourceToTempFile(resourcePath);

        if( psScriptFile != null ){

            String localURL = "local-".concat(URL);

            // Step 2: Run PowerShell on the extracted script file
            runPowerShellScript(psScriptFile.getAbsolutePath(), "-URL", localURL);

            // Optionally delete temp file on exit
            psScriptFile.deleteOnExit();

        }

    }

    private static File extractResourceToTempFile(String resourcePath) {

        try {

            InputStream inputStream = Utilities.class.getClassLoader().getResourceAsStream(resourcePath);

            if (inputStream == null) {

                throw new FileNotFoundException("Resource not found: " + resourcePath);

            }

            File tempFile = File.createTempFile("temp_ps_script_", ".ps1");

            try (FileOutputStream out = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[1024];

                int read;

                while ((read = inputStream.read(buffer)) != -1) {

                    out.write(buffer, 0, read);

                }

            }

            return tempFile;

        }catch (Exception e){

            e.printStackTrace();

            return null;

        }

    }

    private static void runPowerShellScript(String scriptPath, String... args) {

        try {

            // Prepare command: powershell.exe -ExecutionPolicy Bypass -File <scriptPath> <args>

            String[] command = new String[4 + args.length];

            command[0] = "powershell.exe";

            command[1] = "-ExecutionPolicy";

            command[2] = "Bypass";

            command[3] = "-File";

            command[4] = scriptPath;

            if( args.length > 0 ){

                System.arraycopy(args, 0, command, 5, args.length);

            }

            ProcessBuilder pb = new ProcessBuilder(command);

            Process process = pb.start();

            // Print output from PowerShell script
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    System.out.println(line);

                }

            }

            int exitCode = process.waitFor();

            System.out.println("PowerShell script exited with code: " + exitCode);

        }catch (Exception e){

            e.printStackTrace();

        }

    }

}
