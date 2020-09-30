package com.lhfeiyu.tech.advice;

import com.alibaba.fastjson.JSONObject;
import com.lhfeiyu.tech.config.ResponseCode;
import com.lhfeiyu.tech.exception.LogonException;
import com.lhfeiyu.tech.exception.ParamErrorException;
import com.lhfeiyu.tech.tools.Result;
import com.lhfeiyu.tech.tools.ReturnCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

/**
 * 异常统一处理，
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public Object hibernateValidatorExceptionHandler(ConstraintViolationException e, HttpServletRequest request, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        String message = e.getConstraintViolations().iterator().next().getMessage();
        String[] msgAry = message.split("-");
        String param = msgAry[0];
        String msg = msgAry[1];
        json.put("param", param);
        return Result.failure(json, msg, ResponseCode.PARAM_INVALID.getCode());
    }


    /***
     * 404处理
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object notFountHandler(HttpServletRequest request, NoHandlerFoundException e) {
        JSONObject json = new JSONObject();
        /*String method = request.getMethod();
        String path = request.getRequestURI();
        logger.warn("404 url:{},method:{}", path, method);*/
        return Result.failure(json, ResponseCode.SERVER_404);
    }


    @ResponseBody
    @ExceptionHandler(ParamErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JSONObject paramError (ParamErrorException e) {
        return ReturnCode.failure(new JSONObject(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {LogonException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JSONObject paramError (LogonException e) {
        return ReturnCode.failure(new JSONObject(), e.getMessage(), 401);
    }


    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        logger.error("server exception:{}", e.getMessage());
        e.printStackTrace();
        return Result.failure(json, ResponseCode.SERVER_EXCEPTION);
    }

}