package com.nocodebi.service.methods;

import com.nocodebi.service.constant.Constant;
import com.nocodebi.service.cookieManager.CookieStoreManager;
import com.nocodebi.service.utils.Utilities;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Auth {

    public static boolean login(CookieStoreManager cookieStoreManager) throws Exception {

        String hostname = "";
        String os = System.getProperty("os.name");;
        String browser = "chrome";

        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        scanner.close();

        // Get hostname
        try {

            InetAddress inetAddress = InetAddress.getLocalHost();

            hostname = inetAddress.getHostName();

        } catch (UnknownHostException e) {

            System.out.println("Hostname could not be resolved");

            e.printStackTrace();

            return false;

        }

        try {

            String body = String.format("{ \"username\":\"%s\", \"password\":\"%s\", \"hostname\":\"%s\", \"os\":\"%s\" , \"browser\":\"%s\" }", username, password, hostname, os, browser);

            HttpResponse<String> response = Utilities.apiCall(cookieStoreManager, Constant.PRODUCT_CONSOLE, Constant.LOGIN, body);

            String cookiesString = Utilities.toJson(response.headers().allValues("set-cookie"));

            if (cookiesString.contains(Constant.ACCESSTOKEN) && cookiesString.contains(Constant.REFRESHTOKEN)) {

                cookieStoreManager.saveCookies();

                System.out.println("Login Success...");

                return true;

            } else {

                cookieStoreManager.clearCookies(true);

                System.out.println("Invalid Credentials");

                return false;

            }

        }catch (Exception e){

            e.printStackTrace();

            return false;

        }

    }

    public static void logout(CookieStoreManager cookieStoreManager) throws Exception {

        cookieStoreManager.clearCookies(true);

        System.out.println("Logout Success...");

    }

}
