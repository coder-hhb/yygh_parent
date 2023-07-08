package com.hhb.yygh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn")
public interface DictFeignClient {
    //根据value查找对应的省市区
    @GetMapping("/admin/cmn/{value}")
    public String getNameByValue(@PathVariable("value") Long value);

    //查找对应的医院
    @GetMapping("/admin/cmn/{dictCode}/{value}")
    public String getSelectedNameByValue( @PathVariable("dictCode") String dictCode, @PathVariable("value") Long value);
}
