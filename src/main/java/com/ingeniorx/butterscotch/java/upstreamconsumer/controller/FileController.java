package com.ingeniorx.butterscotch.java.upstreamconsumer.controller;

import com.ingeniorx.butterscotch.java.upstreamconsumer.service.LGCFileService;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

    @Autowired
    LGCFileService fileService;

    @PostMapping("/validate")
    public void readFile(@AuthenticationPrincipal String principal, @RequestParam String fileName, @RequestBody String lgcSample){
        fileService.readFile(lgcSample,fileName,principal);
    }
}
