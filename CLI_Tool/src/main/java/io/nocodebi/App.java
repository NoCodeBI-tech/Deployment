package io.nocodebi;

import io.nocodebi.constant.Constant;
import io.nocodebi.methods.Methods;
import io.nocodebi.cookieManager.CookieStoreManager;
import io.nocodebi.utils.JwtUtil;
import io.nocodebi.utils.Utilities;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Map;

public class App
{

    public static void main(String[] args) throws Exception {

        try {

            if (args.length == 0) {
                System.out.println("Usage:  nocodebi <command>");
                System.out.println();
                System.out.println("These are common NoCodeBI commands used in various situations:");
                System.out.println("  login             Authenticate to a NoCodeBI");
                System.out.println("  logout            Log out from a NoCodeBI");
                System.out.println("  install           Install Product Console");
                return;
            }

            String cmd = Arrays.toString(args);

            System.out.println(cmd);

            String command = args[0];

            CookieStoreManager cookieStoreManager = new CookieStoreManager();

            if ("login".equalsIgnoreCase(command)) {

                Methods.login(cookieStoreManager);

                return;

            }

            JwtUtil jwtUtil = new JwtUtil();

            Map<String, String> token = cookieStoreManager.loadCookies();

            if(token == null){

                cookieStoreManager.clearCookies(true);

                System.out.println("Please Login First...");

                return;

            } else {

                if(!jwtUtil.validateToken(token.get(Constant.ACCESSTOKEN))){

                    cookieStoreManager.clearCookies(true);

                    System.out.println("Session Expired...");

                    return;

                }

            }

            if ("logout".equalsIgnoreCase(command)) {

                Methods.logout(cookieStoreManager);

            } else if ("install".equalsIgnoreCase(command)) {



            } else if ("test".equalsIgnoreCase(command)) {

                HttpResponse<String> response = Utilities.apiCall(cookieStoreManager, Constant.PRODUCT_CONSOLE, Constant.test,"{}");

                System.out.println("Test >>> " + Utilities.toJson(response.body()));

            } else {

                System.out.println("Unknown command: " + command);

            }

        } catch (Exception e){

            e.printStackTrace();

        }

    }

}
