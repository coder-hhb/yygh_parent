package com.hhb.yygh.dict.service.impl;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hhb.yygh.dict.listener.DictEeVoListener;
import com.hhb.yygh.dict.mapper.DictMapper;
import com.hhb.yygh.dict.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhb.yygh.model.cmn.Dict;
import com.hhb.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.Cacheable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author hhb
 * @since 2023-06-04
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Cacheable(value = "dict")
    @Override
    public List<Dict> getChildListByPid(Long pid) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",pid);
        List<Dict> dicts = baseMapper.selectList(queryWrapper);
        dicts.stream().forEach(dict -> dict.setHasChildren(isHasChildren(dict.getId())));
        return dicts;
    }

    //导出文件功能
    @Override
    public void download(HttpServletResponse response) throws IOException {
        List<Dict> dicts = baseMapper.selectList(null);
        //转化为dictEevoList
        List<DictEeVo> dictEeVoList = dicts.stream().map(dict -> {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict, dictEeVo);
            return dictEeVo;
        }).collect(Collectors.toList());
        //设置存入的文件
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("数据字典", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(),DictEeVo.class).sheet("学生列表1").doWrite(dictEeVoList);
    }

    @Override
    @CacheEvict(value = "dict", allEntries=true)
    public void upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictEeVoListener(baseMapper)).sheet(0).doRead();
    }

    @Override
    public String getByName(Long value) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("value",value);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);
        if(dict != null){
            return dict.getName();
        }else{
            return null;
        }
    }

    @Override
    public String getSelectedNameByValue(String dictCode,Long value) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code",dictCode);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);


        QueryWrapper<Dict> dictTwoQueryWrapper = new QueryWrapper<>();
        dictTwoQueryWrapper.eq("parent_id",dict.getId()).eq("value",value);
        Dict dictTwo = baseMapper.selectOne(dictTwoQueryWrapper);
        if(dictTwo != null){
            return dictTwo.getName();
        }else{
            return null;
        }
    }

    //查找是否有子节点
    private boolean isHasChildren(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }
}
