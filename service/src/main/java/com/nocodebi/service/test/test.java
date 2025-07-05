package com.nocodebi.service.test;

import com.nocodebi.service.methods.Installation;
import com.nocodebi.service.model.PodStatusInfo;

import java.util.List;

public class test {

    public static void main(String[] args) throws Exception {
        List<PodStatusInfo> statuses = Installation.waitForPodsRunning("kube-system", null, 120);
        for (PodStatusInfo info : statuses) {
            System.out.printf("Pod: %s | Status: %s\n", info.getName(), info.getPhase());
        }
    }

}
