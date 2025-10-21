package com.pcadvisor.hardwareai.service;

import com.pcadvisor.hardwareai.model.HardwareInfo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ApplicationCompatibilityService {

    public static class AppResult {
        public String name;
        public String image;
        public boolean compatible;
        public String reason;
        public List<String> alternatives;
        public String description;
        public int requiredRamGb;
    }

    private final Map<String, AppDefinition> catalog = new HashMap<>();

    private static class AppDefinition {
        String name;
        String image;
        String description;
        int minRam;
        List<String> cpuKeywords;
        List<String> gpuKeywords;
        List<String> alternatives;
        AppDefinition(String name, String image, String description, int minRam, List<String> cpuKeywords, List<String> gpuKeywords, List<String> alternatives) {
            this.name = name; this.image = image; this.description = description; this.minRam = minRam;
            this.cpuKeywords = cpuKeywords; this.gpuKeywords = gpuKeywords; this.alternatives = alternatives;
        }
    }

    public ApplicationCompatibilityService() {
        catalog.put("photoshop", new AppDefinition(
                "Adobe Photoshop",
                "https://upload.wikimedia.org/wikipedia/commons/a/af/Adobe_Photoshop_CC_icon.svg",
                "Editor de imágenes profesional.",
                8,
                Arrays.asList("i5","i7","ryzen","intel","amd"),
                Arrays.asList("nvidia","amd","intel"),
                Arrays.asList("GIMP","Photopea")
        ));
        catalog.put("premiere", new AppDefinition(
                "Adobe Premiere Pro",
                "https://upload.wikimedia.org/wikipedia/commons/4/40/Adobe_Premiere_Pro_CC_icon.svg",
                "Editor de video profesional.",
                16,
                Arrays.asList("i7","ryzen 7","ryzen"),
                Arrays.asList("nvidia","amd"),
                Arrays.asList("DaVinci Resolve","Shotcut")
        ));
        catalog.put("valorant", new AppDefinition(
                "Valorant",
                "https://seeklogo.com/images/V/valorant-logo-4A1A16E7A5-seeklogo.com.png",
                "Shooter competitivo.",
                4,
                Arrays.asList("i3","i5","ryzen"),
                Arrays.asList("nvidia","amd","intel"),
                Arrays.asList("CS2","Apex Legends")
        ));
        catalog.put("autocad", new AppDefinition(
                "AutoCAD",
                "https://upload.wikimedia.org/wikipedia/commons/d/d3/AutoCAD_logo.svg",
                "Software CAD profesional.",
                16,
                Arrays.asList("i5","i7","ryzen"),
                Arrays.asList("nvidia","amd"),
                Arrays.asList("FreeCAD","DraftSight")
        ));
    }

    public AppResult analyzeApp(String queryName, HardwareInfo hw) {
        AppResult out = new AppResult();
        if (queryName == null || queryName.isBlank()) {
            out.name = "Desconocida"; out.image = defaultImage(); out.compatible = false;
            out.reason = "No se proporcionó el nombre de la aplicación."; out.alternatives = List.of("Buscar nombre válido");
            return out;
        }

        String key = queryName.trim().toLowerCase();
        AppDefinition def = catalog.get(key);
        if (def == null) {
            for (AppDefinition d : catalog.values()) {
                if (d.name.toLowerCase().contains(key) || key.contains(d.name.toLowerCase())) { def = d; break; }
            }
        }

        if (def == null) {
            out.name = capitalize(queryName);
            out.image = tryLogo(queryName);
            out.compatible = genericHeuristic(hw);
            out.reason = out.compatible ? "Tu equipo ejecuta aplicaciones básicas correctamente." : "Tu equipo podría no ser suficiente para aplicaciones modernas. Revisa requisitos oficiales.";
            out.alternatives = List.of("Usar versiones web o alternativas ligeras");
            return out;
        }

        out.name = def.name;
        out.image = def.image != null ? def.image : defaultImage();
        out.description = def.description;
        out.requiredRamGb = def.minRam;
        out.alternatives = def.alternatives;

        double ram = hw != null ? hw.getRamTotalGb() : 0;
        String cpu = hw != null && hw.getCpuModel() != null ? hw.getCpuModel().toLowerCase() : "";
        String gpu = hw != null && hw.getGpus() != null && !hw.getGpus().isEmpty() ? String.join(" ", hw.getGpus()).toLowerCase() : "";

        boolean ramOk = ram >= def.minRam;
        boolean cpuOk = def.cpuKeywords.isEmpty() ? true : def.cpuKeywords.stream().anyMatch(k -> cpu.contains(k));
        boolean gpuOk = def.gpuKeywords.isEmpty() ? true : def.gpuKeywords.stream().anyMatch(k -> gpu.contains(k));

        out.compatible = ramOk && cpuOk && gpuOk;

        StringBuilder reason = new StringBuilder();
        if (out.compatible) {
            reason.append("✅ Tu PC cumple los requisitos: RAM ").append(ram).append(" GB (min ").append(def.minRam).append(" GB). ");
            reason.append(cpuOk ? "CPU: OK. " : "CPU: puede quedarse corto. ");
            reason.append(gpuOk ? "GPU: OK." : "GPU: puede quedarse corto.");
        } else {
            reason.append("⚠️ No cumple: ");
            if (!ramOk) reason.append("RAM insuficiente ("+ram+" < "+def.minRam+"). ");
            if (!cpuOk) reason.append("CPU no coincide con palabras clave esperadas. ");
            if (!gpuOk) reason.append("GPU no cumple palabras clave esperadas. ");
            reason.append("Si la instalas a la fuerza, la app puede fallar, provocar lags o cuelgues.");
        }
        out.reason = reason.toString();

        return out;
    }

    public List<String> listKnownApps() {
        List<String> names = new ArrayList<>();
        for (AppDefinition d : catalog.values()) names.add(d.name);
        return names;
    }

    private boolean genericHeuristic(HardwareInfo hw) {
        if (hw == null) return false;
        return hw.getRamTotalGb() >= 4 && hw.getCpuPhysicalCores() >= 2;
    }

    private String tryLogo(String name) {
        String domain = name.toLowerCase().replaceAll("[^a-z0-9]", "") + ".com";
        return "https://logo.clearbit.com/" + domain;
    }

    private String defaultImage() {
        return "https://cdn-icons-png.flaticon.com/512/1828/1828817.png";
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
}