package com.nocodebi.prodservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class AppContext {

    private String stageId;

    private String appId;

    private String stageName;

    private String appName;

    private String versionId;

    private String userId;

    private String centralURL;

    private String productURL;

    private String appURL;

    private String premiseSHA;

    private String coreJarURL;

    private String m2ZipURL;

    private String appDataPath;

    private String chartURL;

    private String timestamp;

    public AppContext() {

    }

    public void setDefault() {

        this.stageId = "";

        this.appId = "";

        this.stageName = "";

        this.appName = "";

        this.versionId = "";

        this.userId = "";

        this.centralURL = "";

        this.productURL = "";

        this.appURL = "";

        this.premiseSHA = "";

        this.coreJarURL = "";

        this.m2ZipURL = "";

        this.chartURL = "";

        this.appDataPath = "/app/data";

        this.timestamp = String.valueOf(System.currentTimeMillis());

    }

    public String getStageId() {

        return stageId;

    }

    public void setStageId(String stageId) {

        this.stageId = stageId;

    }

    public String getAppId() {

        return appId;

    }

    public void setAppId(String appId) {

        this.appId = appId;

    }

    public String getStageName() {

        return stageName;

    }

    public void setStageName(String stageName) {

        this.stageName = stageName;

    }

    public String getAppName() {

        return appName;

    }

    public void setAppName(String appName) {

        this.appName = appName;

    }

    public String getVersionId() {

        return versionId;

    }

    public void setVersionId(String versionId) {

        this.versionId = versionId;

    }

    public String getUserId() {

        return userId;

    }

    public void setUserId(String userId) {

        this.userId = userId;

    }

    public String getCentralURL() {

        return centralURL;

    }

    public void setCentralURL(String centralURL) {

        this.centralURL = centralURL;

    }

    public String getProductURL() {

        return productURL;

    }

    public void setProductURL(String productURL) {

        this.productURL = productURL;

    }

    public String getAppURL() {

        return appURL;

    }

    public void setAppURL(String appURL) {

        this.appURL = appURL;

    }

    public String getPremiseSHA() {

        return premiseSHA;

    }

    public void setPremiseSHA(String premiseSHA) {

        this.premiseSHA = premiseSHA;

    }

    public String getTimestamp() {

        return timestamp;

    }

    public void setTimestamp(String timestamp) {

        this.timestamp = timestamp;

    }

    public String getCoreJarURL() {

        return coreJarURL;

    }

    public void setCoreJarURL(String coreJarURL) {

        this.coreJarURL = coreJarURL;

    }

    public String getM2ZipURL() {

        return m2ZipURL;

    }

    public void setM2ZipURL(String m2ZipURL) {

        this.m2ZipURL = m2ZipURL;

    }

    public String getChartURL() {

        return chartURL;

    }

    public void setChartURL(String chartURL) {

        this.chartURL = chartURL;

    }

    public String getAppDataPath() {

        return appDataPath;

    }

    public void setAppDataPath(String appDataPath) {

        this.appDataPath = appDataPath;

    }

}
