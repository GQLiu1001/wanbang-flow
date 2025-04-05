package com.wanbang.console.util;

import java.util.ArrayList;
import java.util.List;

public class YearMonthUtil {
    public static List<String> format (Integer year , Integer month , Integer length){
        List<String> list = new ArrayList<String>();
        String yearMonth = "";
        if (month > length) {
            for (int i = length; i > 0; i--) {
                if (month < 10 && month > 0) {
                    String monthStr = "-0" + month;
                    yearMonth = year + monthStr;
                    System.out.println(yearMonth);
                    list.add(yearMonth);
                    month--;
                } else {
                    String monthStr = "-" + month;
                    yearMonth = year + monthStr;
                    System.out.println(yearMonth);
                    list.add(yearMonth);
                    month--;
                }
            }
        } else {
            Integer m = month;
            for (int i = month; i > 0; i--) {
                String monthStr = "-0" + month;
                yearMonth = year + monthStr;
                System.out.println(yearMonth);
                list.add(yearMonth);
                month--;
            }
            Integer num = length - m;
            System.out.println(num);
            for (int n = 0; n <= num / 12 + 1; n++) {
                System.out.println("第"+n+"次");
                if (n>=1){
                    num = num - 12;
                }
                System.out.println(num);
                year = year - 1;
                System.out.println(year);
                for (int i = 12; i > 12 - num && i>0; i--) {
                    if (i < 10) {
                        String monthStr = "-0" + i;
                        yearMonth = year + monthStr;
                        System.out.println(yearMonth);
                        list.add(yearMonth);
                    } else {
                        String monthStr = "-" + i;
                        yearMonth = year + monthStr;
                        System.out.println(yearMonth);
                        list.add(yearMonth);
                    }
                }
            }
        }
        return list;
    }
}
