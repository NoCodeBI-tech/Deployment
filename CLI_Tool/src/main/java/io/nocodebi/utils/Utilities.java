package io.nocodebi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.beans.Expression;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.nio.file.*;
import java.security.*;
import java.util.*;

import java.security.cert.X509Certificate;
import java.security.cert.Certificate;

import io.nocodebi.App;
import io.nocodebi.cookieManager.CookieStoreManager;

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

        }catch (Exception e){

            e.printStackTrace();

            return response;

        }

    }

    public static Map<String, String> setup_Wild_Card_Certificate(){

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
            Path hostsPath = Paths.get(System.getenv("SystemRoot") + "\\System32\\drivers\\etc\\hosts");
            List<String> hostsLines = Files.readAllLines(hostsPath);

            if (!hostsLines.contains(hostsEntry)) {
                Files.write(hostsPath, Collections.singletonList(hostsEntry), StandardOpenOption.APPEND);
                System.out.println("🌐 Added hosts entry: " + hostsEntry);
            } else {
                System.out.println("ℹ️ Hosts entry already exists.");
            }

            System.out.println("🎉 All done! You can now access: https://local-product.nocodebi.io");

            // Read the full PEM files
            String privateKeyPem = Files.readString(keyPath);
            String certificatePem = Files.readString(crtPath);

            // Remove BEGIN/END lines and whitespace
            String privateKeyBase64 = privateKeyPem
                    .replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("\\s", ""); // remove all whitespace (newlines)

            String certificateBase64 = certificatePem
                    .replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("\\s", ""); // remove all whitespace (newlines)

            // Output
            System.out.println("🔑 Private Key (Base64 only):\n" + privateKeyBase64);
            System.out.println("📜 Certificate (Base64 only):\n" + certificateBase64);

            Map<String, String> output = new HashMap<>();
            output.put("crt", certificateBase64);
            output.put("key", privateKeyBase64);

            return output;

        }catch (Exception e){

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

    public static void addHostEntry(String IP, String URL){

        try {

            String hostsEntry = IP + " " + URL;

            Path hostsPath = getHostFilePath();

            if (!Files.readAllLines(hostsPath).contains(hostsEntry)) {

                Files.write(hostsPath, Collections.singletonList(hostsEntry), StandardOpenOption.APPEND);

                System.out.println("🌐 Added hosts entry: " + hostsEntry);

            } else {

                System.out.println("ℹ️ Hosts entry already exists.");

            }

        }catch (Exception e){

            e.printStackTrace();

        }

    }

    public static Path getHostFilePath(){

        return Paths.get(System.getenv("SystemRoot") + "\\System32\\drivers\\etc\\hosts");

    }


}
