package com.summer.job.param;

import org.springframework.util.StringUtils;

/**
 * @Desc
 * @Author Summer
 * @Date 2019/9/19 12:00
 */
public class ParamErrorBreakResolver implements IResolver<Boolean> {

    private boolean errorBreak = true;

    @Override
    public String getName() {
        return "errorBreak";
    }

    @Override
    public Boolean getValue() {
        return errorBreak;
    }

    @Override
    public void resolve(String value) {
        errorBreak = StringUtils.hasText(value) && "false".equalsIgnoreCase(value);
    }
}
