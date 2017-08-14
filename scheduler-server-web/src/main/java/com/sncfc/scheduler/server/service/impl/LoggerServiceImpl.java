package com.sncfc.scheduler.server.service.impl;

import com.sncfc.scheduler.server.dao.ILoggerDao;
import com.sncfc.scheduler.server.pojo.Pager;
import com.sncfc.scheduler.server.service.ILoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 2017/2/22.
 */
@Service
public class LoggerServiceImpl implements ILoggerService{

    @Autowired
    private ILoggerDao loggerDao;

    @Override
    public Pager queryJobList(Map paramsMap) {
        Pager pager = new Pager();
        if(!StringUtils.isEmpty(paramsMap.get("pageSize"))){
            pager.setPageSize(Integer.parseInt(paramsMap.get("pageSize").toString()));
        }
        if(!StringUtils.isEmpty(paramsMap.get("curPage"))){
            pager.setCurPage(Integer.parseInt(paramsMap.get("curPage").toString()));
        }
        Integer counts = loggerDao.countScheduleLog(paramsMap);
        List list = loggerDao.queryScheduleLogList(pager.getCurPage(),pager.getPageSize(),paramsMap);
        pager.setTotalRow(counts);
        pager.setQueryList(list);
        return pager;
    }
}
