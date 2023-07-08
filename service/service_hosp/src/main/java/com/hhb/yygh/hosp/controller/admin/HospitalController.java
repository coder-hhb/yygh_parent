package com.hhb.yygh.hosp.controller.admin;

import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.hosp.service.HospitalService;
import com.hhb.yygh.model.hosp.Hospital;
import com.hhb.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hospital")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;
    @GetMapping("{pageNum}/{pageSize}")
    public R getHospitalPage(@PathVariable Integer pageNum, @PathVariable Integer pageSize , HospitalQueryVo hospitalQueryVo){
        Page<Hospital> page = hospitalService.getHospitalPage(pageNum,pageSize,hospitalQueryVo);
        long total = page.getTotalElements();
        List<Hospital> content = page.getContent();
        return R.ok().data("total",total).data("rows",content);
    }

    //修改医院的状态
    @PutMapping("/{id}/{status}")
    public R updateStatus(@PathVariable String id,@PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return R.ok();
    }
    //查看医院详情页
    @GetMapping("/detail/{id}")
    public R detailHospital(@PathVariable String id){
        Hospital hospital = hospitalService.getHospitalDetailById(id);
        return R.ok().data("hospital",hospital);
    }
}
