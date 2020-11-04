package com.summer.job;

import com.summer.job.TaskData;
import com.summer.job.exceptions.RetryException;
import com.summer.job.param.JobParams;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.BeanNameAware;

/**
 * @Desc 分隔时间抽象逻辑
 * @Author Summer
 * @Date 2019/7/31 17:41
 */
public abstract class AbstractParamSplitJobHandler extends IJobHandler implements BeanNameAware {

    protected String beanName;

    @Override
    public void setBeanName(String s) {
        this.beanName = s;
    }

    /**
     * 执行逻辑
     */
    public abstract ReturnT<String> runOneDay(JobParams jobParams) throws Exception;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("当前任务执行类 beanName： {}", beanName);
        try {
            JobParams jobParams = new JobParams(param);
            XxlJobLogger.log("任务[{}]启动", this.getClass().toGenericString());
            ReturnT<String> result = this.execute(this::runOneDay, jobParams);
            XxlJobLogger.log("任务[{}]结束", this.getClass().toGenericString());
            return result;
        } finally {
            TaskData.destroy();
        }
    }

    interface Executor {
        ReturnT<String> execute(JobParams jobParams) throws Exception;
    }

    /**
     * 执行单天或多天任务，格式eq:2019-07-20,10  或  2019-07-20,2019-07-30
     */
    private ReturnT<String> execute(Executor executor, JobParams jobParams) throws Exception {
        ReturnT<String> returnT = SUCCESS;
        while (jobParams.hasNextDate()) {
            try {
                TaskData.setCurrentDate(jobParams.nextDate());
                XxlJobLogger.log("----------------日期[{}]启动---------------------", jobParams.getCurrentDate());
                returnT = this.executorWithRetry(executor, jobParams);
                XxlJobLogger.log("----------------日期[{}]结束---------------------", jobParams.getCurrentDate());
                if (returnT != null && SUCCESS.getCode() != returnT.getCode()) {
                    XxlJobLogger.log("{}数据处理失败", jobParams.getCurrentDate().toString());
                }
            } catch (Exception e) {
                if (jobParams.getErrorBreak()) {
                    throw e;
                } else {
                    XxlJobLogger.log(e);
                }
            }
        }
        return returnT;
    }

    private ReturnT<String> executorWithRetry(Executor executor, JobParams jobParams) throws Exception {
        try {
            return executor.execute(jobParams);
        } catch (RetryException e) {
            return e.retryWithResult(() -> executor.execute(jobParams));
        }
    }
}
