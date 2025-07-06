package com.nocodebi.service.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PortForwardWatchdog {

    private static volatile boolean keepRunning = true;
    private static Process portForwardProcess;

    public static void startContinuousPortForward(String namespace,
                                                  String serviceName,
                                                  int localPort,
                                                  int targetPort) {

        killExistingPortForward();

        Thread thread = new Thread(() -> {
            while (keepRunning) {
                try {
                    ProcessBuilder pb = new ProcessBuilder(
                            "kubectl", "port-forward",
                            "svc/" + serviceName,
                            localPort + ":" + targetPort,
                            "-n", namespace
                    );

                    pb.redirectErrorStream(true);
                    portForwardProcess = pb.start();

                    System.out.printf("✅ Port-forward started: localhost:%d → svc/%s:%d%n", localPort, serviceName, targetPort);

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(portForwardProcess.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null && keepRunning) {
                            System.out.println("[kubectl] " + line);
                        }
                    }

                    // Wait for it to exit
                    portForwardProcess.waitFor();

                    if (keepRunning) {
                        System.out.println("🔁 Port-forward process ended. Restarting...");
                        Thread.sleep(3000); // wait before retrying
                    }

                } catch (Exception e) {
                    System.err.println("❌ Exception in port-forward loop: " + e.getMessage());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    public static void stopPortForward() {
        keepRunning = false;
        if (portForwardProcess != null && portForwardProcess.isAlive()) {
            portForwardProcess.destroy();
            System.out.println("🛑 Port-forward process stopped.");
        }
    }

    private static void killExistingPortForward() {
        try {
            // This only works on systems with `taskkill` (Windows) or `pkill` (Linux/Mac)
            // Adjust based on your OS
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                pb = new ProcessBuilder("taskkill", "/F", "/IM", "kubectl.exe");
            } else {
                pb = new ProcessBuilder("pkill", "-f", "kubectl port-forward");
            }
            pb.start().waitFor();
            System.out.println("✅ Killed existing kubectl port-forward processes.");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to kill existing port-forward processes: " + e.getMessage());
        }
    }

}
