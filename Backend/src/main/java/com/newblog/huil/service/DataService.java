package com.newblog.huil.service;

import java.util.Date;

/**
 * @author HuilLIN
 */
public interface DataService {
    /**
     * 将指定的IP计入UV
     * @param ip
     */
    public void recordUV(String ip);

    /**
     * 统计指定日期范围内的UV
     * @param start
     * @param end
     * @return
     */
    public long calculateUV(Date start,Date end);

    /**
     * 将指定用户计入DAU
     * @param userId
     */
    public void recordDAU(int userId);

    /**
     * 统计指定日期范围内的DAU
     * @param start
     * @param end
     * @return
     */
    public long calculateDAU(Date start,Date end);
}
