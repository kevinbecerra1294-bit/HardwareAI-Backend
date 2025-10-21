package com.pcadvisor.hardwareai.controller;

import com.pcadvisor.hardwareai.model.HardwareInfo;
import com.pcadvisor.hardwareai.service.ApplicationCompatibilityService;
import com.pcadvisor.hardwareai.service.ApplicationCompatibilityService.AppResult;
import com.pcadvisor.hardwareai.service.HardwareInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
// Se eliminó @CrossOrigin(origins = "*") para evitar conflicto con CorsConfig.java
public class HardwareController {

    private final HardwareInfoService hardwareInfoService;
    private final ApplicationCompatibilityService appService;

    public HardwareController(HardwareInfoService hardwareInfoService, ApplicationCompatibilityService appService) {
        this.hardwareInfoService = hardwareInfoService;
        this.appService = appService;
    }

    @GetMapping("/hardware")
    public HardwareInfo getHardware() {
        return hardwareInfoService.getHardwareInfo();
    }

    @GetMapping("/apps/search")
    public AppResult searchApp(@RequestParam String name) {
        HardwareInfo hw = hardwareInfoService.getHardwareInfo();
        return appService.analyzeApp(name, hw);
    }

    @GetMapping("/apps/list")
    public List<String> listApps() {
        return appService.listKnownApps();
    }
}