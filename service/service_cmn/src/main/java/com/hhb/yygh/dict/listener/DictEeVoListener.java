package com.hhb.yygh.dict.listener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhb.yygh.dict.mapper.DictMapper;
import com.hhb.yygh.model.cmn.Dict;
import com.hhb.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

public class DictEeVoListener extends AnalysisEventListener<DictEeVo> {
    private DictMapper dictMapper;
    public DictEeVoListener(DictMapper dictMapper){
        this.dictMapper = dictMapper;
    }
    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        //导入文件
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",dictEeVo.getId());
        Integer count = dictMapper.selectCount(queryWrapper);
        //已存在数据进行更新操作
        if(count > 0){
            dictMapper.updateById(dict);
        }else{
            //不存在直接插入
            dictMapper.insert(dict);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
