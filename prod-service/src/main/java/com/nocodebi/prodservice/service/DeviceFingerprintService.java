package com.nocodebi.prodservice.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class DeviceFingerprintService {

    public static String generateDeviceFingerprint() {
        List<String> identifiers = new ArrayList<>();

        // 1. MAC Address (Primary identifier)
        String mac = getMACAddress();
        if (!mac.isEmpty()) identifiers.add("MAC=" + mac);

        // 2. System identifiers (Fallbacks)
        identifiers.add("OS=" + System.getProperty("os.name"));
        identifiers.add("ARCH=" + System.getProperty("os.arch"));

        // 3. Hardware-specific (Requires OS-specific handling)
        String diskId = getDiskIdentifier();
        if (!diskId.isEmpty()) identifiers.add("DISK=" + diskId);

        // 4. Generate SHA-256 hash
        return sha256(String.join("|", identifiers));
    }

    private static String getMACAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || iface.isVirtual() || !iface.isUp()) continue;

                byte[] mac = iface.getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            // Log error if needed
        }
        return "";
    }

    private static String getDiskIdentifier() {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                return getWindowsDiskId();
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                return getUnixDiskId();
            }
        } catch (Exception e) {
            // Log error if needed
        }
        return "";
    }

    private static String getWindowsDiskId() throws Exception {
        Process process = Runtime.getRuntime().exec(
                new String[]{"wmic", "path", "win32_physicalmedia", "get", "SerialNumber"}
        );
        return parseCommandOutput(process, "SerialNumber");
    }

    private static String getUnixDiskId() throws Exception {
        // Try volume ID first (works without root)
        Process lsblk = Runtime.getRuntime().exec(
                new String[]{"lsblk", "-dno", "UUID"}
        );
        String uuid = parseCommandOutput(lsblk, "");
        if (!uuid.isEmpty()) return uuid;

        // Fallback to physical serial (may require root)
        Process hdparm = Runtime.getRuntime().exec(
                new String[]{"sh", "-c", "hdparm -I /dev/sda 2>/dev/null | grep 'Serial\\ Number'"}
        );
        return parseCommandOutput(hdparm, "Serial Number:").trim();
    }

    private static String parseCommandOutput(Process process, String filter) throws Exception {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        )) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (filter.isEmpty() || line.contains(filter)) {
                    output.append(line.replace(filter, ""));
                }
            }
        }
        process.waitFor();
        return output.toString().replaceAll("\\s+", "");
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}