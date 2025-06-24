package io.nocodebi.cookieManager;

import io.nocodebi.constant.Constant;
import io.nocodebi.utils.JwtUtil;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.nio.file.*;
import java.util.*;

public class CookieStoreManager {
    private final CookieManager cookieManager;
    private final Path cookieFilePath;

    private final JwtUtil jwtUtil;

    public CookieStoreManager() {
        this.cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        this.cookieFilePath = Paths.get(System.getProperty(Constant.USER_HOME), Constant.SESSION_PATH);
        this.jwtUtil = new JwtUtil();
    }

    public HttpClient getClient() {
        return HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();
    }

    public void saveCookies() throws IOException {

        CookieStore store = cookieManager.getCookieStore();
        List<SerializableCookie> serializableCookies = new ArrayList<>();

        for (URI uri : store.getURIs()) {

            List<HttpCookie> cookies = store.getCookies();

            for (HttpCookie cookie : cookies) {

                serializableCookies.add(new SerializableCookie(uri.toString(), cookie));

            }

        }

        Files.createDirectories(cookieFilePath.getParent());

        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(cookieFilePath))) {

            oos.writeObject(serializableCookies);

        }

    }

    public Map<String, String> loadCookies() throws IOException, ClassNotFoundException {

        if (!Files.exists(cookieFilePath)) return null;

        Map<String, String> token = new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(cookieFilePath))) {

            List<SerializableCookie> cookies = (List<SerializableCookie>) ois.readObject();

            for (SerializableCookie sc : cookies) {

                cookieManager.getCookieStore().add(new URI(sc.uri), sc.toHttpCookie());

                token.put(sc.name, sc.value);

            }

        } catch (URISyntaxException e) {

            throw new RuntimeException(e);

        }

        return token;

    }

    public void clearCookies(boolean removeFromDisk) throws IOException {

        cookieManager.getCookieStore().removeAll();

        if (removeFromDisk && Files.exists(cookieFilePath)) {

            Files.delete(cookieFilePath);

        }

    }

    public String getCookieHeader(){

        StringBuilder cookieHeader = new StringBuilder();

        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();

        for (HttpCookie cookie : cookies) {

            if (cookieHeader.length() > 0) cookieHeader.append("; ");

            cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue());

        }

        return cookieHeader.toString();

    }

    public boolean validateAccessToken(){

        if (!Files.exists(cookieFilePath)) return false;

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(cookieFilePath))) {

            List<SerializableCookie> cookies = (List<SerializableCookie>) ois.readObject();

            for (SerializableCookie sc : cookies) {

                if(sc.name.equals(Constant.ACCESSTOKEN)){

                    if(!jwtUtil.validateToken(sc.value)) return true;

                }

            }

            return false;

        } catch (IOException | ClassNotFoundException e) {

            throw new RuntimeException(e);

        }

    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    private static class SerializableCookie implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String uri;
        private final String name;
        private final String value;
        private final String domain;
        private final String path;
        private final long maxAge;
        private final boolean secure;
        private final boolean httpOnly;

        public SerializableCookie(String uri, HttpCookie cookie) {
            this.uri = uri;
            this.name = cookie.getName();
            this.value = cookie.getValue();
            this.domain = cookie.getDomain();
            this.path = cookie.getPath();
            this.maxAge = cookie.getMaxAge();
            this.secure = cookie.getSecure();
            this.httpOnly = cookie.isHttpOnly();
        }

        public HttpCookie toHttpCookie() {
            HttpCookie cookie = new HttpCookie(name, value);
            cookie.setDomain(domain);
            cookie.setPath(path);
            cookie.setMaxAge(maxAge);
            cookie.setSecure(secure);
            cookie.setHttpOnly(httpOnly);
            return cookie;
        }
    }

}
