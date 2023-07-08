package com.hhb.yygh.oss.controller;

import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "上传文件")
@RestController
@RequestMapping("/user/oss/file")
public class OssController {
    @Autowired
    private FileService fileService;

    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public R uploadFile(@RequestParam("file") MultipartFile file){
        String uploadUrl = fileService.uploadFile(file);
        return R.ok().message("文件上传成功").data("url",uploadUrl);
    }
}
