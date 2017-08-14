package com.sncfc.scheduler.server.service;

import com.sncfc.scheduler.server.pojo.Pager;

import java.util.Map;

/**
 * Created by 123 on 2017/2/22.
 */
public interface ILoggerService {
    /**
     * 查询任务列表 分页
     * @param paramsMap
     * @return
     */
    Pager queryJobList(Map paramsMap);
}
