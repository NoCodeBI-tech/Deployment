package com.nocodebi.service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.nocodebi.service.constant.Constant;
import com.nocodebi.service.methods.Installation;
import com.nocodebi.service.model.*;
import com.nocodebi.service.utils.Utilities;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommandController {

    @PostMapping("/installProductConsole")

    public ResponseEntity<Response> installProductConsole(@RequestBody JsonNode request,
                                                          HttpServletRequest httpServletRequest) throws Exception {

        ResponseTransaction transaction = new ResponseTransaction();

        Response response = null;

        boolean status = false;

        StringBuilder result = new StringBuilder();

        if (!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    status,
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            result.append(Installation.buildTraefik());

            result.append(Installation.buildProductConsole());

            status = result.indexOf("STATUS: deployed") != -1;

            response = new Response(Constant.SUCCESS,
                    status,
                    null,
                    transaction);

        }

        return ResponseEntity.ok(response);

    }

    @PostMapping("/deleteProductConsole")

    public ResponseEntity<Response> deleteProductConsole(@RequestBody JsonNode request,
                                                         HttpServletRequest httpServletRequest) throws Exception {

        ResponseTransaction transaction = new ResponseTransaction();

        Response response = null;

        boolean status = false;

        StringBuilder result = new StringBuilder();

        if (!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    status,
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            result.append(Installation.uninstallTraefik());

            result.append(Installation.uninstallProductConsole());

            status = result.indexOf(String.format("release \"%s\" uninstalled", Constant.PRODUCT_CONSOLE_NAME)) != -1;

            response = new Response(Constant.SUCCESS,
                    status,
                    null,
                    transaction);

        }

        return ResponseEntity.ok(response);

    }

    @PostMapping("/buildApplication")

    public ResponseEntity<Response> buildApplication(@RequestBody JsonNode request,
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

            context = Installation.buildApplication(context);

            if (context != null) {

                response = new Response(Constant.SUCCESS,
                        context,
                        Constant.OBJECT_CREATED_SUCCESSFULLY,
                        transaction);

                Installation.waitForPodsRunning(context.getAppName(), null, 120);

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

        AppContext context = Utilities.fromJson(request, AppContext.class);

        Response response = null;

        if (!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    null,
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            if (context != null) {

                List<ContainerUsage> usages = Installation.getPodMetrics(context.getAppName(), null);

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

                List<PodSummary> summary = Installation.getPodStatusAsJson(context.getAppName(), null);

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

                    status = Installation.scaleDeployment(context.getAppName(), instanceName, 1);

                } else if (action.equalsIgnoreCase("stop")) {

                    status = Installation.scaleDeployment(context.getAppName(), instanceName, 0);

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

                if (action.equalsIgnoreCase("start")) {

                    status = Installation.scaleDeployment(context.getAppName(),
                            null,
                            1);

                    response = new Response(Constant.SUCCESS,
                            context,
                            Constant.OBJECT_RETRIEVE_SUCCESSFULLY,
                            transaction);

                } else if (action.equalsIgnoreCase("stop")) {

                    status = Installation.scaleDeployment(context.getAppName(),
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
