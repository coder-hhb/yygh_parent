package com.hhb.yygh.common.config.exception;

public class YyghException extends RuntimeException{
  private Integer code;
  private String message;

  public YyghException(Integer code,String message){
      this.code = code;
      this.message = message;
  }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
