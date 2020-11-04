package com.summer.job.exceptions;

import com.xxl.job.core.biz.model.ReturnT;

public class RetryException extends Exception {

    private static final ThreadLocal<Integer> COUNT = new ThreadLocal<>();
    private Integer maxCount;

    private Integer second;

    public RetryException(Integer second) {
        this(second, 6);
    }

    public RetryException(Integer second, Integer maxCount) {
        this.second = second;
        this.maxCount = maxCount;
    }

    public Integer getSecond() {
        return second;
    }

    public void retry(RetryInterface retryInterface) throws Exception {
        if (COUNT.get() == null) {
            COUNT.set(0);
        }
        if (COUNT.get() >= maxCount - 1) {
            throw new Exception(String.format("重试次数超过最大数[%s]", maxCount));
        }
        try {
            COUNT.set(COUNT.get() + 1);
            if (second > 0) {
                try {
                    Thread.sleep(second * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            retryInterface.execute();
        } finally {
            COUNT.set(COUNT.get() - 1);
            if (COUNT.get() <= 0) {
                COUNT.remove();
            }
        }
    }

    public interface RetryInterface {
        void execute() throws Exception;
    }

    public interface RetryWithResultInterface<T> {
        ReturnT<T> execute() throws Exception;
    }

    public <T> ReturnT<T> retryWithResult(RetryWithResultInterface<T> retryInterface) throws Exception {
        if (COUNT.get() == null) {
            COUNT.set(0);
        }
        if (COUNT.get() >= maxCount - 1) {
            throw new Exception(String.format("重试次数超过最大数[%s]", maxCount));
        }
        try {
            COUNT.set(COUNT.get() + 1);
            if (second > 0) {
                try {
                    Thread.sleep(second * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return retryInterface.execute();
        } finally {
            COUNT.set(COUNT.get() - 1);
            if (COUNT.get() <= 0) {
                COUNT.remove();
            }
        }
    }
}
