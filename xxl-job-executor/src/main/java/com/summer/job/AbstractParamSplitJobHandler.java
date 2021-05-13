package com.summer.job;

import com.summer.job.exceptions.RetryException;
import com.summer.job.param.JobParams;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.IJobHandler;

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
    public abstract void runOneDay(JobParams jobParams) throws Exception;

    @Override
    public void execute() throws Exception {
        XxlJobHelper.log("当前任务执行类 beanName： {}", beanName);
        try {
            JobParams jobParams = new JobParams(XxlJobHelper.getJobParam());
            XxlJobHelper.log("任务[{}]启动", this.getClass().toGenericString());
            this.execute(this::runOneDay, jobParams);
            XxlJobHelper.log("任务[{}]结束", this.getClass().toGenericString());
        } finally {
            TaskData.destroy();
        }
    }

    interface Executor {
        void execute(JobParams jobParams) throws Exception;
    }

    /**
     * 执行单天或多天任务，格式eq:2019-07-20,10  或  2019-07-20,2019-07-30
     */
    private void execute(Executor executor, JobParams jobParams) throws Exception {
        while (jobParams.hasNextDate()) {
            try {
                TaskData.setCurrentDate(jobParams.nextDate());
                XxlJobHelper.log("----------------日期[{}]启动---------------------", jobParams.getCurrentDate());
                this.executorWithRetry(executor, jobParams);
                XxlJobHelper.log("----------------日期[{}]结束---------------------", jobParams.getCurrentDate());
                if (XxlJobHelper.getHandleResult() != XxlJobContext.HANDLE_COCE_SUCCESS) {
                    XxlJobHelper.log("{}数据处理失败", jobParams.getCurrentDate().toString());
                }
            } catch (Exception e) {
                if (jobParams.getErrorBreak()) {
                    throw e;
                } else {
                    XxlJobHelper.log(e);
                    XxlJobHelper.handleFail();
                }
            }
        }
    }

    private void executorWithRetry(Executor executor, JobParams jobParams) throws Exception {
        try {
            executor.execute(jobParams);
        } catch (RetryException e) {
            e.retry(() -> executor.execute(jobParams));
        }
    }
}
