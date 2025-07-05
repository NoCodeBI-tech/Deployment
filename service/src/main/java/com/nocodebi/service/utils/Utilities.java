package com.nocodebi.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nocodebi.service.constant.Constant;
import com.nocodebi.service.cookieManager.CookieStoreManager;
import io.fabric8.kubernetes.api.model.Pod;
import jakarta.servlet.http.HttpServletRequest;
import okhttp3.OkHttpClient;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Utilities {

    public static <T> T fromJson(Object object, Class<T> clazz) {

        Gson gson = new Gson();

        try {

            if (object instanceof String) {

                return gson.fromJson((String) object, clazz);

            } else if (object instanceof JsonNode) {

                return gson.fromJson(new ObjectMapper().writeValueAsString(object), clazz);

            }

        } catch (JsonSyntaxException | JsonProcessingException e) {

            throw new RuntimeException("Error converting Json to Object: " + e.getMessage(), e);

        } finally {

            gson = null;

        }

        return null;

    }

    public static String toJson(Object object) {

        Gson gson = new Gson();

        try {

            return gson.toJson(object);

        } catch (JsonSyntaxException e) {

            throw new RuntimeException("Error converting Object to String: " + e.getMessage(), e);

        } finally {

            gson = null;

        }
    }

    public static HttpResponse<String> apiCall(CookieStoreManager cookieStoreManager,
                                               String url, String endpoint,
                                               String body) {

        HttpResponse<String> response = null;

        String loginUrl = null;

        String cookieHeader = null;

        HttpClient client = null;

        HttpRequest request = null;

        try {

            loginUrl = url.concat(endpoint);

            cookieHeader = cookieStoreManager.getCookieHeader();

            client = cookieStoreManager.getClient();

            request = HttpRequest.newBuilder()
                    .uri(URI.create(loginUrl))
                    .header("Content-Type", "application/json")
                    .header("Cookie", cookieHeader)
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response;

        } catch (Exception e) {

            e.printStackTrace();

            return response;

        }

    }

    public static Map<String, String> setup_Wild_Card_Certificate() {

        try {

            Security.addProvider(new BouncyCastleProvider());

            String domain = "*.nocodebi.io";

            String certName = "nocodebi.io";

            String commonName = "*.nocodebi.io";

            String password = "nocodebi";  // Should be encrypted in production

            Path tempDir = Files.createTempDirectory("nocodebi_cert_setup");
            Path keyPath = tempDir.resolve(certName + ".key");
            Path crtPath = tempDir.resolve(certName + ".crt");
            Path pfxPath = tempDir.resolve(certName + ".pfx");

            // 1. Generate RSA Key Pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // 2. Build certificate with SAN
            X500Name issuer = new X500Name("CN=" + commonName);
            BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
            Date notBefore = new Date();
            Date notAfter = new Date(notBefore.getTime() + 825L * 24 * 60 * 60 * 1000);  // 825 days

            // SAN Extension
            GeneralName[] altNames = new GeneralName[]{
                    new GeneralName(GeneralName.dNSName, "*.nocodebi.io"),
                    new GeneralName(GeneralName.dNSName, "nocodebi.io")
            };
            GeneralNames subjectAltName = new GeneralNames(altNames);

            JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer, serial, notBefore, notAfter, issuer, publicKey
            );

            certBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAltName);

            ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                    .setProvider("BC").build(privateKey);

            X509Certificate certificate = new JcaX509CertificateConverter()
                    .setProvider("BC").getCertificate(certBuilder.build(signer));

            // 3. Export .key
            try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(keyPath.toFile()))) {
                writer.writeObject(privateKey);
            }

            // 4. Export .crt
            try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(crtPath.toFile()))) {
                writer.writeObject(certificate);
            }

            // 5. Export to .pfx
            KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
            pkcs12.load(null, null);
            pkcs12.setKeyEntry(certName, privateKey, password.toCharArray(), new Certificate[]{certificate});
            try (FileOutputStream fos = new FileOutputStream(pfxPath.toFile())) {
                pkcs12.store(fos, password.toCharArray());
            }

            // 6. Install certificate to Windows Root store using certutil
            System.out.println("🔐 Installing certificate to Windows ROOT store...");
            Process proc = new ProcessBuilder("certutil", "-f", "-p", password, "-importpfx", pfxPath.toString())
                    .inheritIO()
                    .start();
            proc.waitFor();

            // 7. Add hosts file entry
            String hostsEntry = "127.0.0.1 local-product.nocodebi.io";
            Path hostsPath = getHostFilePath();
            List<String> hostsLines = Files.readAllLines(hostsPath);

            if (!hostsLines.contains(hostsEntry)) {

                Files.write(hostsPath, Collections.singletonList(hostsEntry), StandardOpenOption.APPEND);

//                System.out.println("🌐 Added hosts entry: " + hostsEntry);

            }

//            else {

//                System.out.println("ℹ️ Hosts entry already exists.");

//            }

            System.out.println("🎉 All done! You can now access: https://local-product.nocodebi.io");

            // Read the full PEM files
            String keyPem = Files.readString(keyPath);
            String crtPem = Files.readString(crtPath);

//            System.out.println("privateKeyPem >>> " + keyPem);
//            System.out.println("certificatePem >>> " + crtPem);

            String keyBase64 = Base64.getEncoder().encodeToString(keyPem.getBytes(StandardCharsets.UTF_8));
            String crtBase64 = Base64.getEncoder().encodeToString(crtPem.getBytes(StandardCharsets.UTF_8));

            Map<String, String> output = new HashMap<>();
            output.put("crt", crtBase64);
            output.put("key", keyBase64);

            Utilities.saveDataInWindows(Constant.CERTIFICATE_PATH, output);

            return output;

        } catch (Exception e) {

            e.printStackTrace();

            return new HashMap<>();

        }
    }

    public static void uninstall_Wild_Card_Certificate() {

        try {

            String certSubjectCN = "CN=*.nocodebi.io"; // The CN of cert to delete, from setup

            String hostsEntry = "127.0.0.1 local-product.nocodebi.io";

            Path hostsPath = getHostFilePath();

            // 1. Find thumbprint of cert in Root store with matching CN
            Process findProc = new ProcessBuilder("powershell", "-Command",
                    "Get-ChildItem Cert:\\LocalMachine\\Root | Where-Object {$_.Subject -eq '" + certSubjectCN + "'} | Select-Object -ExpandProperty Thumbprint")
                    .redirectErrorStream(true)
                    .start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(findProc.getInputStream()));

            String thumbprint = reader.readLine();

            findProc.waitFor();

            if (thumbprint != null && !thumbprint.isEmpty()) {

                // 2. Delete certificate by thumbprint
                System.out.println("Deleting certificate with thumbprint: " + thumbprint);

                Process delProc = new ProcessBuilder("certutil", "-delstore", "Root", thumbprint)
                        .inheritIO()
                        .start();

                delProc.waitFor();

            } else {

                System.out.println("Certificate not found in Root store. Skipping deletion.");

            }

            // 3. Remove hosts entry
            List<String> hostsLines = Files.readAllLines(hostsPath);

            List<String> updatedLines = new ArrayList<>();

            boolean removed = false;

            for (String line : hostsLines) {

                if (line.trim().equalsIgnoreCase(hostsEntry)) {

                    removed = true;

                    continue; // skip this line

                }

                updatedLines.add(line);

            }

            if (removed) {

                Files.write(hostsPath, updatedLines, StandardOpenOption.TRUNCATE_EXISTING);

                System.out.println("Removed hosts entry: " + hostsEntry);

            } else {

                System.out.println("Hosts entry not found. Skipping hosts file modification.");

            }

            System.out.println("Uninstall complete.");

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public static boolean saveDataInWindows(String path, Object obj) {
        try {

            Path filePath = Paths.get(System.getProperty(Constant.USER_HOME), path);

            // ✅ Ensure parent directory exists
            if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());  // Use createDirectories, not createDirectory
            }

            // ✅ Try-with-resources to auto-close stream
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(obj);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Object readDataFromWindows(String path) {
        Path filePath = Paths.get(System.getProperty(Constant.USER_HOME), path);

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addHostEntry(String IP, String URL) {

        try {

            String hostsEntry = IP + " " + URL;

            Path hostsPath = getHostFilePath();

            if (!Files.readAllLines(hostsPath).contains(hostsEntry)) {

                Files.write(hostsPath, Collections.singletonList(hostsEntry), StandardOpenOption.APPEND);

                System.out.println("🌐 Added hosts entry: " + hostsEntry);

            } else {

                System.out.println("ℹ️ Hosts entry already exists.");

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public static String calculatePodAge(Instant startTime) {
        Duration duration = Duration.between(startTime, Instant.now());
        long days = duration.toDays();
        return days + "d";
    }

    public static String formatReady(Pod pod) {
        long readyCount = pod.getStatus().getContainerStatuses().stream().filter(cs -> cs.getReady()).count();
        int total = pod.getStatus().getContainerStatuses().size();
        return readyCount + "/" + total;
    }

    public static String getLinuxStyleDataPath() {
        String userHome = System.getProperty("user.home");
        String osName = System.getProperty("os.name").toLowerCase();

        String path = userHome;

        // Convert Windows-style paths (e.g., C:\\Users\\user\\data) to Linux-style (e.g., /c/Users/user/data)
        if (osName.contains("win")) {
            path = path.replace("\\", "/"); // Normalize slashes
            if (path.length() > 2 && path.charAt(1) == ':') {
                // Convert C:/Users/... to /c/Users/...
                char driveLetter = Character.toLowerCase(path.charAt(0));
                path = "/" + driveLetter + path.substring(2);
            }
        }

        return path;
    }

    public static void addHostEntryWithAppName(String appName) {

        try {

            String hostsEntry = Constant.LOCALHOST + " " + String.format(Constant.UNFORMATTED_DOMAIN, appName);

            Path hostsPath = getHostFilePath();

            if (!Files.readAllLines(hostsPath).contains(hostsEntry)) {

                Files.write(hostsPath, Collections.singletonList(hostsEntry), StandardOpenOption.APPEND);

                System.out.println("🌐 Added hosts entry: " + hostsEntry);

            } else {

                System.out.println("ℹ️ Hosts entry already exists.");

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public static Path getHostFilePath() {

        return Paths.get(System.getenv("SystemRoot") + "\\System32\\drivers\\etc\\hosts");

    }

    public static boolean validateRequest(HttpServletRequest request) {

        JwtUtil jwtUtil = null;

        try {

            jwtUtil = new JwtUtil();

            return jwtUtil.validateToken(request.getHeader(Constant.ACCESSTOKEN));

        } catch (Exception e) {

            e.printStackTrace();

            return false;

        } finally {

            jwtUtil = null;

        }

    }

    public static OkHttpClient buildUnsafeHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create unsafe HTTP client", e);
        }
    }

    public static String getServerValue(String key) {

        return switch (key) {
            case "ADMIN_SERVER", "SERVER_ADMIN" -> "appserver-admin";
            case "CONNECTOR_SERVER", "SERVER_CONNECTOR" -> "appserver-connector";
            case "CONSOLIDATION_SERVER", "SERVER_CONSOLIDATION" -> "appserver-consolidation";
            case "SEARCH_SERVER", "SERVER_SEARCH" -> "appserver-search";
            case "VIEW_SERVER", "SERVER_VIEW" -> "appserver-view";
            case "CONFIGURATION_SERVER", "SERVER_CONFIGURATION" -> "appserver-configuration";
            case "WORKFLOW_SERVER", "SERVER_WORKFLOW" -> "appserver-workflow";
            case "FLOW_SERVER", "SERVER_FLOW" -> "appserver-flow";
            case "SECURITY_PROVIDER_SERVER", "SERVER_SECURITY_PROVIDER" -> "appserver-security-provider";
            default -> null;
        };

    }

}
