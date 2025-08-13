package com.nocodebi.service.constant;

public class Constant {

    public static final String USER_HOME = "user.home";
    public static final String SESSION_PATH = "\\.nocodebi\\session.json";
    public static final String CERTIFICATE_PATH = "\\.nocodebi\\cert.json";
    public static final String PRODUCT_CONSOLE = "https://product.nocodebi.io";
    public static final String LOCAL_PRODUCT_CONSOLE = "https://local-product.nocodebi.io/";
    public static final String SERVICE = "http://localhost:9876";
    public static final String LOGIN = "/data/login/handleEmailPasswordAuth";
    public static final String test = "/data/profile/getSessionList";
    public static final String AUTHORIZATION = "Authorization";
    public static final String ACCESSTOKEN = "accessToken";
    public static final String REFRESHTOKEN = "refreshToken";


    //kubernetes
    public static final String HELM = "helm";
    public static final String KUBECTL = "kubectl";
    public static final String GET = "get";
    public static final String ALL = "all";
    public static final String DELETE = "delete";
    public static final String APPLY = "apply";
    public static final String _FILE = "-f";
    public static final String SCALE = "scale";
    public static final String GET_JSON = "-o json";
    public static final String DEPLOYMENT = "deployment";
    public static final String DASH = "-";
    public static final String RESULT = "result";
    public static final String EXITCODE = "exitCode";
    public static final String EQUALTO = "=";
    public static final String _INSTALL = "--install";
    public static final String TOP = "top";
    public static final String POD = "pod";
    public static final String PODS = "pods";
    public static final String UNINSTALL = "uninstall";
    public static final String UPGRADE = "upgrade";
    public static final String NAMESPACE = "namespace";
    public static final String _NAMESPACE = "--namespace=";
    public static final String _N = "-n";
    public static final String _CREATE_NAMESPACE = "--create-namespace";
    public static final String _SET = "--set";
    public static final String COMMA = ",";
    public static final String _REPLICAS = "--replicas=";
    public static final String STARTREPLICAS = "--replicas=1";
    public static final String STOPREPLICAS = "--replicas=0";
    public static final String GRAP = "| grep";
    public static final String DOUBLEAND = "&&";
    public static final String GLOBAL_APPNAME = "global.appName=";
    public static final String GLOBAL_TLS_CRT = "global.tls.crt=";
    public static final String GLOBAL_TLS_KEY = "global.tls.key=";
    public static final String GLOBAL_INGRESS_URL = "global.ingressURL=";
    public static final String GLOBAL_USER_HOME = "global.userHome=";
    public static final String GLOBAL_ENV_APP_CHART_URL = "global.env.CHART_REPO_URL=";
    public static final String GLOBAL_ENV_STAGE_ID = "global.env.STAGE_ID=";
    public static final String GLOBAL_ENV_APP_ID = "global.env.APP_ID=";
    public static final String GLOBAL_ENV_VERSION_ID = "global.env.VERSION_ID=";
    public static final String GLOBAL_ENV_USER_ID = "global.env.USER_ID=";
    public static final String GLOBAL_ENV_CENTRAL_SERVER_URL = "global.env.CENTRAL_SERVER_URL=";
    public static final String GLOBAL_ENV_PRODUCT_CONSOLE_SERVER_URL = "global.env.PRODUCT_CONSOLE_SERVER_URL=";
    public static final String GLOBAL_ENV_APP_SERVER_URL = "global.env.APP_SERVER_URL=";
    public static final String GLOBAL_ENV_PREMISES_SHA = "global.env.PREMISES_SHA=";
    public static final String GLOBAL_ENV_CORE_JAR_URL = "global.env.CORE_JAR_URL=";
    public static final String GLOBAL_ENV_M2_ZIP_URL = "global.env.M2_ZIP_URL=";
    public static final String GLOBAL_ENV_APP_DATA_PATH = "global.env.APP_DATA_PATH=";
    public static final String GLOBAL_ENV_INSTANCE_ID = "global.env.INSTANCE_ID=";
    public static final String GLOBAL_IMAGE_TAG_LATEST = "global.image.tag=latest";
    public static final String GLOBAL_IMAGE_TAG = "global.image.tag=";
    public static final String SEMICOLON = ";";
    // Content Urls
    public static final String APP_URL = "https://raw.githubusercontent.com/NoCodeBI-tech/Deployment/main/local_app.tgz";
    public static final String HOSTED_DATABASE_URL = "https://raw.githubusercontent.com/NoCodeBI-tech/Deployment/main/local_hosted_database_server.tgz";
    public static final String PRODUCT_CONSOLE_URL = "https://raw.githubusercontent.com/NoCodeBI-tech/Deployment/main/local_product.tgz";
    public static final String TRAEFIK_URL = "https://raw.githubusercontent.com/NoCodeBI-tech/Deployment/main/traefik/traefik.yml";
    public static final String TRAEFIK_CRD_CMD = "https://raw.githubusercontent.com/traefik/traefik/v2.10/docs/content/reference/dynamic-configuration/kubernetes-crd-definition-v1.yml";
    // Name
    public static final String PRODUCT_CONSOLE_NAME = "productconsole";
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    public static final String EXCEPTION = "EXCEPTION";
    public static final String EMPTY = "{}";

    // status
    public static final String UNAUTHORISED = "UNAUTHORISED";
    public static final String INSTALLATION_FAILED = "INSTALLATION FAILED";
    public static final String CRT = "crt";
    public static final String KEY = "key";
    public static final String LOCALHOST = "127.0.0.1";
    public static final String DOMAIN = ".nocodebi.io";
    public static final String UNFORMATTED_HTTPS_DOMAIN = "https://local-%s-%s.nocodebi.io";
    public static final String UNFORMATTED_DOMAIN = "local-%s-%s.nocodebi.io";
    public static String OBJECT_RETRIEVE_SUCCESSFULLY = "OBJECT_RETRIEVE_SUCCESSFULLY";
    public static String OBJECT_CREATED_SUCCESSFULLY = "OBJECT_CREATED_SUCCESSFULLY";
    public static String OBJECT_UPDATED_SUCCESSFULLY = "OBJECT_UPDATED_SUCCESSFULLY";
    public static String OBJECT_DELETED_SUCCESSFULLY = "OBJECT_DELETED_SUCCESSFULLY";

}