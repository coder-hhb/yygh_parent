package com.hhb.yygh.dict.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hhb.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author hhb
 * @since 2023-06-04
 */
public interface DictService extends IService<Dict> {

    List<Dict> getChildListByPid(Long pid);

    void download(HttpServletResponse response) throws IOException;

    void upload(MultipartFile file) throws IOException;

    String getByName(Long value);

    String getSelectedNameByValue(String dictCode,Long value);
}
