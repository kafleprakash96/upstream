package com.ingeniorx.butterscotch.java.upstreamconsumer.service;

import com.ingeniorx.butterscotch.java.upstreamconsumer.dto.RemediationQueueDto;

import java.io.IOException;

public interface LGCFileService {

    void readFile(String lgcData, String fileName, String token);

    boolean readFiles(String lgcData, String fileName, String token) throws IOException;

    RemediationQueueDto saveRemediationQueue(String lgcData, String fileName);
}
