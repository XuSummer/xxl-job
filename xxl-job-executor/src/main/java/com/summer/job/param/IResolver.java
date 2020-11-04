package com.summer.job.param;

/**
 * @Desc
 * @Author Summer
 * @Date 2019/9/19 12:01
 */
public interface IResolver<T> {
    String getName();

    T getValue();

    void resolve(String value) throws Exception;
}
