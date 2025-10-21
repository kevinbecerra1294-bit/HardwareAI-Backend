package com.pcadvisor.hardwareai.model;

import java.util.List;
import java.util.Map;

public class HardwareInfo {
    private String cpuModel;
    private int cpuPhysicalCores;
    private int cpuLogicalCores;
    private double cpuLoadPercent;

    private double ramTotalGb;
    private double ramAvailableGb;

    private List<String> gpus;
    private List<Map<String,String>> disks; // model,sizeGB,serial
    private String os;
    private String architecture;
    private String baseboard;
    private String bios;
    private List<Map<String,String>> networkInterfaces; // name, mac, ip

    private String estimatedLifetime;

    public HardwareInfo() {}

    // getters / setters
    public String getCpuModel() { return cpuModel; }
    public void setCpuModel(String cpuModel) { this.cpuModel = cpuModel; }

    public int getCpuPhysicalCores() { return cpuPhysicalCores; }
    public void setCpuPhysicalCores(int cpuPhysicalCores) { this.cpuPhysicalCores = cpuPhysicalCores; }

    public int getCpuLogicalCores() { return cpuLogicalCores; }
    public void setCpuLogicalCores(int cpuLogicalCores) { this.cpuLogicalCores = cpuLogicalCores; }

    public double getCpuLoadPercent() { return cpuLoadPercent; }
    public void setCpuLoadPercent(double cpuLoadPercent) { this.cpuLoadPercent = cpuLoadPercent; }

    public double getRamTotalGb() { return ramTotalGb; }
    public void setRamTotalGb(double ramTotalGb) { this.ramTotalGb = ramTotalGb; }

    public double getRamAvailableGb() { return ramAvailableGb; }
    public void setRamAvailableGb(double ramAvailableGb) { this.ramAvailableGb = ramAvailableGb; }

    public List<String> getGpus() { return gpus; }
    public void setGpus(List<String> gpus) { this.gpus = gpus; }

    public List<Map<String, String>> getDisks() { return disks; }
    public void setDisks(List<Map<String, String>> disks) { this.disks = disks; }

    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }

    public String getArchitecture() { return architecture; }
    public void setArchitecture(String architecture) { this.architecture = architecture; }

    public String getBaseboard() { return baseboard; }
    public void setBaseboard(String baseboard) { this.baseboard = baseboard; }

    public String getBios() { return bios; }
    public void setBios(String bios) { this.bios = bios; }

    public List<Map<String,String>> getNetworkInterfaces() { return networkInterfaces; }
    public void setNetworkInterfaces(List<Map<String,String>> networkInterfaces) { this.networkInterfaces = networkInterfaces; }

    public String getEstimatedLifetime() { return estimatedLifetime; }
    public void setEstimatedLifetime(String estimatedLifetime) { this.estimatedLifetime = estimatedLifetime; }
}