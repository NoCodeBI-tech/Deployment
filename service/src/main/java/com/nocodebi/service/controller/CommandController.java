package com.nocodebi.service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.nocodebi.service.constant.Constant;
import com.nocodebi.service.methods.Installation;
import com.nocodebi.service.model.Response;
import com.nocodebi.service.model.ResponseTransaction;
import com.nocodebi.service.utils.Utilities;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommandController {

    @PostMapping("/installProduct")

    public ResponseEntity<Response> installProductConsole(@RequestBody JsonNode request,
                                                          HttpServletRequest httpServletRequest) throws Exception {

        ResponseTransaction transaction = new ResponseTransaction();

        Response response = null;

        StringBuilder result = new StringBuilder();

        if(!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    Constant.UNAUTHORISED,
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            result.append(Installation.buildTraefik());

            result.append(Installation.buildProductConsole());

            response = new Response(Constant.SUCCESS,
                    result,
                    null,
                    transaction);

        }

        return ResponseEntity.ok(response);

    }

    @PostMapping("/uninstallProduct")

    public ResponseEntity<Response> uninstallProductConsole(@RequestBody JsonNode request,
                                                            HttpServletRequest httpServletRequest) throws Exception {


        ResponseTransaction transaction = new ResponseTransaction();

        Response response = null;

        StringBuilder result = new StringBuilder();

        if(!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    Constant.UNAUTHORISED,
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            result.append(Installation.uninstallTraefik());

            result.append(Installation.uninstallProductConsole());

            response = new Response(Constant.SUCCESS,
                    result,
                    null,
                    transaction);

        }

        return ResponseEntity.ok(response);

    }

    @PostMapping("/installApp")

    public ResponseEntity<Response> installApp(@RequestBody JsonNode request,
                                                          HttpServletRequest httpServletRequest) throws Exception {

        ResponseTransaction transaction = new ResponseTransaction();

        Response response = null;

        StringBuilder result = new StringBuilder();

        if(!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    Constant.UNAUTHORISED,
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            result.append(Installation.buildTraefik());

            result.append(Installation.buildProductConsole());

            response = new Response(Constant.SUCCESS,
                    result,
                    null,
                    transaction);

        }

        return ResponseEntity.ok(response);

    }

    @PostMapping("/uninstallApp")

    public ResponseEntity<Response> uninstallApp(@RequestBody JsonNode request,
                                                            HttpServletRequest httpServletRequest) throws Exception {


        ResponseTransaction transaction = new ResponseTransaction();

        Response response = null;

        StringBuilder result = new StringBuilder();

        if(!Utilities.validateRequest(httpServletRequest)) {

            response = new Response(
                    Constant.UNAUTHORISED,
                    Constant.UNAUTHORISED,
                    Constant.UNAUTHORISED,
                    transaction);

        } else {

            result.append(Installation.uninstallTraefik());

            result.append(Installation.uninstallProductConsole());

            response = new Response(Constant.SUCCESS,
                    result,
                    null,
                    transaction);

        }

        return ResponseEntity.ok(response);

    }

}
