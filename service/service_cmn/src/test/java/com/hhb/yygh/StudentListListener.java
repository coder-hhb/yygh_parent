package com.hhb.yygh;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class StudentListListener extends AnalysisEventListener<Student> {
    //解析excel每一行数据都会调用该方法
    @Override
    public void invoke(Student student, AnalysisContext analysisContext) {
        System.out.println(student);
    }

    //解析excel某个标题的信息
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
    System.out.println("标题为" + headMap);
    }

    //当excel文件被解析完毕后，执行该方法
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
