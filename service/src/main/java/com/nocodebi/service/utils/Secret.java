package com.nocodebi.service.utils;

public class Secret {

    protected static String JWT_SECRET = "YourJWTSecretKeyMustBeAtLeast256BitsLongForHS256!";

    protected static final String ENCRYPTION_SECRET = "NoCodeBIUndrWify";

    protected static long JWT_EXPIRE_IN_MS = 3600000;

}
