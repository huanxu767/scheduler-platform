package com.sncfc.scheduler.server.dao.impl;

import com.sncfc.scheduler.server.dao.ITestDao;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Created by 123 on 2017/2/9.
 */
@Repository
public class TestDaoImpl extends JdbcDaoSupport implements ITestDao {

    @Resource(name = "dataSource")
    public void setSuperDataSource(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    public int addTest() {
        String name = "wori111111111111111";
        Long money = 5L;
        return this.getJdbcTemplate().update("insert into s_test values(?,?)", new Object[]{name, money});
    }

    @Override
    public int updateTest() {
        String name = "许欢";
        Long money = 50L;
        return this.getJdbcTemplate().update("update s_test set money = ? where name = ? ", new Object[]{money, name});
    }
}
