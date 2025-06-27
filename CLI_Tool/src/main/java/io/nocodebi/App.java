package io.nocodebi;

import io.nocodebi.constant.Constant;
import io.nocodebi.methods.Auth;
import io.nocodebi.cookieManager.CookieStoreManager;
import io.nocodebi.methods.Installation;
import io.nocodebi.service.DeviceFingerprintService;
import io.nocodebi.utils.JwtUtil;
import io.nocodebi.utils.Utilities;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class App
{

    public static void main(String[] args) throws Exception {

        String cmd = null;

        String command = null;

        try {

            // TODO : Testing Code will be delete

            // Testing Start

            while(true) {

                Scanner sc = new Scanner(System.in);

                System.out.print("Enter CMD to Execute : ");

                String line = sc.nextLine();

                args = line.split("\\s+");

                // Testing End

                cmd = Arrays.toString(args);

                if (args.length == 0) {
                    System.out.println("Usage:  nocodebi <command>");
                    System.out.println();
                    System.out.println("These are common NoCodeBI commands used in various situations:");
                    System.out.println("  login             Authenticate to a NoCodeBI");
                    System.out.println("  logout            Log out from a NoCodeBI");
                    System.out.println("  install           Install Product Console");
                    return;
                } else if (args.length == 1) {

                    System.out.println(cmd);

                    command = args[0];

                    CookieStoreManager cookieStoreManager = new CookieStoreManager();

                    if ("login".equalsIgnoreCase(command)) {

                        Auth.login(cookieStoreManager);

                        return;

                    }

                    JwtUtil jwtUtil = new JwtUtil();

                    Map<String, String> token = cookieStoreManager.loadCookies();

                    if (token == null) {

                        cookieStoreManager.clearCookies(true);

                        System.out.println("Please Login First...");

                        return;

                    } else {

                        if (!jwtUtil.validateToken(token.get(Constant.ACCESSTOKEN))) {

                            cookieStoreManager.clearCookies(true);

                            System.out.println("Session Expired...");

                            return;

                        }

                    }

                    if ("logout".equalsIgnoreCase(command)) {

                        Auth.logout(cookieStoreManager);

                    } else if ("install".equalsIgnoreCase(command)) {

                        Installation.buildTraefik();

                        Installation.buildProductConsole();

                    } else if ("uninstall".equalsIgnoreCase(command)) {

                        System.out.println(Installation.uninstallTraefik());

                        System.out.println(Installation.uninstallProductConsole());

                    } else if ("test".equalsIgnoreCase(command)) {

                        System.out.println("SHA Key >>> " + DeviceFingerprintService.generateDeviceFingerprint());

                        HttpResponse<String> response = Utilities.apiCall(cookieStoreManager, Constant.PRODUCT_CONSOLE, Constant.test, "{}");

                        System.out.println("Test >>> " + Utilities.toJson(response.body()));

                    } else if ("exit".equalsIgnoreCase(command)) {

                        System.out.println("Thank you for choosing NoCodeBI...");

                        break;

                    } else {

                        System.out.println("Unknown command: " + command);

                    }

                } else {

                    System.out.println("Unknown command: " + cmd);

                }

            }

        } catch (Exception e){

            e.printStackTrace();

        }

    }

}
