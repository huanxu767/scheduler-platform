package com.sncfc.scheduler.server.service.impl;

import com.sncfc.scheduler.server.dao.ITestDao;
import com.sncfc.scheduler.server.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 123 on 2017/2/9.
 */
@Service
@Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
public class TestServiceImpl implements ITestService {

    @Autowired
    private ITestDao testDao;

    @Override
    public int updateTestTransaction(){
        testDao.updateTest();
        testDao.addTest();
        return 0;
    }

    @Override
    @Transactional(propagation= Propagation.NOT_SUPPORTED)
    public int updateTest(){
        testDao.updateTest();
        return testDao.addTest();
    }
}
