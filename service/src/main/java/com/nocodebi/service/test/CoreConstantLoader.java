//package com.nocodebi.service.test;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//
//@Component
//public class CoreConstantLoader {
//
//    @Value("${STAGE_ID:}")
//    private String stageId;
//
//    @Value("${APP_ID:}")
//    private String appId;
//
//    @Value("${VERSION_ID:}")
//    private String versionId;
//
//    @Value("${USER_ID:}")
//    private String userId;
//
//    @Value("${CENTRAL_SERVER_URL:}")
//    private String centralServerURL;
//
//    @Value("${PRODUCT_CONSOLE_SERVER_URL:}")
//    private String productConsoleServerURL;
//
//    @Value("${APP_SERVER_URL:}")
//    private String appServerURL;
//
//    @Value("${PREMISES_SHA:}")
//    private String premisesSHA;
//
//    @Value("${CORE_JAR_URL:}")
//    private String coreJarURL;
//
//    @Value("${M2_ZIP_URL:}")
//    private String m2ZipURL;
//
//    @Value("${APP_DATA_PATH:/app/data/}")
//    private String appDataPath;
//
//    @PostConstruct
//    public void init() {
//
//        System.out.println("Core Constant Loader is Initialize...");
//
//        if (centralServerURL != null && !centralServerURL.isEmpty())
//
//            CoreConstant.CENTRAL_SERVER_URL = centralServerURL;
//
//        if (productConsoleServerURL != null && !productConsoleServerURL.isEmpty()) {
//
//            CoreConstant.PRODUCT_CONSOLE_SERVER_URL = productConsoleServerURL;
//
//            CoreConstant.PRODUCT_CONSOLE_SERVER_LOGIN_URL = productConsoleServerURL.concat("login");
//
//        }
//
//        if (appServerURL != null && !appServerURL.isEmpty()) {
//
//            CoreConstant.APP_SERVER_URL = appServerURL;
//
//            CoreConstant.CONFIGURATION_SERVER_URL = appServerURL.concat("config");
//
//        }
//
//        if (appDataPath != null && !appDataPath.isEmpty()) {
//
//            StringBuilder pathBuilder = new StringBuilder(appDataPath);
//
//            if (!appDataPath.endsWith("/")) {
//
//                pathBuilder.append("/");
//
//            }
//
//            String finalPath = pathBuilder.toString();
//
//            CoreConstant.CORE_JAR_PATH = finalPath.concat(CoreConstant.CORE_JAR_PATH);
//
//            CoreConstant.PLUGIN_PATH = finalPath.concat(CoreConstant.PLUGIN_PATH);
//
//            CoreConstant.M2_ZIP_PATH = finalPath.concat(CoreConstant.M2_ZIP_PATH);
//
//            CoreConstant.M2_REPO_PATH = finalPath.concat(CoreConstant.M2_REPO_PATH);
//
//            CoreConstant.INDEXING_PATH = finalPath.concat(CoreConstant.INDEXING_PATH);
//
//            CoreConstant.DATABASE_CONFIG_PATH = finalPath.concat(CoreConstant.DATABASE_CONFIG_PATH);
//
//            CoreConstant.DATABASE_CENTRAL_PATH = finalPath.concat(CoreConstant.DATABASE_CENTRAL_PATH);
//
//            CoreConstant.DATABASE_HOSTED_PATH = finalPath.concat(CoreConstant.DATABASE_HOSTED_PATH);
//
//            CoreConstant.CONSOLIDATION_PATH = finalPath.concat(CoreConstant.CONSOLIDATION_PATH);
//
//            CoreConstant.FILE_STORAGE_PATH = finalPath.concat(CoreConstant.FILE_STORAGE_PATH);
//
//        }
//
//        if (appId != null && !appId.isEmpty()) {
//
//            CoreConstant.appId = appId;
//
//        }
//
//        if (stageId != null && !stageId.isEmpty()) {
//
//            CoreConstant.stageId = stageId;
//
//        }
//
//        if (versionId != null && !versionId.isEmpty()) {
//
//            CoreConstant.versionId = versionId;
//
//        }
//
//        if (userId != null && !userId.isEmpty()) {
//
//            CoreConstant.userId = userId;
//
//        }
//
//        if (premisesSHA != null && !premisesSHA.isEmpty()) {
//
//            CoreConstant.premisesSHA = premisesSHA;
//
//        }
//
//        // Download coreJarURL -> CORE_JAR_PATH
//        if (coreJarURL != null && !coreJarURL.isEmpty()) {
//            CoreConstant.coreJarURL = coreJarURL;
//            Path coreJarPath = Paths.get(CoreConstant.CORE_JAR_PATH);
//            try {
//                // Ensure parent directory exists
//                Files.createDirectories(coreJarPath.getParent());
//
//                // Download and save file
//                try (InputStream in = new URL(coreJarURL).openStream()) {
//                    Files.copy(in, coreJarPath, StandardCopyOption.REPLACE_EXISTING);
//                    System.out.println("Downloaded coreJarURL to " + CoreConstant.CORE_JAR_PATH);
//                }
//            } catch (IOException e) {
//                System.err.println("Failed to download or save coreJarURL: " + e.getMessage());
//            }
//        }
//
//        // Download m2ZipURL -> M2_ZIP_PATH
//        if (m2ZipURL != null && !m2ZipURL.isEmpty()) {
//            CoreConstant.m2ZipURL = m2ZipURL;
//            Path m2ZipPath = Paths.get(CoreConstant.M2_ZIP_PATH);
//            try {
//                // Ensure parent directory exists
//                Files.createDirectories(m2ZipPath.getParent());
//
//                // Download and save file
//                try (InputStream in = new URL(m2ZipURL).openStream()) {
//                    Files.copy(in, m2ZipPath, StandardCopyOption.REPLACE_EXISTING);
//                    System.out.println("Downloaded m2ZipURL to " + CoreConstant.M2_ZIP_PATH);
//                }
//            } catch (IOException e) {
//                System.err.println("Failed to download or save m2ZipURL: " + e.getMessage());
//            }
//        }
//
//        if (appDataPath != null && !appDataPath.isEmpty()) {
//
//            CoreConstant.appDataPath = appDataPath;
//
//        }
//
//        System.out.println("------------------------ CoreConstant Values Summary ------------------------");
//        System.out.println("CENTRAL_SERVER_URL = " + CoreConstant.CENTRAL_SERVER_URL);
//        System.out.println("PRODUCT_CONSOLE_SERVER_URL = " + CoreConstant.PRODUCT_CONSOLE_SERVER_URL);
//        System.out.println("APP_SERVER_URL = " + CoreConstant.APP_SERVER_URL);
//        System.out.println("CONFIGURATION_SERVER_URL = " + CoreConstant.CONFIGURATION_SERVER_URL);
//
//        System.out.println("CoreConstant.CORE_JAR_PATH = " + CoreConstant.CORE_JAR_PATH);
//        System.out.println("CoreConstant.PLUGIN_PATH = " + CoreConstant.PLUGIN_PATH);
//        System.out.println("CoreConstant.M2_ZIP_PATH = " + CoreConstant.M2_ZIP_PATH);
//        System.out.println("CoreConstant.M2_REPO_PATH = " + CoreConstant.M2_REPO_PATH);
//        System.out.println("CoreConstant.INDEXING_PATH = " + CoreConstant.INDEXING_PATH);
//        System.out.println("CoreConstant.DATABASE_CONFIG_PATH = " + CoreConstant.DATABASE_CONFIG_PATH);
//        System.out.println("CoreConstant.DATABASE_CENTRAL_PATH = " + CoreConstant.DATABASE_CENTRAL_PATH);
//        System.out.println("CoreConstant.DATABASE_HOSTED_PATH = " + CoreConstant.DATABASE_HOSTED_PATH);
//        System.out.println("CoreConstant.CONSOLIDATION_PATH = " + CoreConstant.CONSOLIDATION_PATH);
//        System.out.println("CoreConstant.FILE_STORAGE_PATH = " + CoreConstant.FILE_STORAGE_PATH);
//
//        System.out.println("CoreConstant.appId = " + CoreConstant.appId);
//        System.out.println("CoreConstant.stageId = " + CoreConstant.stageId);
//        System.out.println("CoreConstant.versionId = " + CoreConstant.versionId);
//        System.out.println("CoreConstant.userId = " + CoreConstant.userId);
//        System.out.println("CoreConstant.premisesSHA = " + CoreConstant.premisesSHA);
//        System.out.println("CoreConstant.coreJarURL = " + CoreConstant.coreJarURL);
//        System.out.println("CoreConstant.m2ZipURL = " + CoreConstant.m2ZipURL);
//        System.out.println("CoreConstant.appDataPath = " + CoreConstant.appDataPath);
//        System.out.println("-----------------------------------------------------------------------------");
//
//    }
//
//}
