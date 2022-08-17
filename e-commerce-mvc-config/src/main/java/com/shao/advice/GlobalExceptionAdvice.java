package com.shao.advice;

import com.shao.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = Exception.class)
    public CommonResponse<String> handlerCommonException(HttpServletRequest request, Exception ex) {
        CommonResponse<String> commonResponse = new CommonResponse<>(-1, "bussiness error");
        commonResponse.setData(ex.getMessage());
        log.error(ex.getMessage(), ex);
        return commonResponse;
    }
}
