package com.nocodebi.service.methods;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertImporter {

    public static void importCertificate(String certFilePath, String alias) {
        String cacertsPath = System.getProperty("java.home") + "/lib/security/cacerts";
        String storePassword = "changeit";

        System.out.println(System.getProperty("java.home"));

        try (FileInputStream certInput = new FileInputStream(certFilePath)) {
            // Load existing truststore
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (FileInputStream in = new FileInputStream(cacertsPath)) {
                trustStore.load(in, storePassword.toCharArray());
            }

            // Load certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(certInput);

            // Import into truststore
            trustStore.setCertificateEntry(alias, cert);

            // Save updated truststore
            try (FileOutputStream out = new FileOutputStream(cacertsPath)) {
                trustStore.store(out, storePassword.toCharArray());
            }

            System.out.println("✅ Certificate imported successfully with alias: " + alias);
        } catch (Exception e) {
            System.err.println("❌ Failed to import certificate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        importCertificate("path/to/k8s-ca.crt", "k8s-cluster");
    }
}
