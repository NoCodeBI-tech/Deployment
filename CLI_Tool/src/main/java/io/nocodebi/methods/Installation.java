package io.nocodebi.methods;

import com.fasterxml.jackson.databind.JsonNode;
import io.nocodebi.constant.Constant;
import io.nocodebi.utils.Utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Installation {

    public static String buildProductConsole() {

        String output = null;

        try {

            Map<String, String> certificate = Utilities.setup_Wild_Card_Certificate();

            List<String> command = new ArrayList<>();

            command.add(Constant.HELM);

            command.add(Constant.UPGRADE);

            command.add(Constant._INSTALL);

            command.add(Constant.PRODUCT_CONSOLE_NAME);

            command.add(Constant.PRODUCT_CONSOLE_URL);

            command.add(Constant._NAMESPACE + Constant.PRODUCT_CONSOLE_NAME);

            command.add(Constant._CREATE_NAMESPACE);

            command.add(Constant._SET);

            command.add(Constant.GLOBAL_APPNAME +
                    Constant.PRODUCT_CONSOLE_NAME +
                    Constant.COMMA +
                    Constant.GLOBAL_TLS_CRT +
                    certificate.get("crt").toString() +
                    Constant.COMMA +
                    Constant.GLOBAL_TLS_KEY +
                    certificate.get("key").toString());

            System.out.println("buildProductConsole >>> Command : " + String.join(" ", command));

            output = runProcess(command).toString();

            System.out.println("buildProductConsole >>> " + output);

            return output;

        }catch (Exception e){

            e.printStackTrace();

            return output;
        }

    }

    public static String buildTraefik() {

        StringBuilder output = null;

        try {

            List<String> command = new ArrayList<>();

            output = new StringBuilder();

            command.add(Constant.KUBECTL);

            command.add(Constant.APPLY);

            command.add(Constant._FILE);

            command.add(Constant.TRAEFIK_CRD_CMD);

            output.append(runProcess(command).get(Constant.RESULT).toString());

            command = new ArrayList<>();

            command.add(Constant.KUBECTL);

            command.add(Constant.APPLY);

            command.add(Constant._FILE);

            command.add(Constant.TRAEFIK_URL);

            System.out.println("buildTraefik >>> Command : " + String.join(" ", command));

            output.append(runProcess(command).get(Constant.RESULT).toString());

            System.out.println("buildTraefik >>> " + output);

            return output.toString();

        }catch (Exception e){

            e.printStackTrace();

            return output == null ? null : output.toString();
        }

    }

    public static String uninstallProductConsole() {

        List<String> command = new ArrayList<>();

        command.add(Constant.HELM);

        command.add(Constant.UNINSTALL);

        command.add(Constant.PRODUCT_CONSOLE_NAME);

        command.add(Constant._NAMESPACE + Constant.PRODUCT_CONSOLE_NAME);

        System.out.println("Command : " + String.join(" ", command));

        return runProcess(command).toString();

    }

    public static String uninstallTraefik() {

        List<String> command = new ArrayList<>();

        StringBuilder output = new StringBuilder();

        command.add(Constant.KUBECTL);

        command.add(Constant.APPLY);

        command.add(Constant._FILE);

        command.add(Constant.TRAEFIK_CRD_UNINSTALL_CMD);

        output.append(runProcess(command).get(Constant.RESULT).toString());

        command = new ArrayList<>();

        command.add(Constant.KUBECTL);

        command.add(Constant.DELETE);

        command.add(Constant._FILE);

        command.add(Constant.TRAEFIK_URL);

        System.out.println("Command : " + String.join(" ", command));

        output.append(runProcess(command).get(Constant.RESULT).toString());

        output.append(runProcess(List.of(Constant.TRAEFIK_CRD_UNINSTALL_CMD)).get(Constant.RESULT).toString());

        System.out.println("uninstallTraefik >>> " + output);

        return output.toString();

    }

    public String getBuildStatus() {

        try {

            Map<String, Map<String, String>> result = new HashMap<>();

            List<String> command = new ArrayList<>();

            command.add(Constant.KUBECTL);

            command.add(Constant.GET);

            command.add(Constant.ALL);

            command.add(Constant._NAMESPACE + Constant.PRODUCT_CONSOLE_NAME);

            System.out.println("Command : " + String.join(" ", command));

            return runProcess(command).toString();

        } catch (Exception e) {

           e.printStackTrace();

           return "Error";

        }

    }

//    @PostMapping("/data/applicationSetting/resourceUsage")
//
//    public ResponseEntity<Response> resourceUsage(@RequestBody JsonNode request,
//
//                                                  @AuthenticationPrincipal User tokenUser, HttpServletRequest httpServletRequest) {
//
//        try {
//
//            Map<String, Map<String, String>> result = new HashMap<>();
//
//            String appName = Utilities.getValue(request, "appName");
//
//            List<String> command = new ArrayList<>();
//
//            command.add(Constant.KUBECTL);
//
//            command.add(Constant.TOP);
//
//            command.add(Constant.PODS);
//
//            command.add(Constant._NAMESPACE + appName);
//
//            System.out.println("Command : " + String.join(" ", command));
//
//            Map<String, String> res = Utilities.runProcess(command);
//
//            result.put(appName, res);
//
//            responseObj = new Response(CoreConstant.SUCCESS, result, CoreConstant.OBJECT_RETRIEVE_SUCCESSFULLY,
//
//                    new ResponseTransaction());
//
//            System.out.println("resourceUsage : " + Utilities.toJson(responseObj));
//
//            return ResponseEntity.ok(responseObj);
//
//        } catch (Exception e) {
//
//            responseObj = new Response(CoreConstant.ERROR, CoreConstant.ERROR,
//
//                    CoreConstant.OBJECT_RETRIEVE_SUCCESSFULLY, new ResponseTransaction());
//
//            System.out.println("resourceUsage : " + Utilities.toJson(responseObj));
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//
//                    .body(responseObj);
//
//        }
//
//    }

    public static Map<String,String> runProcess(List<String> command) {

        HashMap<String,String> response = new HashMap<>();

        try {

            ProcessBuilder pb = new ProcessBuilder(command);

            pb.redirectErrorStream(true);

            Process process = pb.start();

            String result = new BufferedReader(new InputStreamReader(process.getInputStream()))

                    .lines().collect(Collectors.joining("\n"));

            String exitCode = String.valueOf(process.waitFor());

            response.put(Constant.RESULT, result);

            response.put(Constant.EXITCODE, exitCode);

        } catch (Exception e) {

            response.put(Constant.RESULT, "Error: " + e.getMessage());

            response.put(Constant.EXITCODE, "-1");

        }

        return response;

    }

}
