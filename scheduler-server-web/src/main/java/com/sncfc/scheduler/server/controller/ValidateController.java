package com.sncfc.scheduler.server.controller;

import com.sncfc.scheduler.server.service.IScheduleJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

/**
 * 客户端验证控制
 */
@Controller
public class ValidateController {

    private static final Logger logger = LoggerFactory.getLogger(ValidateController.class);

    private static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";

    @Autowired
    private IScheduleJobService scheduleJobService;
    /**
     * 任务页面
     *
     * @return
     */

    @RequestMapping(value = "jobCmdValidate")
    public void jobCmdValidate(HttpServletRequest requset,HttpServletResponse response) {
        boolean resultFlag = false;
        try {
            Map map = (Map)readRequest(requset);
            if(map.containsKey("nodeName") && map.containsKey("fireInstanceId")){
                logger.info("jobCmdValidate time");
                resultFlag = scheduleJobService.existedScheduleLog(map.get("fireInstanceId").toString());
            }
            logger.info("jobCmdValidate:" + map.toString() + " resultFlag = " + resultFlag);
        } catch (Exception e) {
            logger.error("jobCmdValidate",e);
        }finally {
            try {
                writeResponse(response,resultFlag);
            } catch (IOException e) {
                logger.error("jobCmdValidate",e);
            }
        }
    }
    @RequestMapping(value = "jobExecutionReport")
    public void jobExecutionReport(HttpServletRequest requset, HttpServletResponse response) {
        List<String> unCompleteList = null;
        try {
            List requestList = (List)readRequest(requset);
            unCompleteList = scheduleJobService.updateScheduleLog(requestList);
        } catch (IOException e) {
            logger.error("jobExecutionReport",e);
        }finally {
            try {
                writeResponse(response,unCompleteList);
            } catch (IOException e) {
                logger.error("jobCmdValidate",e);
            }
        }
    }

    private void writeResponse(HttpServletResponse response, Object result) throws IOException {
        response.setContentType(CONTENT_TYPE_SERIALIZED_OBJECT);
        ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
        try {
            oos.writeObject(result);
        } finally {
            oos.close();
        }
    }
    private Object readRequest(HttpServletRequest request) throws IOException {
        InputStream is = request.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        try {
            Object obj = ois.readObject();
            return obj;
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        } finally {
            ois.close();
        }
    }

}
