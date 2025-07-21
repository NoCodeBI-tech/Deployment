package com.nocodebi.service;

import com.nocodebi.service.cookieManager.CookieStoreManager;
import com.nocodebi.service.service.PortForwardWatchdog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceApplication {

    public static CookieStoreManager manager = new CookieStoreManager();

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
        try {

            start();

        } catch (Exception ignore) {

        }
    }

    public static void start() throws InterruptedException {

        PortForwardWatchdog.startContinuousPortForward(
                "default",
                "traefik",
                443,
                443);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            PortForwardWatchdog.stopPortForward();
            try {
                Thread.sleep(3000); // wait for OS to release the port
            } catch (InterruptedException ignored) {
            }
        }));


        // Keep main thread alive
        Thread.currentThread().join();

    }

}