package com.sncfc.scheduler.server.dao.impl;

import com.sncfc.scheduler.server.dao.IScheduleJobDao;
import com.sncfc.scheduler.server.pojo.Project;
import com.sncfc.scheduler.server.pojo.ScheduleJob;
import com.sncfc.scheduler.server.pojo.ScheduleJobParams;
import com.sncfc.scheduler.server.pojo.ScheduleLog;
import com.sncfc.scheduler.server.util.BaseJdbcDAO;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by xuhuan on 2017/1/19.
 */
@Repository(value = "scheduleJobDao")
public class ScheduleJobDaoImpl extends BaseJdbcDAO implements IScheduleJobDao {

    @Override
    public Long insertScheduleJob(final ScheduleJob job) {
        String seqSql = "SELECT SEQ_SCHEDULE_JOB.NEXTVAL FROM DUAL";
        Long id = queryForObject(seqSql, Long.class);
        String sql = "INSERT INTO SCHEDULE_JOB(SCHEDULE_JOB_ID, PROJECT_ID, JOB_ALIAS_NAME,CRON_EXPRESSION,SYNC,URL, JOB_TYPE,JOB_DESC,CLASS_NAME,METHOD_NAME,CREATE_TIME) VALUES(?,?, ?, ?, ?,?, ?, ?,?,?, SYSDATE)";
        update(sql, new Object[]{id, job.getProjectId(), job.getJobAliasName(), job.getCronExpression(), job.getSync(), job.getUrl(), job.getJobType(), job.getJobDesc(), job.getClassName(), job.getMethodName()});
        return id;
    }

    @Override
    public void insertScheduleJobParams(final Long id, final List<ScheduleJobParams> paramsList) {
        if (paramsList == null || paramsList.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO SCHEDULE_JOB_PARAMS(SCHEDULE_JOB_ID,VALUE) VALUES(?,?)";
        batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                int no = 1;
                ScheduleJobParams jobParams = paramsList.get(i);
                preparedStatement.setLong(no++, id);
                preparedStatement.setString(no++, jobParams.getValue());
            }
            @Override
            public int getBatchSize() {
                return paramsList.size();
            }
        });
    }

    @Override
    public ScheduleJob queryScheduleJob(Long scheduleJobId) {
        String sql = "SELECT * FROM SCHEDULE_JOB WHERE SCHEDULE_JOB_ID = ?";
        ScheduleJob job = queryForObject(sql, new Object[]{scheduleJobId}, new JobMapper());
        return job;
    }

    @Override
    public int updateJobStatus(Long scheduleJobId, int status) {
        String sql = "UPDATE SCHEDULE_JOB SET STATUS = ? WHERE SCHEDULE_JOB_ID = ?";
        return update(sql, new Object[]{status, scheduleJobId});
    }

    @Override
    public int updateJob(ScheduleJob scheduleJob) {
        String sql = "UPDATE SCHEDULE_JOB SET PROJECT_ID = ?, JOB_ALIAS_NAME = ?,CRON_EXPRESSION = ?,URL = ?, JOB_TYPE = ?,JOB_DESC = ?,CLASS_NAME = ?,METHOD_NAME = ?,UPDATE_TIME=sysdate WHERE SCHEDULE_JOB_ID = ?";
        return update(sql, new Object[]{scheduleJob.getProjectId(), scheduleJob.getJobAliasName(),
                scheduleJob.getCronExpression(), scheduleJob.getUrl(), scheduleJob.getJobType(),
                scheduleJob.getJobDesc(), scheduleJob.getClassName(), scheduleJob.getMethodName(), scheduleJob.getScheduleJobId()});
    }

    @Override
    public int insertScheduleLog(ScheduleLog scheduleLog) {
        final String sql = "INSERT INTO schedule_log" +
                " (JOB_NAME,JOB_GROUP,FIRE_INSTANCE_ID,STATUS,SUCCESS,NODE_NAME,ERROR_MESSAGE,TRIGGER_TYPE,CREATE_TIME) " +
                " VALUES(?, ?, ?, ?, ?, ?, ?,?,SYSDATE)";
        return update(sql, new Object[]{scheduleLog.getJobName(), scheduleLog.getJobGroup(), scheduleLog.getFireInstanceId(),scheduleLog.getStatus(), scheduleLog.getSuccess(), scheduleLog.getNodeName(),scheduleLog.getErrorMessage(),scheduleLog.getTriggerType()});
    }

    @Override
    public int existedScheduleLog(String nodeName, String fireInstanceId) {
        final String sql = "SELECT COUNT(*) COUNTS FROM SCHEDULE_LOG WHERE NODE_NAME = ? AND FIRE_INSTANCE_ID = ?";
        Integer counts = queryForObject(sql, new Object[]{nodeName, fireInstanceId}, Integer.class);
        return counts;
    }

    @Override
    public void updateLogStatus(String fireInstanceId, boolean executed,String errorMsg) {
        String sql = "UPDATE SCHEDULE_LOG SET STATUS = ?  WHERE FIRE_INSTANCE_ID = ? ";
        String completeSql = "UPDATE SCHEDULE_LOG SET  SUCCESS = ?,  STATUS = ?,ERROR_MESSAGE = ? ,END_TIME = SYSDATE  WHERE FIRE_INSTANCE_ID = ?";
        if (executed) {
            //任务完成
            if(StringUtils.isEmpty(errorMsg)){
                update(completeSql, new Object[]{ScheduleLog.JOB_SUCCESS,ScheduleLog.CLIENT_END, "",fireInstanceId});
            }else{
                update(completeSql, new Object[]{ScheduleLog.JOB_FAILURE,ScheduleLog.CLIENT_END, errorMsg,fireInstanceId});
            }
        } else {
            //任务未完成
            update(sql, new Object[]{ScheduleLog.CLIENT_DOING, fireInstanceId});
        }
    }

    @Override
    public List<String> queryUnCompleteJob(Set<String> nodeSet) {
        String sql = "select fire_instance_id from schedule_log where status in(1,2) and node_name in (?)";
        Set<String> s = new HashSet<String>();
        for (String str : nodeSet) {
            s.add("'"+str+"'");
        }
        String names = StringUtils.collectionToCommaDelimitedString(s);
        List<String> resultList = (List<String>) getJdbcTemplate().query(sql, new Object[]{names}, new StringMapper());
        return resultList;
    }

    @Override
    public Integer countScheduleJob(Map paramsMap) {
        StringBuffer sql = new StringBuffer();
        List params = new ArrayList();
        sql.append("SELECT COUNT(*) FROM SCHEDULE_JOB WHERE STATUS <> 0  ");
        if(!StringUtils.isEmpty(paramsMap.get("projectId"))){
            sql.append("AND PROJECT_ID = ?");
            params.add(paramsMap.get("projectId"));
        }
        if(!StringUtils.isEmpty(paramsMap.get("jobAliasName"))){
            sql.append("AND JOB_ALIAS_NAME like ?");
            params.add("%" + paramsMap.get("jobAliasName") + "%");
        }
        return queryForObject(sql.toString(),params.toArray(), Integer.class);
    }

    @Override
    public List queryScheduleJobList(int curPage, int pageSize, Map paramsMap) {
        StringBuffer sql = new StringBuffer();
        List params = new ArrayList();
        sql.append(" SELECT S.SCHEDULE_JOB_ID,S.PROJECT_ID,S.JOB_ALIAS_NAME,S.CRON_EXPRESSION,S.SYNC,S.URL,S.JOB_TYPE,");
        sql.append(" TO_CHAR(S.CREATE_TIME,'YYYY/MM/DD HH:MM') CREATE_TIME,S.STATUS,S.JOB_DESC,S.CLASS_NAME,S.METHOD_NAME,P.PROJECT_NAME");
        sql.append(" FROM SCHEDULE_JOB S  ");
        sql.append(" LEFT JOIN SCHEDULE_PROJECT P ON S.PROJECT_ID = P.PROJECT_ID WHERE S.STATUS <> 0  ");
        if(!StringUtils.isEmpty(paramsMap.get("projectId"))){
            sql.append("AND S.PROJECT_ID = ?");
            params.add(paramsMap.get("projectId"));
        }
        if(!StringUtils.isEmpty(paramsMap.get("jobAliasName"))){
            sql.append("AND S.JOB_ALIAS_NAME like ?");
            params.add("%" + paramsMap.get("jobAliasName") + "%");
        }
        sql.append(" ORDER BY S.SCHEDULE_JOB_ID DESC");
        String desSql = getLimitSql(sql.toString(),curPage,pageSize);
        return queryForList(desSql,params.toArray());
    }

    @Override
    public List<ScheduleJobParams> queryScheduleJobParams(long scheduleJobId) {
        String sql = "SELECT * FROM SCHEDULE_JOB_PARAMS WHERE SCHEDULE_JOB_ID = ?";
        List params = getJdbcTemplate().query(sql, new Object[]{scheduleJobId}, new JobParamsMapper());
        return params;
    }

    @Override
    public List<Project> queryProjectList() {
        String sql = "SELECT * FROM SCHEDULE_PROJECT ";
        List params = getJdbcTemplate().query(sql,new ProjectMapper());
        return params;
    }

    @Override
    public int deleteScheduleJobParams(Long scheduleJobId) {
        String sql = "DELETE  FROM SCHEDULE_JOB_PARAMS WHERE SCHEDULE_JOB_ID = ?";
        return update(sql, new Object[]{scheduleJobId});
    }

    @Override
    public List countTriggersList(int days) {
        String sql = "SELECT * FROM ( SELECT CREATE_DAY ,COUNT(*) COUNTS" +
                "    FROM (SELECT TO_CHAR(CREATE_TIME,'YYYY-MM-DD') CREATE_DAY" +
                "    FROM SCHEDULE_LOG ) GROUP BY CREATE_DAY ORDER BY CREATE_DAY DESC) WHERE ROWNUM <= ?";
        return queryForList(sql,new Object[]{days});
    }

    @Override
    public List countJobs() {
        String sql = "SELECT STATUS,COUNT(*) NUMS FROM SCHEDULE_JOB WHERE STATUS <> 0 GROUP BY STATUS ";
        return queryForList(sql);
    }

    @Override
    public List countAllTriggers() {
        String sql = "SELECT SUCCESS,COUNT(*) SUCESS_NUMS FROM SCHEDULE_LOG GROUP BY SUCCESS";
        return queryForList(sql);
    }

    @Override
    public int updateScheduleLog(ScheduleLog scheduleLog) {
        String sql = "UPDATE SCHEDULE_LOG SET STATUS = ?,SUCCESS = ?,NODE_NAME = ?,ERROR_MESSAGE = ?  WHERE FIRE_INSTANCE_ID = ? ";
        return update(sql,new Object[]{scheduleLog.getStatus(),scheduleLog.getSuccess(),scheduleLog.getNodeName(),scheduleLog.getErrorMessage(),scheduleLog.getFireInstanceId()});
    }

    @Override
    public int existedScheduleLog(String fireInstanceId) {
        final String sql = "SELECT COUNT(*) COUNTS FROM SCHEDULE_LOG WHERE FIRE_INSTANCE_ID = ?";
        Integer counts = queryForObject(sql, new Object[]{fireInstanceId}, Integer.class);
        return counts;
    }

    @Override
    public Map countTriggers(int days) {
        String sql = "SELECT SUM(TOTALS) COUNTS,SUM(SUCESS_COUNTS) SUCESS_COUNTS,SUM(FAILURE_COUNTS) FAILURE_COUNTS" +
                "    FROM ( SELECT CREATE_DAY,SUM(SUCESS_NUM) SUCESS_COUNTS,SUM(FAILURE_COUNTS) FAILURE_COUNTS,COUNT(*) TOTALS" +
                "    FROM (SELECT TO_CHAR(CREATE_TIME,'YYYY-MM-DD') CREATE_DAY,(CASE SUCCESS WHEN '3' THEN 1 ELSE 0 END ) SUCESS_NUM ,(CASE SUCCESS WHEN '2' THEN 1  WHEN '4' THEN 1 ELSE 0 END)  FAILURE_COUNTS FROM SCHEDULE_LOG )" +
                " GROUP BY CREATE_DAY ORDER BY CREATE_DAY DESC ) WHERE ROWNUM <= ?";
        return queryForMap(sql,new Object[]{days});
    }


    class StringMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet resultSet, int i) throws SQLException {
            return resultSet.getString("FIRE_INSTANCE_ID");
        }
    }

    class ScheduleLogMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet resultSet, int i) throws SQLException {
            ScheduleLog scheduleLog = new ScheduleLog();
            scheduleLog.setJobName(resultSet.getString("JOB_NAME"));
            scheduleLog.setJobGroup(resultSet.getString("JOB_GROUP"));
            scheduleLog.setFireInstanceId(resultSet.getString("FIRE_INSTANCE_ID"));
            scheduleLog.setStatus(resultSet.getString("STATUS"));
            scheduleLog.setSuccess(resultSet.getString("SUCCESS"));
            scheduleLog.setNodeName(resultSet.getString("NODE_NAME"));
            scheduleLog.setErrorMessage(resultSet.getString("ERROR_MESSAGE"));
            scheduleLog.setCreateTime(resultSet.getDate("CREATE_TIME"));
            scheduleLog.setEndTime(resultSet.getDate("END_TIME"));
            return scheduleLog;
        }
    }

    class JobMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet resultSet, int i) throws SQLException {
            ScheduleJob job = new ScheduleJob();
            job.setScheduleJobId(resultSet.getLong("SCHEDULE_JOB_ID"));
            job.setProjectId(resultSet.getLong("PROJECT_ID"));
            job.setJobAliasName(resultSet.getString("JOB_ALIAS_NAME"));
            job.setCronExpression(resultSet.getString("CRON_EXPRESSION"));
            job.setSync(resultSet.getString("SYNC"));
            job.setUrl(resultSet.getString("URL"));
            job.setJobType(resultSet.getString("JOB_TYPE"));
            job.setCreateTime(resultSet.getDate("CREATE_TIME"));
            job.setUpdateTime(resultSet.getDate("UPDATE_TIME"));
            job.setStatus(resultSet.getInt("STATUS"));
            job.setJobDesc(resultSet.getString("JOB_DESC"));
            job.setClassName(resultSet.getString("CLASS_NAME"));
            job.setMethodName(resultSet.getString("METHOD_NAME"));
            return job;
        }
    }
    class JobParamsMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet resultSet, int i) throws SQLException {
            ScheduleJobParams params = new ScheduleJobParams();
            params.setScheduleJobId(resultSet.getLong("SCHEDULE_JOB_ID"));
            params.setValue(resultSet.getString("VALUE"));
            return params;
        }
    }

    class ProjectMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet resultSet, int i) throws SQLException {
            Project params = new Project();
            params.setId(resultSet.getString("PROJECT_ID"));
            params.setName(resultSet.getString("PROJECT_NAME"));
            return params;
        }
    }
}
