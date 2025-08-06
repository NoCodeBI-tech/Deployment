package com.nocodebi.prodservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nocodebi.prodservice.constant.Constant;
import com.nocodebi.prodservice.methods.Installation;
import com.nocodebi.prodservice.model.*;
import com.nocodebi.prodservice.utils.Utilities;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CommandController {

    @PostMapping("/buildApplication")

    public ResponseEntity<Response> buildApplication(@RequestBody JsonNode request,
                                                     HttpServletRequest httpServletRequest) {

        ResponseTransaction transaction = new ResponseTransaction();

        AppContext context = Utilities.fromJson(request, AppContext.class);

        System.out.println("buildApplication : " + request);

        System.out.println("context : " + Utilities.toJson(context));

        Response response;

        if (!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    null,
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            context = Installation.buildApplication(context);

            if (context != null) {

                response = new Response(Constant.SUCCESS,
                        context,
                        Constant.OBJECT_CREATED_SUCCESSFULLY,
                        transaction);

            } else {

                response = new Response(Constant.ERROR,
                        null,
                        Constant.ERROR,
                        transaction);

            }

        }

        return ResponseEntity.ok(response);

    }

    @PostMapping("/deleteApplication")

    public ResponseEntity<Response> deleteApplication(@RequestBody JsonNode request,
                                                      HttpServletRequest httpServletRequest) {

        ResponseTransaction transaction = new ResponseTransaction();

        AppContext context = Utilities.fromJson(request, AppContext.class);

        Response response = null;

        if (!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    null,
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            context = Installation.uninstallApplication(context);

            if (context != null) {

                response = new Response(Constant.SUCCESS,
                        context,
                        Constant.OBJECT_DELETED_SUCCESSFULLY,
                        transaction);

            } else {

                response = new Response(Constant.ERROR,
                        null,
                        Constant.ERROR,
                        transaction);

            }

        }

        return ResponseEntity.ok(response);

    }

    @PostMapping("/resourceUsage")

    public ResponseEntity<Response> resourceUsage(@RequestBody JsonNode request,
                                                  HttpServletRequest httpServletRequest) {


        ResponseTransaction transaction = new ResponseTransaction();

        AppContext context = Utilities.fromJson(request.path("appContext"), AppContext.class);

        JsonNode instanceDetail = request.path("serverInstanceDetail");

        Gson gson = new Gson();

        Type listType = new TypeToken<List<ServerInstanceDetail>>() {
        }.getType();

        List<ServerInstanceDetail> serverInstanceDetails = gson.fromJson(String.valueOf(instanceDetail), listType);

        Response response = null;

        if (!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    null,
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            if (context != null) {

                List
                        <DeploymentMetricsSummary> deploymentMetricsSummaryList = Installation
                        .getDeploymentMetricsSummary(
                                context.getStageName() + context.getAppName(),
                                null);

                List<ServerInstanceDetail> usages = new ArrayList<>();

                deploymentMetricsSummaryList.forEach(instance -> {

                    String serverTag = Installation.
                            findServerTagFromDeploymentName(instance.getDeploymentName());

                    Optional<ServerInstanceDetail> matchedInstance = serverInstanceDetails
                            .stream()
                            .filter(i -> serverTag.equals(i.getName()))
                            .findFirst();

                    if (matchedInstance.isPresent()) {

                        ServerInstanceDetail found = matchedInstance.get();

                        found.setUsedCore(instance.getAvgCpu());

                        found.setUsedRam(instance.getAvgMemory());

                        usages.add(found);

                    }

                });

                response = new Response(Constant.SUCCESS,
                        usages,
                        Constant.OBJECT_RETRIEVE_SUCCESSFULLY,
                        transaction);

            } else {

                response = new Response(Constant.ERROR,
                        null,
                        Constant.ERROR,
                        transaction);

            }

        }

        return ResponseEntity.ok(response);

    }

    @PostMapping("/getBuildStatus")

    public ResponseEntity<Response> getBuildStatus(@RequestBody JsonNode request,

                                                   HttpServletRequest httpServletRequest) {

        ResponseTransaction transaction = new ResponseTransaction();

        AppContext context = Utilities.fromJson(request, AppContext.class);

        Response response = null;

        if (!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    Collections.emptyList(),
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            if (context != null) {

                List<PodSummary> summary = Installation.getPodStatusAsJson(context.getStageName() + context.getAppName(), null);

                response = new Response(Constant.SUCCESS,
                        summary,
                        Constant.OBJECT_RETRIEVE_SUCCESSFULLY,
                        transaction);

            } else {

                response = new Response(Constant.ERROR,
                        Collections.emptyList(),
                        Constant.ERROR,
                        transaction);

            }

        }

        return ResponseEntity.ok(response);

    }

    @PostMapping("/performInstanceAction") // run | stop

    public ResponseEntity<Response> performInstanceAction(@RequestBody JsonNode request,
                                                          HttpServletRequest httpServletRequest) {

        ResponseTransaction transaction = new ResponseTransaction();

        AppContext context = Utilities.fromJson(request.get("appContext"), AppContext.class);

        String action = request.get("action").asText();

        String instanceName = request.get("instanceName").asText();

        Response response = null;

        if (!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    null,
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            if (context != null) {

                boolean status = false;

                if (action.equalsIgnoreCase("start")) {

                    status = Installation.scaleDeployment(context.getStageName() + context.getAppName(), instanceName, 1);

                } else if (action.equalsIgnoreCase("stop")) {

                    status = Installation.scaleDeployment(context.getStageName() + context.getAppName(), instanceName, 0);

                }

                response = new Response(Constant.SUCCESS,
                        status ? context : null,
                        Constant.OBJECT_RETRIEVE_SUCCESSFULLY,
                        transaction);

            } else {

                response = new Response(Constant.ERROR,
                        null,
                        Constant.ERROR,
                        transaction);

            }

        }

        return ResponseEntity.ok(response);

    }

    @PostMapping("/performStageAction") // run | stop | delete

    public ResponseEntity<Response> performStageAction(@RequestBody JsonNode request,
                                                       HttpServletRequest httpServletRequest) {

        ResponseTransaction transaction = new ResponseTransaction();

        System.out.println("request >>> " + request);

        AppContext context = Utilities.fromJson(request.get("appContext"), AppContext.class);

        String action = request.get("action").asText();

        Response response = null;

        if (!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    null,
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            if (context != null) {

                boolean status = false;

                if (action.equalsIgnoreCase("run")) {

                    status = Installation.scaleDeployment(context.getStageName() + context.getAppName(),
                            null,
                            1);

                    response = new Response(Constant.SUCCESS,
                            context,
                            Constant.OBJECT_RETRIEVE_SUCCESSFULLY,
                            transaction);

                } else if (action.equalsIgnoreCase("stop")) {

                    status = Installation.scaleDeployment(context.getStageName() + context.getAppName(),
                            null,
                            0);

                    response = new Response(Constant.SUCCESS,
                            context,
                            Constant.OBJECT_RETRIEVE_SUCCESSFULLY,
                            transaction);

                } else if (action.equalsIgnoreCase("delete")) {

                    context = Installation.uninstallApplication(context);

                    response = new Response(Constant.SUCCESS,
                            context,
                            Constant.OBJECT_RETRIEVE_SUCCESSFULLY,
                            transaction);

                }

            } else {

                response = new Response(Constant.ERROR,
                        null,
                        Constant.ERROR,
                        transaction);

            }

        }

        return ResponseEntity.ok(response);

    }

}
