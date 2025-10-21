package com.pcadvisor.hardwareai.service;

import com.pcadvisor.hardwareai.model.HardwareInfo;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HardwareInfoService {

    public HardwareInfo getHardwareInfo() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        HardwareInfo info = new HardwareInfo();

        // CPU
        CentralProcessor cpu = hal.getProcessor();
        info.setCpuModel(cpu.getProcessorIdentifier() != null ? cpu.getProcessorIdentifier().getName() : "Desconocido");
        info.setCpuPhysicalCores(cpu.getPhysicalProcessorCount());
        info.setCpuLogicalCores(cpu.getLogicalProcessorCount());
        long[] prevTicks = cpu.getSystemCpuLoadTicks();
        try { Thread.sleep(800); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        double cpuLoad = cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100.0;
        info.setCpuLoadPercent(Math.round(cpuLoad * 10.0) / 10.0);

        // RAM
        GlobalMemory mem = hal.getMemory();
        double totalGb = Math.round((mem.getTotal() / (1024.0*1024*1024.0)) * 10.0) / 10.0;
        double availGb = Math.round((mem.getAvailable() / (1024.0*1024*1024.0)) * 10.0) / 10.0;
        info.setRamTotalGb(totalGb);
        info.setRamAvailableGb(availGb);

        // GPUs
        List<GraphicsCard> gpus = hal.getGraphicsCards();
        List<String> gpuNames = new ArrayList<>();
        if (gpus != null) {
            for (GraphicsCard g : gpus) gpuNames.add(g.getName());
        }
        info.setGpus(gpuNames);

        // Disks
        List<HWDiskStore> disks = hal.getDiskStores();
        List<Map<String,String>> diskList = new ArrayList<>();
        if (disks != null) {
            for (HWDiskStore d : disks) {
                Map<String,String> m = new HashMap<>();
                String model = d.getModel() != null ? d.getModel() : d.getName();
                m.put("model", model);
                double sizeGb = Math.round((d.getSize()/(1024.0*1024.0*1024.0))*10.0)/10.0;
                m.put("sizeGB", String.valueOf(sizeGb));
                m.put("serial", d.getSerial() != null ? d.getSerial() : "N/A");
                diskList.add(m);
            }
        }
        info.setDisks(diskList);

        // OS and board/bios
        info.setOs(os != null ? os.toString() : System.getProperty("os.name"));
        info.setArchitecture(System.getProperty("os.arch"));
        try {
            ComputerSystem cs = hal.getComputerSystem();
            info.setBaseboard(cs.getBaseboard() != null ? cs.getBaseboard().getManufacturer() + " " + cs.getBaseboard().getModel() : "Desconocido");
            info.setBios(cs.getFirmware() != null ? cs.getFirmware().getManufacturer() + " " + cs.getFirmware().getVersion() : "Desconocido");
        } catch (Exception e) {
            info.setBaseboard("Desconocido");
            info.setBios("Desconocido");
        }

        // Network
        List<NetworkIF> nets = hal.getNetworkIFs();
        List<Map<String,String>> netList = new ArrayList<>();
        if (nets != null) {
            for (NetworkIF n : nets) {
                Map<String,String> nm = new HashMap<>();
                nm.put("name", n.getName());
                nm.put("mac", n.getMacaddr());
                String ip = n.getIPv4addr().length > 0 ? n.getIPv4addr()[0] : "";
                nm.put("ip", ip);
                netList.add(nm);
            }
        }
        info.setNetworkInterfaces(netList);

        // Estimate lifetime
        info.setEstimatedLifetime(estimateLifetime(info));

        return info;
    }

    private String estimateLifetime(HardwareInfo info) {
        double ram = info.getRamTotalGb();
        int cores = info.getCpuPhysicalCores();
        if (ram <= 4 || cores <= 1) return "Menos de 1 año — actualizar recomendado";
        if (ram <= 8) return "1–2 años — soporte básico";
        if (ram <= 16) return "2–4 años — buen estado";
        return "4+ años — equipo moderno";
    }
}