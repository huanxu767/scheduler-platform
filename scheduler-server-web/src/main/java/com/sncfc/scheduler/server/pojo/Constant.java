package com.sncfc.scheduler.server.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 2017/1/20.
 */
public class Constant {
    public static final String JOB_TYPE_STATIC_METHOD = "1";
    public static final String JOB_TYPE_SPRING_BEAN = "2";

    /**
     * 自动触发任务
     */
    public static final String JOB_TRIGGER_AUTO = "1";
    /**
     * 用户单次触发任务
     */
    public static final String JOB_TRIGGER_USER = "1";

    public static Map<String,Long> JOB_MAP = new HashMap();


}
