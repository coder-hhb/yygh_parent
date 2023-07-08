package com.hhb.yygh.hosp.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhb.yygh.common.config.exception.YyghException;
import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.hosp.service.HospitalSetService;
import com.hhb.yygh.common.config.utils.MD5;
import com.hhb.yygh.model.hosp.HospitalSet;
import com.hhb.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Random;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author hhb
 * @since 2023-06-03
 */
@Api(tags = "医院设置接口")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation(value = "医院设置列表")
    @GetMapping("/getAll")
    public R list(){
        List<HospitalSet> list = null;
       try{
           list = hospitalSetService.list();
       }catch (Exception ex){
           throw new YyghException(200013,"xxx异常");
       }
        return R.ok().data("list",list);

    }


    @ApiOperation(value = "删除医院设置")
    @DeleteMapping("/deleteById/{id}")
    public R delete(@PathVariable long id){
        return hospitalSetService.removeById(id) ? R.ok() : R.error();
    }


    @ApiOperation(value = "分页医院设置列表")
    @GetMapping("/page/{pageNum}/{size}")
    public R getHospitalSetPage(
            @ApiParam(name = "pageNum", value = "当前页码", required = true)
            @PathVariable long pageNum,
            @ApiParam(name = "size", value = "每页记录数", required = true)
            @PathVariable long size
            ){
        Page<HospitalSet> hospitalSetPage = new Page<>(pageNum, size);
       hospitalSetService.page(hospitalSetPage,null);
        List<HospitalSet> list = hospitalSetPage.getRecords();
        long total = hospitalSetPage.getTotal();
        return R.ok().data("total",total).data("rows",list);
    }

    //带条件的分页查询
    //    @ApiModelProperty(value = "医院名称")
    //    private String hosname;
    //
    //    @ApiModelProperty(value = "医院编号")
    //    private String hoscode;
    @ApiOperation(value = "分页条件医院设置列表")
    @PostMapping("/page/{pageNumber}/{size}")
    public R getPageHospitalSet(
            @ApiParam(name = "pageNumber", value = "当前页码", required = true)
            @PathVariable long pageNumber,
            @ApiParam(name = "size", value = "每页记录数", required = true)
            @PathVariable long size,
            @ApiParam(name = "hospitalSetQueryVo", value = "查询对象", required = false)
            @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo
            ){
        Page<HospitalSet> page1 = new Page<>(pageNumber, size);
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        //参数为空查询全部
        if(hospitalSetQueryVo == null){
            hospitalSetService.page(page1,queryWrapper);
        }else{
            String hoscode = hospitalSetQueryVo.getHoscode();
            String hosname = hospitalSetQueryVo.getHosname();
            if(!StringUtils.isEmpty(hosname)){
                queryWrapper.like("hosname",hosname);
            }
            if(!StringUtils.isEmpty(hoscode)){
                queryWrapper.eq("hoscode",hoscode);
            }
            hospitalSetService.page(page1,queryWrapper);
        }
        List<HospitalSet> rows = page1.getRecords();
        long total = page1.getTotal();
        return R.ok().data("total",total).data("rows",rows);
    }

    @ApiOperation(value = "新增医院设置")
    @PostMapping("/saveHospSet")
    public R saveHospSet(
            @ApiParam(name = "hospitalSet", value = "医院设置对象")
            @RequestBody HospitalSet hospitalSet){
        //设置状态 1可用 0 不可用
        hospitalSet.setStatus(1);
        //签名密匙
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        return hospitalSetService.save(hospitalSet) ? R.ok() : R.error();
    }

    @ApiOperation(value = "根据ID查询医院设置")
    @GetMapping("/getHospSet/{id}")
    public R getHospSet(
            @ApiParam(name = "id", value = "医院设置ID", required = true)
            @PathVariable long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if(hospitalSet != null){
            return R.ok().data("item",hospitalSet);
        }else{
            return R.error();
        }
    }
    @ApiOperation(value = "根据ID修改医院设置")
    @PutMapping("/updateHospSet")
    public R updateHospSet(
            @ApiParam(name = "hospitalSet", value = "医院设置对象", required = true)
            @RequestBody HospitalSet hospitalSet){
        return hospitalSetService.updateById(hospitalSet) ? R.ok() : R.error();
    }

    //批量删除医院设置
    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("/delete")
    public R batchRemove(@RequestBody List<Long> ids){
       return hospitalSetService.removeByIds(ids) ? R.ok() : R.error();
    }

    //医院解锁与锁定
    @ApiOperation(value = "医院解锁与锁定")
    @PutMapping("/status/{id}/{status}")
    public R updateStatus(@PathVariable long id,@PathVariable Integer status){
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setId(id);
        hospitalSet.setStatus(status);
        return hospitalSetService.updateById(hospitalSet) ? R.ok() : R.error();
    }

}

