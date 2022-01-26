package com.gr.uuid;


import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

/**
 * 雪花算法 自增id
 * @author liangc
 * @date 2020/8/18 9:29
 */
public class SnowFlakeIdWorker {

    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    //开始时间戳  2020-1-1. 时间戳位上并不存储实际的时间, 而是当前时间与开始时间的差值(秒)
    private final static long twepoch = LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.of("Z")).toEpochSecond();
    // 机器标识位数
    private final static long workerIdBits = 5L;
    // 数据中心标识位数
    private final static long datacenterIdBits = 5L;

    // 毫秒内自增位数
    private final static long sequenceBits = 12L;
    // 机器ID偏左移12位
    private final static long workerIdShift = sequenceBits;
    // 数据中心ID左移17位
    private final static long datacenterIdShift = sequenceBits + workerIdBits;
    // 时间毫秒左移22位
    private final static long DateLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    //sequence掩码，确保sequnce不会超出上限  生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
    private final static long sequenceMask = -1L ^ (-1L << sequenceBits);
    //上次时间戳
    private static long lastDate = -1L;
    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private static long workerMask = -1L ^ (-1L << workerIdBits);
    private static long processMask = -1L ^ (-1L << datacenterIdBits);
    private static SnowFlakeIdWorker snowFlake;

    static {
        snowFlake = new SnowFlakeIdWorker();
    }

    //序列
    private long sequence = 0L;
    //服务器ID
    private long workerId = 1L;
    //进程编码
    private long processId = 1L;
    private SnowFlakeIdWorker() {

        //获取机器编码
        this.workerId = this.getMachineNum();
        //获取进程编码
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        this.processId = Long.valueOf(runtimeMXBean.getName().split("@")[0]).longValue();

        //避免编码超出最大值
        this.workerId = workerId & workerMask;
        this.processId = processId & processMask;
    }

    public static synchronized String nextStringId() {
        //dateTimeFormatter.format(LocalDate.now()) + snowFlake.getNextId();
        return String.valueOf(snowFlake.getNextId());
    }

    public static synchronized long nextId() {
        return snowFlake.getNextId();
    }

    public synchronized long getNextId() {
        //获取时间戳
        long currentTimestamp = timeGen();
        //解决时钟回拨问题
        if (currentTimestamp < lastDate) {
            currentTimestamp = lastDate;
        }
        //如果时间戳与上次时间戳相同
        if (lastDate == currentTimestamp) {
            // 当前毫秒内，则+1，与sequenceMask确保sequence不会超出上限
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒SnowFlakeHolder
                currentTimestamp = tilNextMillis(lastDate);
            }
        } else {
            sequence = 0;
        }
        lastDate = currentTimestamp;
        // ID偏移组合生成最终的ID，并返回ID  移位并通过或运算拼到一起组成64位的ID
        long nextId = ((currentTimestamp - twepoch) << DateLeftShift)
                | (processId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
        return nextId;
    }

    /**
     * 再次获取时间戳直到获取的时间戳与现有的不同
     *
     * @param lastDate
     * @return 下一个时间戳
     */
    private long tilNextMillis(final long lastDate) {
        long Date = this.timeGen();
        while (Date <= lastDate) {
            Date = this.timeGen();
        }
        return Date;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 获取机器编码
     *
     * @return
     */
    private long getMachineNum() {
        long machinePiece;
        StringBuilder sb = new StringBuilder();
        Enumeration<NetworkInterface> e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        while (e.hasMoreElements()) {
            NetworkInterface ni = e.nextElement();
            sb.append(ni.toString());
        }
        machinePiece = sb.toString().hashCode();
        return machinePiece;
    }

}
