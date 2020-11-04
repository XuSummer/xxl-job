package com.summer.job.param;

import com.tapque.framework.util.MathUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Desc
 * @Author Summer
 * @Date 2019/9/19 12:00
 */
public class ParamDateResolver implements IResolver<List<LocalDate>> {

    private List<LocalDate> dateArray = Collections.singletonList(LocalDate.now().minusDays(1));
    private int index = 0;
    private LocalDate currentDate;

    @Override
    public String getName() {
        return "date";
    }

    @Override
    public List<LocalDate> getValue() {
        return dateArray;
    }

    @Override
    public void resolve(String value) throws Exception {
        if (isSingle(value)) {
            dateArray = this.analyzeDateSingle(value);
        } else if (isSection(value)) {
            dateArray = this.analyzeDateBySection(value);
        } else if (isArray(value)) {
            dateArray = this.analyzeDateByArray(value);
        } else {
            throw new Exception("未识别的日期格式:" + value);
        }
    }

    public boolean hasNext() {
        return !dateArray.isEmpty() && dateArray.size() > index;
    }

    public LocalDate nextDate() {
        currentDate = dateArray.get(index++);
        return currentDate;
    }

    public LocalDate currentDate() {
        if (currentDate == null) {
            return dateArray.get(0);
        }
        return currentDate;
    }

    private boolean isArray(String n) {
        return n.startsWith("[") && n.endsWith("]");
    }

    private boolean isSection(String n) {
        return n.startsWith("{") && n.endsWith("}");
    }

    private boolean isSingle(String n) {
        return !isArray(n) && !isSection(n) && !n.contains(",");
    }

    private boolean isDateStr(String n) {
        try {
            LocalDate.parse(n);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private LocalDate parseDate(String item) throws Exception {
        if (MathUtil.isInteger(item)) {
            int i = Integer.parseInt(item);
            if (i > 0) {
                throw new Exception("数字日期不可大于0：" + i);
            }
            return LocalDate.now().minusDays(Math.abs(i));
        } else if (isDateStr(item)) {
            return LocalDate.parse(item);
        } else {
            throw new Exception("未识别的日期格式：" + item);
        }
    }

    private List<LocalDate> analyzeDateByArray(String n) throws Exception {
        String s = n.substring(1, n.length() - 1);
        String[] ab = s.split(",");
        List<LocalDate> dateArray = new ArrayList<>();
        for (String item : ab) {
            dateArray.add(this.parseDate(item));
        }
        return dateArray;
    }

    private List<LocalDate> analyzeDateBySection(String n) throws Exception {
        String s = n.substring(1, n.length() - 1);
        String[] ab = s.split(",");
        if (ab.length != 2) {
            throw new Exception("区间日期必须存在两个值");
        }
        List<LocalDate> dateArray = new ArrayList<>();
        LocalDate a = this.parseDate(ab[0]);
        LocalDate b = this.parseDate(ab[1]);
        while (a.compareTo(b) <= 0) {
            dateArray.add(a);
            a = a.plusDays(1);
        }
        return dateArray;
    }

    private List<LocalDate> analyzeDateSingle(String n) throws Exception {
        return Collections.singletonList(this.parseDate(n));
    }
}
