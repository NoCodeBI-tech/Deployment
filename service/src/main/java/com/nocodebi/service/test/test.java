package com.nocodebi.service.test;

import com.nocodebi.service.utils.Utilities;

public class test {

    public static void main(String[] args) throws Exception {
//        List<PodStatusInfo> statuses = Installation.waitForPodsRunning("kube-system", null, 120);
//        for (PodStatusInfo info : statuses) {
//            System.out.printf("Pod: %s | Status: %s\n", info.getName(), info.getPhase());
//        }
        System.out.println(Utilities.getLinuxStyleDataPath());
    }

}
