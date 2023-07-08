package com.hhb.yygh.common.config.handler;

import com.hhb.yygh.common.config.exception.YyghException;
import com.hhb.yygh.common.config.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public R handleException(Exception e){
        e.printStackTrace();
        return R.error().message(e.getMessage());
    }

    @ExceptionHandler(value = YyghException.class)
    public R handleYyghException(YyghException ex){
        ex.printStackTrace();
        return R.error().code(ex.getCode()).message(ex.getMessage());
    }
}
