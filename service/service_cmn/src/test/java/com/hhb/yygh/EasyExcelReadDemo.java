package com.hhb.yygh;

import com.alibaba.excel.EasyExcel;

public class EasyExcelReadDemo {
    public static void main(String[] args){
    EasyExcel.read("D:\\hello.xlsx", Student.class, new StudentListListener()).sheet(0).doRead();
    }
}

