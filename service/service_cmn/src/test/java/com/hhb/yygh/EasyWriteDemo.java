package com.hhb.yygh;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class EasyWriteDemo {
    public static void main(String[] args){
        List<Student> list = new ArrayList<>();
        list.add(new Student(1,"朱晓曦",18,true));
        list.add(new Student(2,"朱晓曦22",28,false));
        list.add(new Student(3,"朱晓曦33",38,true));
        EasyExcel.write("D:\\hello.xlsx",Student.class).sheet("学生列表1").doWrite(list);
    }
}
