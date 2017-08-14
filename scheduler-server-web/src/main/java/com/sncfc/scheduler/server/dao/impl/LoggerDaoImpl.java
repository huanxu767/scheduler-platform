package com.sncfc.scheduler.server.dao.impl;

import com.sncfc.scheduler.server.dao.ILoggerDao;
import com.sncfc.scheduler.server.util.BaseJdbcDAO;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuhuan on 2017/1/19.
 */
@Repository(value = "loggerDao")
public class LoggerDaoImpl extends BaseJdbcDAO implements ILoggerDao {


    @Override
    public Integer countScheduleLog(Map paramsMap) {
        StringBuffer sql = new StringBuffer();
        List params = new ArrayList();
        sql.append(" SELECT COUNT(*) ");
        sql.append(" FROM SCHEDULE_LOG S,SCHEDULE_PROJECT P ,SCHEDULE_JOB B  ");
        sql.append(" WHERE S.JOB_GROUP = P.PROJECT_ID AND S.JOB_NAME = B.SCHEDULE_JOB_ID AND B.STATUS <> 0 ");
        if(!StringUtils.isEmpty(paramsMap.get("projectId"))){
            sql.append("AND S.JOB_GROUP = ?");
            params.add(paramsMap.get("projectId"));
        }
        if(!StringUtils.isEmpty(paramsMap.get("jobAliasName"))){
            sql.append("AND B.JOB_ALIAS_NAME like ?");
            params.add("%" + paramsMap.get("jobAliasName") + "%");
        }
        return queryForObject(sql.toString(),params.toArray(), Integer.class);
    }

    @Override
    public List queryScheduleLogList(int curPage, int pageSize, Map paramsMap) {
        StringBuffer sql = new StringBuffer();
        List params = new ArrayList();
        sql.append(" SELECT S.JOB_NAME,S.JOB_GROUP,S.SUCCESS,S.STATUS,S.NODE_NAME,S.ERROR_MESSAGE,");
        sql.append(" TO_CHAR(S.CREATE_TIME,'YYYY-MM-DD HH24:MI:SS') CREATE_TIME,TO_CHAR(S.END_TIME,'YYYY-MM-DD HH24:MI:SS') END_TIME,P.PROJECT_NAME,B.JOB_ALIAS_NAME");
        sql.append(" FROM SCHEDULE_LOG S,SCHEDULE_PROJECT P ,SCHEDULE_JOB B  ");
        sql.append(" WHERE S.JOB_GROUP = P.PROJECT_ID AND S.JOB_NAME = B.SCHEDULE_JOB_ID AND B.STATUS <> 0 ");
        if(!StringUtils.isEmpty(paramsMap.get("projectId"))){
            sql.append("AND S.JOB_GROUP = ?");
            params.add(paramsMap.get("projectId"));
        }
        if(!StringUtils.isEmpty(paramsMap.get("jobAliasName"))){
            sql.append("AND B.JOB_ALIAS_NAME like ?");
            params.add("%" + paramsMap.get("jobAliasName") + "%");
        }
        sql.append(" ORDER BY S.CREATE_TIME DESC");
        String desSql = getLimitSql(sql.toString(),curPage,pageSize);
        return queryForList(desSql,params.toArray());
    }
}
