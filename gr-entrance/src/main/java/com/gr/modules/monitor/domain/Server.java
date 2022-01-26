package com.gr.modules.monitor.domain;

import com.gr.constant.Arith;
import com.gr.ip.IPUtils;
import com.gr.modules.monitor.entity.*;
import lombok.Data;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * 服务器相关信息
 * @Author: liangc
 * @Date: 2020/4/24 11:19
 *
 */
@Data
public class Server {

    private static final int OSHI_WAIT_SECOND = 1000;

    /**
     * 服务器相关信息
     */
    private SystemEntity systemEntity = new SystemEntity();

    /**
     * CPU相关信息
     */
    private CpuEntity cpuEntity = new CpuEntity();

    /**
     * 內存相关信息
     */
    private MemEntity memEntity = new MemEntity();

    /**
     * JVM相关信息
     */
    private JvmEntity jvmEntity = new JvmEntity();

    /**
     * 磁盘相关信息
     */
    private List<SystemFileEntity> sysFileEntities = new LinkedList<>();


    public void copyTo() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        setCpuInfo(hal.getProcessor());

        setMemInfo(hal.getMemory());

        setSysInfo();

        setJvmInfo();

        setSysFiles(si.getOperatingSystem());
    }

    /**
     * 设置CPU信息
     */
    private void setCpuInfo(CentralProcessor processor) {
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long cSys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        cpuEntity.setCpuNum(processor.getLogicalProcessorCount());
        cpuEntity.setTotal(totalCpu);
        cpuEntity.setSys(cSys);
        cpuEntity.setUsed(user);
        cpuEntity.setWait(iowait);
        cpuEntity.setFree(idle);
    }

    /**
     * 设置内存信息
     */
    private void setMemInfo(GlobalMemory memory) {
        memEntity.setTotal(memory.getTotal());
        memEntity.setUsed(memory.getTotal() - memory.getAvailable());
        memEntity.setFree(memory.getAvailable());
    }

    /**
     * 设置服务器信息
     */
    private void setSysInfo() {
        Properties props = System.getProperties();
        systemEntity.setOsName(props.getProperty("os.name"));
        systemEntity.setOsArch(props.getProperty("os.arch"));
        systemEntity.setUserDir(props.getProperty("user.dir"));
        systemEntity.setComputerName(IPUtils.getHostName());
        systemEntity.setComputerIp(IPUtils.getHostIp());
    }

    /**
     * 设置Java虚拟机
     */
    private void setJvmInfo(){
        Properties props = System.getProperties();
        jvmEntity.setTotal(Runtime.getRuntime().totalMemory());
        jvmEntity.setMax(Runtime.getRuntime().maxMemory());
        jvmEntity.setFree(Runtime.getRuntime().freeMemory());
        jvmEntity.setVersion(props.getProperty("java.version"));
        jvmEntity.setHome(props.getProperty("java.home"));
    }

    /**
     * 设置磁盘信息
     */
    private void setSysFiles(OperatingSystem os) {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            SystemFileEntity systemFileEntity = new SystemFileEntity();
            systemFileEntity.setDirName(fs.getMount());
            systemFileEntity.setSysTypeName(fs.getType());
            systemFileEntity.setTypeName(fs.getName());
            systemFileEntity.setTotal(convertFileSize(total));
            systemFileEntity.setFree(convertFileSize(free));
            systemFileEntity.setUsed(convertFileSize(used));
            systemFileEntity.setUsage(Arith.mul(Arith.div(used, total, 4), 100));
            sysFileEntities.add(systemFileEntity);
        }
    }

    /**
     * 字节转换
     *
     * @param size 字节大小
     * @return 转换后值
     */
    public String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }
}
