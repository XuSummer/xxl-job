package com.summer.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.summer.job.exceptions.RetryException;
import com.summer.job.exceptions.SkipCurrentDateException;
import com.summer.job.param.JobParams;
import com.xxl.job.core.context.XxlJobHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDate;

/**
 * @Desc 基础数据获取抽象逻辑
 * @Author Summer
 * @Date 2019/8/1 10:49
 */
public abstract class AbstractSimpleJobHandler extends AbstractParamSplitJobHandler {

    @Autowired
    protected ObjectMapper mapper;

    protected static final ThreadLocal<LocalDate> CURRENT_DATE = new ThreadLocal<>();

    private void runWrap() throws Exception {
        try {
            this.run();
        } catch (SkipCurrentDateException e) {
            XxlJobHelper.log(e.getMessage());
            XxlJobHelper.handleFail();
        } catch (RetryException e) {
            XxlJobHelper.log(e);
            e.retry(this::runWrap);
        } catch (Exception e) {
            if (e instanceof HttpStatusCodeException) {
                HttpStatusCodeException se = (HttpStatusCodeException) e;
                XxlJobHelper.log("statusCode: {}, responseBody: {}", se.getStatusCode(), se.getResponseBodyAsString());
                if (se.getStatusCode().value() >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    // 服务商错误则需要重试
                    new RetryException(10).retry(this::runWrap);
                }
            } else if (e instanceof ResourceAccessException) {
                XxlJobHelper.log(e);
                new RetryException(10).retry(this::runWrap);
            }
            // 未知错误直接终止任务
            throw e;
        }
    }

    /**
     * 执行核心
     */
    protected abstract void run() throws Exception;

    /**
     * 根据渠道信息查询该渠道下所有关联数据
     */
    @Override
    public void runOneDay(JobParams jobParams) throws Exception {
        try {
            CURRENT_DATE.set(jobParams.getCurrentDate());
            this.runWrap();
        } finally {
            CURRENT_DATE.remove();
        }
    }
}
