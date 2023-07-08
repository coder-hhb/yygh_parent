package com.hhb.yygh;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class Student {
    @ExcelProperty(value = "学生id",index = 0)
    private Integer sid;
    @ExcelProperty(value = "学生姓名",index = 1)
    private String sname;
    @ExcelProperty(value = "学生年龄",index = 2)
    private Integer age;
    @ExcelProperty(value = "学生性别",index = 3)
    private boolean gender;

    public Student(Integer sid, String sname, Integer age, boolean gender) {
        this.sid = sid;
        this.sname = sname;
        this.age = age;
        this.gender = gender;
    }

    public Student() {
    }
}
