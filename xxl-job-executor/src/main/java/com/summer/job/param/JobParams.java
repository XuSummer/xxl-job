package com.summer.job.param;

import com.summer.job.TaskData;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Desc
 * @Author Summer
 * @Date 2019/8/12 18:33
 */
public class JobParams {
    public static final String KEY_ACT_IDS = "actIds";

    private final ParamErrorBreakResolver errorBreakResolver = new ParamErrorBreakResolver();
    private final ParamDateResolver dateResolver = new ParamDateResolver();

    private final List<Long> actIds = new ArrayList<>();

    private Iterator<Long> actIterator;

    public JobParams(String params) throws Exception {
        if (StringUtils.hasText(params)) {
            params = params.trim();
            if (params.contains("：") || params.contains("；")) {
                throw new RuntimeException("参数分隔符请使用英文格式");
            }
            if (params.contains(":")) {
                String[] kvs;
                if (params.contains(";")) {
                    kvs = params.split(";");
                } else {
                    kvs = new String[]{params};
                }
                this.splitKvParams(kvs);
            } else {
                dateResolver.resolve(params);
            }
        }
    }

    private void splitKvParams(String[] kvs) throws Exception {
        String key, value;
        String[] kvArr;
        for (String kv : kvs) {
            key = kv.substring(0, kv.indexOf(":"));
            value = kv.substring(kv.indexOf(":") + 1);
            if (StringUtils.hasText(value)) {
                value = value.trim();

                if (KEY_ACT_IDS.equalsIgnoreCase(key)) {
                    this.splitActIds(value);
                } else if (dateResolver.getName().equalsIgnoreCase(key)) {
                    dateResolver.resolve(value);
                } else if (errorBreakResolver.getName().equalsIgnoreCase(key)) {
                    errorBreakResolver.resolve(value);
                } else {
                    TaskData.setData(key.toUpperCase(), value);
                }
            }
        }
    }

    public Boolean getErrorBreak() {
        return errorBreakResolver.getValue();
    }

    private void splitActIds(String actStr) {
        if (StringUtils.hasText(actStr)) {
            String[] actArrs = actStr.split(",");
            for (String act : actArrs) {
                this.actIds.add(Long.parseLong(act.trim()));
            }
        }
    }

    public boolean hasNextDate() {
        return dateResolver.hasNext();
    }

    public LocalDate getCurrentDate() {
        return dateResolver.currentDate();
    }

    public LocalDate nextDate() {
        return dateResolver.nextDate();
    }

    public boolean hasAct() {
        return !CollectionUtils.isEmpty(this.actIds);
    }

    private Long currentActId;

    public boolean hasNextActId() {
        if (hasAct()) {
            if (this.actIterator == null) {
                actIterator = this.actIds.iterator();
            }
            return actIterator.hasNext();
        }
        return false;
    }

    public Long nextActId() {
        if (hasNextActId()) {
            currentActId = actIterator.next();
            return currentActId;
        }
        return null;
    }

    public Long getCurrentActId() {
        return currentActId;
    }

}
