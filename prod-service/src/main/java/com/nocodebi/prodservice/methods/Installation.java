package com.nocodebi.prodservice.methods;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocodebi.prodservice.constant.Constant;
import com.nocodebi.prodservice.model.*;
import com.nocodebi.prodservice.service.DeviceFingerprintService;
import com.nocodebi.prodservice.utils.Utilities;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.ContainerMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetricsList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public static AppContext buildApplication(AppContext context) {
        try {

            String premisesSHA = DeviceFingerprintService.generateDeviceFingerprint();

            if (context.getPremiseSHA() != null
                    && !context.getPremiseSHA().isEmpty()
                    && !premisesSHA.equals(context.getPremiseSHA())) {

                return null;

            }

            List<String> command = new ArrayList<>(List.of(
                    Constant.HELM,
                    Constant.UPGRADE,
                    Constant._INSTALL,
                    context.getStageName() + context.getAppName(),
                    Utilities.isNotNullOrBlank(context.getChartURL()) ? context.getChartURL() : Constant.APP_URL,
                    Constant._NAMESPACE + context.getStageName() + context.getAppName(),
                    Constant._CREATE_NAMESPACE,
                    Constant._SET,
                    Constant.GLOBAL_APPNAME + context.getStageName() + context.getAppName() +
                            Constant.COMMA + Constant.GLOBAL_INGRESS_URL + context.getAppURL() +
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
            System.out.println("Error building application [" + context.getStageName() + context.getAppName() + "]: " + e.getMessage());
            return null;
        }
    }

    public static AppContext uninstallApplication(AppContext context) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            List<String> command = List.of(
                    Constant.HELM,
                    Constant.UNINSTALL,
                    context.getStageName() + context.getAppName(),
                    Constant._NAMESPACE + context.getStageName() + context.getAppName()
            );

            // Debug: Uncomment if needed
            // System.out.println("uninstallApplication >>> Command: " + String.join(" ", command));

            CommandResult result = runProcess(command);

            if (result.isSuccess() && result.getStdout().contains(String.format("release \"%s\" uninstalled", context.getStageName() + context.getAppName()))) {

                context.setPremiseSHA("");

                deleteNamespaceIfEmpty(context.getStageName() + context.getAppName());

                return context;

            } else {

                System.err.println("Error:\n" + result.getStderr());

                return null;

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error uninstalling application [" + context.getStageName() + context.getAppName() + "]: " + e.getMessage());
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

    public static List<PodSummary> getPodStatusAsJson(String namespace,
                                                      String podName) {

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
                summary.status = pod.getStatus().getPhase().toLowerCase();

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

    public static List<DeploymentMetricsSummary> getDeploymentMetricsSummary(String namespace, String deploymentName) {

        try (KubernetesClient client = new KubernetesClientBuilder().build()) {

            List<DeploymentMetricsSummary> summaries = new ArrayList<>();

            List<Deployment> deployments;

            if (deploymentName != null && !deploymentName.isBlank()) {
                var dep = client.apps().deployments().inNamespace(namespace).withName(deploymentName).get();
                deployments = (dep != null) ? List.of(dep) : Collections.emptyList();
            } else {
                deployments = client.apps().deployments().inNamespace(namespace).list().getItems();
            }

            for (var deployment : deployments) {
                String name = deployment.getMetadata().getName();
                var matchLabels = deployment.getSpec().getSelector().getMatchLabels();

                if (matchLabels == null || matchLabels.isEmpty()) {
                    continue;
                }

                var pods = client.pods()
                        .inNamespace(namespace)
                        .withLabels(matchLabels)
                        .list()
                        .getItems();

                if (pods == null || pods.isEmpty()) {
                    continue;
                }

                long totalCpuMilli = 0;
                long totalMemoryBytes = 0;
                int podCount = 0;

                for (var pod : pods) {
                    var podMetric = client.top()
                            .pods()
                            .inNamespace(namespace)
                            .withName(pod.getMetadata().getName())
                            .metric();

                    if (podMetric != null && podMetric.getContainers() != null) {
                        for (var container : podMetric.getContainers()) {
                            var usage = container.getUsage();
                            if (usage != null) {
                                totalCpuMilli += parseCpuToMilli(String.valueOf(usage.get("cpu")));
                                totalMemoryBytes += parseMemoryToBytes(String.valueOf(usage.get("memory")));
                            }
                        }
                        podCount++;
                    }
                }

                if (podCount > 0) {
                    long avgCpu = totalCpuMilli / podCount;
                    long avgMemory = totalMemoryBytes / podCount;

                    summaries.add(new DeploymentMetricsSummary(
                            name,
                            String.valueOf(avgCpu),
//                            avgCpu + "m",
                            formatBytesToReadableMemory(avgMemory)
//                            formatBytesToMi(avgMemory) + "Mi"
                    ));
                }
            }

            return summaries;

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static long parseCpuToMilli(String cpu) {
        if (cpu.endsWith("n")) {
            return Long.parseLong(cpu.replace("n", "")) / 1_000_000; // nano to milli
        } else if (cpu.endsWith("m")) {
            return Long.parseLong(cpu.replace("m", ""));
        } else {
            return Long.parseLong(cpu) * 1000; // cores to milli
        }
    }

    private static long parseMemoryToBytes(String memory) {
        if (memory.endsWith("Ki")) {
            return Long.parseLong(memory.replace("Ki", "")) * 1024;
        } else if (memory.endsWith("Mi")) {
            return Long.parseLong(memory.replace("Mi", "")) * 1024 * 1024;
        } else if (memory.endsWith("Gi")) {
            return Long.parseLong(memory.replace("Gi", "")) * 1024 * 1024 * 1024;
        } else {
            return Long.parseLong(memory); // assume bytes
        }
    }

    private static String formatBytesToReadableMemory(long bytes) {
        if (bytes >= 1024L * 1024 * 1024) {
            return (bytes / (1024L * 1024 * 1024)) + "GB";
        } else if (bytes >= 1024L * 1024) {
            return (bytes / (1024L * 1024)) + "MB";
        } else if (bytes >= 1024L) {
            return (bytes / 1024L) + "KB";
        } else {
            return String.valueOf(bytes); // bytes as is
        }
    }

    private static long formatBytesToMi(long bytes) {
        return bytes / (1024 * 1024);
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

    public static boolean scaleDeployment(String namespace, String deploymentName, int replicas) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {

            if (deploymentName != null && !deploymentName.isBlank()) {

                deploymentName = findDeploymentName(client, namespace, deploymentName);

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

    public static String findDeploymentName(KubernetesClient client,
                                            String namespace,
                                            String serverTag) {

        if (serverTag != null && serverTag.startsWith("SERVER_")) {
            String suffix = serverTag.substring("SERVER_".length()).toLowerCase();

            var deployments = client.apps()
                    .deployments()
                    .inNamespace(namespace)
                    .list()
                    .getItems();

            for (var deployment : deployments) {
                String name = deployment.getMetadata().getName();
                if (name.toLowerCase().endsWith("-" + suffix + "-deployment")) {
                    return name;
                }
            }
        }
        return null;
    }

    public static String findServerTagFromDeploymentName(String deploymentName) {
        if (deploymentName != null && deploymentName.endsWith("-deployment")) {
            // Remove the "-deployment" suffix
            String baseName = deploymentName.substring(0, deploymentName.length() - "-deployment".length());

            // Find the last hyphen to isolate the suffix
            int lastDashIndex = baseName.lastIndexOf('-');
            if (lastDashIndex != -1 && lastDashIndex < baseName.length() - 1) {
                String suffix = baseName.substring(lastDashIndex + 1);
                return "SERVER_" + suffix.toUpperCase();
            }
        }
        return null;
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
