package com.nocodebi.service.methods;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocodebi.service.constant.Constant;
import com.nocodebi.service.model.*;
import com.nocodebi.service.service.DeviceFingerprintService;
import com.nocodebi.service.utils.Utilities;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.ContainerMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetricsList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Installation {

//    public static String buildTraefik() {
//
//        StringBuilder output = null;
//
//        try {
//
//            List<String> command = new ArrayList<>();
//
//            output = new StringBuilder();
//
//            command.add(Constant.KUBECTL);
//
//            command.add(Constant.APPLY);
//
//            command.add(Constant._FILE);
//
//            command.add(Constant.TRAEFIK_CRD_CMD);
//
//            output.append(runProcess(command).get(Constant.RESULT));
//
//            output.append("\n");
//
//            command = new ArrayList<>();
//
//            command.add(Constant.KUBECTL);
//
//            command.add(Constant.APPLY);
//
//            command.add(Constant._FILE);
//
//            command.add(Constant.TRAEFIK_URL);
//
////            System.out.println("buildTraefik >>> Command : " + String.join(" ", command));
//
//            output.append(runProcess(command).get(Constant.RESULT));
//
////            System.out.println("buildTraefik >>> " + output);
//
//            return output.toString();
//
//        }catch (Exception e){
//
//            e.printStackTrace();
//
//            return output == null ? null : output.toString();
//        }
//
//    }

    public static boolean buildTraefik() {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {

            // Load CRD from URL
            try (InputStream crdStream = new URL(Constant.TRAEFIK_CRD_CMD).openStream()) {
                client.load(crdStream).create();
            }

            // Load Traefik from URL
            try (InputStream urlStream = new URL(Constant.TRAEFIK_URL).openStream()) {
                client.load(urlStream).create();
            }

            System.out.println("Traefik applied successfully.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error applying Traefik: " + e.getMessage());
            return false;
        }
    }

    public static boolean uninstallTraefik() {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {

            try (InputStream crdStream = new URL(Constant.TRAEFIK_CRD_CMD).openStream()) {
                client.load(crdStream).delete();
            }

            try (InputStream urlStream = new URL(Constant.TRAEFIK_URL).openStream()) {
                client.load(urlStream).delete();
            }

            System.out.println("Traefik deleted successfully.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error deleting Traefik: " + e.getMessage());
            return false;
        }
    }


//    public static String uninstallTraefik() {
//
//        List<String> command = new ArrayList<>();
//
//        StringBuilder output = new StringBuilder();
//
//        command.add(Constant.KUBECTL);
//
//        command.add(Constant.DELETE);
//
//        command.add(Constant._FILE);
//
//        command.add(Constant.TRAEFIK_CRD_CMD);
//
//        output.append(runProcess(command).get(Constant.RESULT).toString());
//
//        output.append("\n");
//
//        command = new ArrayList<>();
//
//        command.add(Constant.KUBECTL);
//
//        command.add(Constant.DELETE);
//
//        command.add(Constant._FILE);
//
//        command.add(Constant.TRAEFIK_URL);
//
//        System.out.println("Command : " + String.join(" ", command));
//
//        output.append(runProcess(command).get(Constant.RESULT).toString());
//
//        System.out.println("uninstall Traefik >>> " + output);
//
//        return output.toString();
//
//    }

//    public static String buildProductConsole() {
//
//        String output = null;
//
//        try {
//
//            Map<String, String> certificate = Utilities.setup_Wild_Card_Certificate();
//
//            List<String> command = new ArrayList<>();
//
//            command.add(Constant.HELM);
//
//            command.add(Constant.UPGRADE);
//
//            command.add(Constant._INSTALL);
//
//            command.add(Constant.PRODUCT_CONSOLE_NAME);
//
//            command.add(Constant.PRODUCT_CONSOLE_URL);
//
//            command.add(Constant._NAMESPACE + Constant.PRODUCT_CONSOLE_NAME);
//
//            command.add(Constant._CREATE_NAMESPACE);
//
//            command.add(Constant._SET);
//
//            command.add(Constant.GLOBAL_APPNAME +
//                    Constant.PRODUCT_CONSOLE_NAME +
//                    Constant.COMMA +
//                    Constant.GLOBAL_TLS_CRT +
//                    certificate.get("crt").toString() +
//                    Constant.COMMA +
//                    Constant.GLOBAL_TLS_KEY +
//                    certificate.get("key").toString());
//
////            System.out.println("buildProductConsole >>> Command : " + String.join(" ", command));
//
//            output = runProcess(command).toString();
//
////            System.out.println("buildProductConsole >>> " + output);
//
//            return output;
//
//        }catch (Exception e){
//
//            e.printStackTrace();
//
//            return output;
//        }
//
//    }
//
//    public static String uninstallProductConsole() {
//
//        List<String> command = new ArrayList<>();
//
//        command.add(Constant.HELM);
//
//        command.add(Constant.UNINSTALL);
//
//        command.add(Constant.PRODUCT_CONSOLE_NAME);
//
//        command.add(Constant._NAMESPACE + Constant.PRODUCT_CONSOLE_NAME);
//
//        System.out.println("Command : " + String.join(" ", command));
//
//        return runProcess(command).toString();
//
//    }

    public static CommandResult buildProductConsole() {
        try {

            Map<String, String> certificate = Utilities.setup_Wild_Card_Certificate();

            if (!certificate.containsKey(Constant.CRT) || !certificate.containsKey(Constant.KEY)) {
                System.out.println("Error: Missing wildcard certificate data");
                return null;
            }

            List<String> command = new ArrayList<>(List.of(
                    Constant.HELM,
                    Constant.UPGRADE,
                    Constant._INSTALL,
                    Constant.PRODUCT_CONSOLE_NAME,
                    Constant.PRODUCT_CONSOLE_URL,
                    Constant._NAMESPACE + Constant.PRODUCT_CONSOLE_NAME,
                    Constant._CREATE_NAMESPACE,
                    Constant._SET,
                    Constant.GLOBAL_APPNAME + Constant.PRODUCT_CONSOLE_NAME +
                            Constant.COMMA + Constant.GLOBAL_TLS_CRT + certificate.get(Constant.CRT) +
                            Constant.COMMA + Constant.GLOBAL_TLS_KEY + certificate.get(Constant.KEY) +
                            Constant.COMMA + Constant.GLOBAL_USER_HOME + Utilities.getLinuxStyleDataPath() +
                            Constant.COMMA + Constant.GLOBAL_ENV_APP_CHART_URL + Constant.APP_URL
            ));

            // Debug: Uncomment if needed
            // System.out.println("buildProductConsole >>> Command: " + String.join(" ", command));

            CommandResult result = runProcess(command);

            if (result.isSuccess()) {
                System.out.println("Output:\n" + result.getStdout());
            } else {
                System.err.println("Error:\n" + result.getStderr());
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error building product console: " + e.getMessage());
            return null;
        }
    }

    public static CommandResult uninstallProductConsole() {
        try {
            List<String> command = List.of(
                    Constant.HELM,
                    Constant.UNINSTALL,
                    Constant.PRODUCT_CONSOLE_NAME,
                    Constant._NAMESPACE + Constant.PRODUCT_CONSOLE_NAME
            );

            // Debug: Uncomment if needed
            // System.out.println("uninstallProductConsole >>> Command: " + String.join(" ", command));

            CommandResult result = runProcess(command);

            if (result.isSuccess()) {
                System.out.println("Output:\n" + result.getStdout());
            } else {
                System.err.println("Error:\n" + result.getStderr());
            }

            return result;

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println("Error uninstalling product console: " + e.getMessage());

            return null;

        }
    }

    public static AppContext buildApplication(AppContext context) {
        try {

            String premisesSHA = DeviceFingerprintService.generateDeviceFingerprint();
            @SuppressWarnings("unchecked")
            Map<String, String> certificate = (Map<String, String>) Utilities.readDataFromWindows(Constant.CERTIFICATE_PATH);

            if (certificate == null || !certificate.containsKey(Constant.CRT) || !certificate.containsKey(Constant.KEY)) {
                System.out.println("Error: Certificate not found or incomplete.");
                return null;
            }

            Utilities.addHostEntryWithAppName(context.getAppName());

            List<String> command = new ArrayList<>(List.of(
                    Constant.HELM,
                    Constant.UPGRADE,
                    Constant._INSTALL,
                    context.getAppName(),
                    context.getChartURL(),
                    Constant._NAMESPACE + context.getAppName(),
                    Constant._CREATE_NAMESPACE,
                    Constant._SET,
                    Constant.GLOBAL_APPNAME + context.getAppName() +
                            Constant.COMMA + Constant.GLOBAL_TLS_CRT + certificate.get(Constant.CRT) +
                            Constant.COMMA + Constant.GLOBAL_TLS_KEY + certificate.get(Constant.KEY) +
                            Constant.COMMA + Constant.GLOBAL_USER_HOME + Utilities.getLinuxStyleDataPath() +
                            Constant.COMMA + Constant.GLOBAL_ENV_STAGE_ID + context.getStageId() +
                            Constant.COMMA + Constant.GLOBAL_ENV_APP_ID + context.getAppId() +
                            Constant.COMMA + Constant.GLOBAL_ENV_VERSION_ID + context.getVersionId() +
                            Constant.COMMA + Constant.GLOBAL_ENV_USER_ID + context.getUserId() +
                            Constant.COMMA + Constant.GLOBAL_ENV_CENTRAL_SERVER_URL + context.getCentralURL() +
                            Constant.COMMA + Constant.GLOBAL_ENV_PRODUCT_CONSOLE_SERVER_URL + context.getProductURL() +
                            Constant.COMMA + Constant.GLOBAL_ENV_APP_SERVER_URL + context.getAppURL() +
                            Constant.COMMA + Constant.GLOBAL_ENV_PREMISES_SHA + premisesSHA +
                            Constant.COMMA + Constant.GLOBAL_ENV_CORE_JAR_URL + context.getCoreJarURL() +
                            Constant.COMMA + Constant.GLOBAL_ENV_M2_ZIP_URL + context.getM2ZipURL() +
                            Constant.COMMA + Constant.GLOBAL_ENV_APP_DATA_PATH + context.getAppDataPath()
            ));

            // Debug: Uncomment if needed
            // System.out.println("buildApplication >>> Command: " + String.join(" ", command));

            CommandResult result = runProcess(command);

            if (result.isSuccess() && result.getStdout().contains("STATUS: deployed")) {

                context.setPremiseSHA(premisesSHA);

                System.out.println("Output:\n" + result.getStdout());

                return context;

            } else {

                System.err.println("Error:\n" + result.getStderr());

                return null;

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error building application [" + context.getAppName() + "]: " + e.getMessage());
            return null;
        }
    }

    public static AppContext uninstallApplication(AppContext context) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            List<String> command = List.of(
                    Constant.HELM,
                    Constant.UNINSTALL,
                    context.getAppName(),
                    Constant._NAMESPACE + context.getAppName()
            );

            // Debug: Uncomment if needed
            // System.out.println("uninstallApplication >>> Command: " + String.join(" ", command));

            CommandResult result = runProcess(command);

            if (result.isSuccess() && result.getStdout().contains(String.format("release \"%s\" uninstalled", context.getAppName()))) {

                context.setPremiseSHA("");

                deleteNamespaceIfEmpty(context.getAppName());

                return context;

            } else {

                System.err.println("Error:\n" + result.getStderr());

                return null;

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error uninstalling application [" + context.getAppName() + "]: " + e.getMessage());
            return null;
        }
    }


    public static void listResources(String namespace) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            client.pods().inNamespace(namespace).list().getItems()
                    .forEach(p -> System.out.println("POD: " + p.getMetadata().getName()));

            client.services().inNamespace(namespace).list().getItems()
                    .forEach(s -> System.out.println("SERVICE: " + s.getMetadata().getName()));

            client.apps().deployments().inNamespace(namespace).list().getItems()
                    .forEach(d -> System.out.println("DEPLOYMENT: " + d.getMetadata().getName()));
        }
    }

    public static List<PodSummary> getPodStatusAsJson(String namespace, String podName) {
        ObjectMapper mapper = new ObjectMapper();
        List<PodSummary> result = new ArrayList<>();

        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            List<Pod> pods;

            if (podName == null || podName.isBlank()) {
                pods = client.pods().inNamespace(namespace).list().getItems();
            } else {
                Pod pod = client.pods().inNamespace(namespace).withName(podName).get();
                if (pod == null) {
                    System.out.println("{\"error\": \"Pod not found: " + podName + "\"}");
                    return Collections.emptyList();
                }
                pods = List.of(pod);
            }

            for (Pod pod : pods) {
                PodSummary summary = new PodSummary();
                summary.name = pod.getMetadata().getName();
                summary.ready = Utilities.formatReady(pod);
                summary.status = pod.getStatus().getPhase();

                int restartCount = pod.getStatus().getContainerStatuses().stream()
                        .mapToInt(cs -> cs.getRestartCount() != null ? cs.getRestartCount() : 0)
                        .sum();

                summary.restarts = String.valueOf(restartCount);

                String startTimeStr = pod.getStatus().getStartTime();

                Instant startTime = (startTimeStr != null)
                        ? OffsetDateTime.parse(startTimeStr).toInstant()
                        : Instant.now();

                summary.age = Utilities.calculatePodAge(startTime);

                result.add(summary);
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("{\"error\": \"" + e.getMessage() + "\"}");
            return Collections.emptyList();
        }
    }

    public static List<ContainerUsage> getPodMetrics(String namespace, String podName) {
        // Early return for invalid namespace
        if (namespace == null || namespace.isBlank()) {
            return Collections.emptyList();
        }

        List<ContainerUsage> usageList = new ArrayList<>();

        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            List<PodMetrics> metrics = getMetricsList(client, namespace, podName);

            if (metrics == null || metrics.isEmpty()) {
                return Collections.emptyList();
            }

            for (PodMetrics pod : metrics) {
                processPodMetrics(pod, usageList);
            }

        } catch (KubernetesClientException kce) {
            // Specific handling for Kubernetes client exceptions
            System.err.printf("Failed to get metrics for namespace %s, pod %s: %s%n",
                    namespace, podName, kce.getMessage());
        } catch (Exception e) {
            // General exception handling
            System.err.printf("Unexpected error while getting metrics: %s%n", e.getMessage());
            e.printStackTrace();
        }

        return usageList;
    }

    private static List<PodMetrics> getMetricsList(KubernetesClient client, String namespace, String podName) {
        if (podName == null || podName.isBlank()) {
            PodMetricsList list = client.top().pods().inNamespace(namespace).metrics();
            return (list == null) ? Collections.emptyList() : list.getItems();
        }

        PodMetrics podMetric = client.top()
                .pods()
                .inNamespace(namespace)
                .withName(podName)
                .metric();
        return (podMetric == null) ? Collections.emptyList() : List.of(podMetric);
    }

    private static void processPodMetrics(PodMetrics pod, List<ContainerUsage> usageList) {
        String podNameVal = pod.getMetadata().getName();

        for (ContainerMetrics container : pod.getContainers()) {
            ContainerUsage usage = new ContainerUsage();
            usage.setPodName(podNameVal);
            usage.setContainerName(container.getName());

            Map<String, Quantity> containerUsage = container.getUsage();
            if (containerUsage != null) {
                usage.setCpu(String.valueOf(containerUsage.get("cpu")));
                usage.setMemory(String.valueOf(containerUsage.get("memory")));
            }

            usageList.add(usage);
        }
    }

    public static CommandResult runProcess(List<String> command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(false);

        try {
            Process process = processBuilder.start();

            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<String> stdoutFuture = executor.submit(() -> readStream(process.getInputStream()));
            Future<String> stderrFuture = executor.submit(() -> readStream(process.getErrorStream()));

            boolean finished = process.waitFor(60, TimeUnit.SECONDS);
            String stdout = stdoutFuture.get();
            String stderr = stderrFuture.get();
            executor.shutdown();

            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("Process timed out: " + String.join(" ", command));
            }

            int exitCode = process.exitValue();

            if (exitCode != 0) {
                System.err.printf("Command failed [%s] with code %d. Error: %s%n",
                        String.join(" ", command), exitCode, stderr);
            }

            return new CommandResult(stdout, stderr, exitCode);

        } catch (Exception e) {
            System.err.println("Command failed: " + String.join(" ", command));
            e.printStackTrace();
            return new CommandResult("", "Exception: " + e.getMessage(), -1);
        }
    }

    private static String readStream(InputStream inputStream) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder output = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {

                output.append(line).append(System.lineSeparator());

            }

            return output.toString().trim();

        } catch (Exception e) {

            e.printStackTrace();

            return e.getLocalizedMessage();

        }
    }

    public static List<PodStatusInfo> waitForPodsRunning(String namespace,
                                                         String podName,
                                                         int timeoutSeconds) {
        List<PodStatusInfo> podStatusList = new ArrayList<>();

        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            long startTime = System.currentTimeMillis();
            long timeoutMillis = timeoutSeconds * 1000L;

            while ((System.currentTimeMillis() - startTime) < timeoutMillis) {
                podStatusList.clear();
                boolean allRunning = true;

                if (podName != null && !podName.isBlank()) {
                    var pod = client.pods().inNamespace(namespace).withName(podName).get();
                    if (pod == null) return List.of(); // or throw exception

                    String phase = pod.getStatus().getPhase();
                    String reason = pod.getStatus().getReason();
                    String message = pod.getStatus().getMessage();

                    podStatusList.add(new PodStatusInfo(podName, phase, reason, message));

                    if (!"Running".equalsIgnoreCase(phase)) {
                        allRunning = false;
                    }

                } else {
                    var pods = client.pods().inNamespace(namespace).list().getItems();
                    for (var pod : pods) {
                        String name = pod.getMetadata().getName();
                        String phase = pod.getStatus().getPhase();
                        String reason = pod.getStatus().getReason();
                        String message = pod.getStatus().getMessage();

                        podStatusList.add(new PodStatusInfo(name, phase, reason, message));

                        if (!"Running".equalsIgnoreCase(phase)) {
                            allRunning = false;
                        }
                    }
                }

                if (allRunning) {
                    System.out.println("✅ All pods are running in namespace: " + namespace);
                    return podStatusList;
                }

                Thread.sleep(3000);
            }

            System.out.println("❌ Timeout reached. Final pod statuses:");
            podStatusList.forEach(p ->
                    System.out.printf("Pod: %s | Phase: %s | Reason: %s\n", p.getName(), p.getPhase(), p.getReason())
            );

            return podStatusList;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // return empty on error
        }
    }

    public static boolean scaleDeployment(String namespace, String deploymentName, int replicas) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {

            if (deploymentName != null && !deploymentName.isBlank()) {
                // Scale a specific deployment
                client.apps()
                        .deployments()
                        .inNamespace(namespace)
                        .withName(deploymentName)
                        .scale(replicas, true);

                System.out.printf("✅ Scaled deployment '%s' in namespace '%s' to %d replicas.%n",
                        deploymentName, namespace, replicas);
            } else {
                // Scale all deployments in the namespace
                var deployments = client.apps()
                        .deployments()
                        .inNamespace(namespace)
                        .list()
                        .getItems();

                for (var deployment : deployments) {
                    String name = deployment.getMetadata().getName();
                    client.apps()
                            .deployments()
                            .inNamespace(namespace)
                            .withName(name)
                            .scale(replicas, true);

                    System.out.printf("✅ Scaled deployment '%s' in namespace '%s' to %d replicas.%n",
                            name, namespace, replicas);
                }
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.printf("❌ Failed to scale deployment(s) in namespace '%s': %s%n", namespace, e.getMessage());
            return false;
        }
    }

    public static boolean deleteNamespaceIfEmpty(String namespace) {

        try (KubernetesClient client = new KubernetesClientBuilder().build()) {

            boolean hasResources = !client.pods().inNamespace(namespace).list().getItems().isEmpty()
                    || !client.apps().deployments().inNamespace(namespace).list().getItems().isEmpty()
                    || !client.services().inNamespace(namespace).list().getItems().isEmpty()
                    || !client.configMaps().inNamespace(namespace).list().getItems().isEmpty()
                    || !client.secrets().inNamespace(namespace).list().getItems().isEmpty();

            if (hasResources) {
                System.out.printf("⚠️ Namespace '%s' still contains resources. Aborting delete.%n", namespace);
                return false;
            }

            List<StatusDetails> details = client.namespaces().withName(namespace).delete();

            boolean deleted = details != null && !details.isEmpty();

            if (deleted) {
                System.out.printf("✅ Namespace '%s' deleted successfully.%n", namespace);
                return true;
            } else {
                System.err.printf("❌ Failed to delete namespace '%s'.%n", namespace);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
