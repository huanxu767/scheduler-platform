package com.sncfc.scheduler.server.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 2017/2/22.
 */
public interface ILoggerDao {

    /**
     * 查询日志总数
     * @param paramsMap
     * @return
     */
    Integer countScheduleLog(Map paramsMap);

    /**
     * 查询日志列表
     * @param curPage
     * @param pageSize
     * @param paramsMap
     * @return
     */
    List queryScheduleLogList(int curPage, int pageSize, Map paramsMap);
}
