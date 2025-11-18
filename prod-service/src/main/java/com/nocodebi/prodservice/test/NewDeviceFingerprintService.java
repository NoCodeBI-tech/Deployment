package com.nocodebi.prodservice.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.*;

public final class NewDeviceFingerprintService {

    // Optional env override for ops
    private static final String ENV_OVERRIDE = "NOCODEBI_DEVICE_ID";
    // Where we persist the computed ID (change path if you prefer)
    private static final Path STORE_PATH = Paths.get(System.getProperty("user.home"), ".nocodebi", "device.id");

    private static volatile String CACHED;

    /**
     * Deterministic device fingerprint (stable + persisted).
     */
    public static synchronized String generateDeviceFingerprint() {
        if (CACHED != null) return CACHED;

        // 0) Environment override (ops can pin an ID)
        String env = System.getenv(ENV_OVERRIDE);
        if (isNotBlank(env)) return CACHED = env.trim();

        // 1) If we’ve persisted before, reuse it
        String persisted = readPersisted();
        if (isNotBlank(persisted)) return CACHED = persisted;

        // 2) Build a stable, normalized material string (sorted keys)
        Map<String, String> ids = new TreeMap<>();
        ids.put("OS", System.getProperty("os.name", ""));
        ids.put("ARCH", System.getProperty("os.arch", ""));

        if (isWindows()) ids.putAll(windowsIds());
        else if (isMac()) ids.putAll(macIds());
        else if (isLinux()) ids.putAll(linuxIds());

        // Avoid volatile identifiers (e.g., MAC) by default.
        // If you *really* want MAC, add: ids.put("MAC", firstStableMac());

        String material = toDeterministicString(ids);
        String fp = sha256(material);

        // 3) Persist so future runs are identical even if hardware strings change
        persist(fp);
        return CACHED = fp;
    }

    // ---------- Platform detection ----------
    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private static boolean isMac() {
        return System.getProperty("os.name", "").toLowerCase().contains("mac");
    }

    private static boolean isLinux() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("nix") || os.contains("nux") || os.contains("aix") || os.contains("linux");
    }

    // ---------- Windows IDs ----------
    private static Map<String, String> windowsIds() {
        Map<String, String> m = new HashMap<>();
        // MachineGuid is the most stable (HKLM\SOFTWARE\Microsoft\Cryptography\MachineGuid)
        String machineGuid = execAndExtract(new String[]{
                "reg", "query", "HKLM\\SOFTWARE\\Microsoft\\Cryptography", "/v", "MachineGuid"
        }, line -> {
            // Example: "MachineGuid    REG_SZ    1a2b3c4d-..."
            if (line.contains("MachineGuid")) {
                String[] parts = line.trim().split("\\s+");
                return parts.length > 2 ? parts[parts.length - 1].trim() : "";
            }
            return "";
        });
        if (isNotBlank(machineGuid)) m.put("WIN_MACHINEGUID", machineGuid);

        // BIOS/Board/Computer UUIDs as fallbacks
        String csUuid = execFirstNonEmpty(new String[]{"wmic", "csproduct", "get", "UUID"});
        if (isLikelyGuid(csUuid)) m.put("WIN_CSPRODUCT_UUID", csUuid);

        String biosSn = execFirstNonEmpty(new String[]{"wmic", "bios", "get", "serialnumber"});
        if (isNotBlank(biosSn)) m.put("WIN_BIOS_SN", biosSn);

        return m;
    }

    // ---------- macOS IDs ----------
    private static Map<String, String> macIds() {
        Map<String, String> m = new HashMap<>();

        // IOPlatformUUID
        String ioreg = execAndExtract(new String[]{"sh", "-c", "ioreg -rd1 -c IOPlatformExpertDevice"},
                line -> line.contains("IOPlatformUUID") ? line.replaceAll(".*IOPlatformUUID\" = \"", "").replace("\"", "").trim() : "");
        if (isNotBlank(ioreg)) m.put("MAC_IOPLATFORMUUID", ioreg);

        // Serial Number (optional)
        String serial = execAndExtract(new String[]{"sh", "-c", "system_profiler SPHardwareDataType"},
                line -> line.contains("Serial Number") ? line.substring(line.lastIndexOf(':') + 1).trim() : "");
        if (isNotBlank(serial)) m.put("MAC_SERIAL", serial);

        return m;
    }

    // ---------- Linux IDs ----------
    private static Map<String, String> linuxIds() {
        Map<String, String> m = new HashMap<>();
        // /etc/machine-id (very stable on systemd systems)
        String machineId = readFirstLineIfExists("/etc/machine-id");
        if (isNotBlank(machineId)) m.put("LINUX_MACHINEID", machineId);

        // DMI product UUID (often world-readable on bare metal/VMs)
        String productUuid = readFirstLineIfExists("/sys/class/dmi/id/product_uuid");
        if (isNotBlank(productUuid)) m.put("LINUX_DMI_PRODUCT_UUID", productUuid);

        // Board serial (if exposed)
        String boardSerial = readFirstLineIfExists("/sys/class/dmi/id/board_serial");
        if (isNotBlank(boardSerial)) m.put("LINUX_BOARD_SERIAL", boardSerial);

        return m;
    }

    // ---------- Optional MAC (volatile) ----------
    // If you decide to include MAC, pick the lexicographically-smallest non-empty MAC for stability.
    @SuppressWarnings("unused")
    private static String firstStableMac() {
        try {
            Enumeration<java.net.NetworkInterface> ifaces = java.net.NetworkInterface.getNetworkInterfaces();
            List<String> macs = new ArrayList<>();
            while (ifaces.hasMoreElements()) {
                java.net.NetworkInterface ni = ifaces.nextElement();
                if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) continue;
                byte[] mac = ni.getHardwareAddress();
                if (mac == null || mac.length == 0) continue;
                StringBuilder sb = new StringBuilder();
                for (byte b : mac) sb.append(String.format("%02X", b));
                macs.add(sb.toString());
            }
            Collections.sort(macs);
            return macs.isEmpty() ? "" : macs.get(0);
        } catch (Exception ignore) {
            return "";
        }
    }

    // ---------- Helpers ----------
    private static String readPersisted() {
        try {
            if (Files.exists(STORE_PATH)) {
                return new String(Files.readAllBytes(STORE_PATH), StandardCharsets.UTF_8).trim();
            }
        } catch (IOException ignore) {
        }
        return "";
    }

    private static void persist(String value) {
        try {
            Files.createDirectories(STORE_PATH.getParent());
            Files.write(STORE_PATH, value.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            // Non-fatal: you still get a fingerprint, it just won't persist
        }
    }

    private static String readFirstLineIfExists(String path) {
        try {
            Path p = Paths.get(path);
            if (!Files.exists(p)) return "";
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
            return lines.isEmpty() ? "" : lines.get(0).trim();
        } catch (IOException e) {
            return "";
        }
    }

    private static String execFirstNonEmpty(String[] cmd) {
        return execAndExtract(cmd, line -> {
            String s = line.trim();
            // Skip headers like "UUID" / "SerialNumber"
            if (s.equalsIgnoreCase("UUID") || s.equalsIgnoreCase("SerialNumber")) return "";
            return s.matches(".*\\w.*") ? s : "";
        });
    }

    private static String execAndExtract(String[] cmd, java.util.function.Function<String, String> extractor) {
        try {
            Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            String out = "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String candidate = extractor.apply(line);
                    if (isNotBlank(candidate)) {
                        out = candidate.trim();
                        break;
                    }
                }
            }
            process.waitFor();
            return out == null ? "" : out.trim();
        } catch (Exception e) {
            return "";
        }
    }

    private static String toDeterministicString(Map<String, String> ids) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : ids.entrySet()) {
            if (isNotBlank(e.getValue())) {
                if (sb.length() > 0) sb.append('|');
                sb.append(e.getKey()).append('=').append(e.getValue().replaceAll("\\s+", ""));
            }
        }
        return sb.toString();
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                String h = Integer.toHexString(b & 0xff);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static boolean isLikelyGuid(String s) {
        return isNotBlank(s) && s.trim().matches("(?i)[0-9a-f\\-]{32,}");
    }
}
