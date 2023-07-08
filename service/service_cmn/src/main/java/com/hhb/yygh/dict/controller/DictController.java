package com.hhb.yygh.dict.controller;


import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.dict.service.DictService;
import com.hhb.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author hhb
 * @since 2023-06-04
 */
@Api(tags = "字典数据设置接口")
@RestController
@RequestMapping("/admin/cmn")
public class DictController {
    @Autowired
    private DictService dictService;

    //导出文件
    @GetMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        dictService.download(response);
    }
    //导入文件
    @PostMapping("/upload")
    public R upload(MultipartFile file) throws IOException{
        dictService.upload(file);
        return R.ok();
    }
    //根据父id查找相应的子列表
    @ApiOperation(value = "查找字列表")
    @GetMapping("/childList/{pid}")
    public R getChildListByPid(@PathVariable Long pid){
        List<Dict> list = dictService.getChildListByPid(pid);
        return R.ok().data("items",list);
    }

    //根据value查找对应的省市区
    @GetMapping("/{value}")
    public String getNameByValue(@PathVariable("value") Long value){
        return dictService.getByName(value);
    }
    //查找指定的名称
    @GetMapping("/{dictCode}/{value}")
    public String getSelectedNameByValue(
            @PathVariable("dictCode") String dictCode,
            @PathVariable("value") Long value){
        return dictService.getSelectedNameByValue(dictCode,value);
    }
}

