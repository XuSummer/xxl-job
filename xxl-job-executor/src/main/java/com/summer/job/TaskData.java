package com.summer.job;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @Desc 任务基础数据
 * @Author Summer
 * @Date 2019/9/20 16:38
 */
public class TaskData {
    // 线程关联数据
    private static final ThreadLocal<Map<String, Object>> CACHE = new ThreadLocal<>();

    private static final String KEY_CURRENT_DATE = "CURRENT_DATE";

    protected static Map<String, Object> getContent() {
        Map<String, Object> content = CACHE.get();
        if (content == null) {
            content = new HashMap<>();
            CACHE.set(content);
        }
        return content;
    }

    /**
     * 获取当前时间
     */
    public static LocalDate getCurrentDate() {
        return (LocalDate) getContent().get(KEY_CURRENT_DATE);
    }

    static void setCurrentDate(LocalDate currentDate) {
        getContent().put(KEY_CURRENT_DATE, currentDate);
    }

    static void destroy() {
        CACHE.remove();
    }

    /**
     * 为线程提供存储线程相关数据暂存接口
     */
    public static Object getData(String key) {
        return getContent().get(key);
    }

    public static void setData(String key, Object value) {
        getContent().put(key, value);
    }
}
