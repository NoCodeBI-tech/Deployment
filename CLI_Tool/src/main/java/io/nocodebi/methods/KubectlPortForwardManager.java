package io.nocodebi.methods;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class KubectlPortForwardManager {

    private volatile Process process;
    private volatile boolean shouldRun = false;
    private Thread monitorThread;

    public synchronized void start() {
        if (shouldRun) {
            System.out.println("Port-forward is already running or starting.");
            return;
        }

        shouldRun = true;

        monitorThread = new Thread(() -> {
            while (shouldRun) {
                try {
                    System.out.println("Starting kubectl port-forward...");
                    ProcessBuilder builder = new ProcessBuilder("kubectl", "port-forward", "svc/traefik", "443:443");
                    builder.redirectErrorStream(true);
                    process = builder.start();

                    // Read and print output
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("[kubectl] " + line);
                        }
                    }

                    // Wait for process to exit
                    int exitCode = process.waitFor();
                    System.out.println("kubectl exited with code: " + exitCode);

                    if (shouldRun) {
                        System.out.println("Restarting kubectl port-forward in 3 seconds...");
                        Thread.sleep(3000);
                    }

                } catch (IOException | InterruptedException e) {
                    if (shouldRun) {
                        System.err.println("kubectl failed: " + e.getMessage());
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ie) {
                            break;
                        }
                    }
                }
            }

            System.out.println("Monitor thread exited.");
        });

        monitorThread.setDaemon(false); // Prevents JVM from exiting
        monitorThread.start();
    }

    public synchronized void stop() {
        shouldRun = false;

        if (process != null && process.isAlive()) {
            System.out.println("Stopping kubectl port-forward...");
            process.destroy();
        }

        if (monitorThread != null && monitorThread.isAlive()) {
            monitorThread.interrupt();
        }

        process = null;
        monitorThread = null;
    }

    public synchronized boolean isRunning() {
        return process != null && process.isAlive();
    }
}
