package com.mytijian.admin.web.exception;

import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.mytijian.exception.AppException;
import com.mytijian.mediator.exceptions.ServiceException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class NewExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler( Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, HttpServletResponse response, Exception ex) throws Exception {
        logger.error("服务器异常:" + ex.getMessage(), ex);

        ModelAndView mv = new ModelAndView();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        Map<String, Object> attributes = new HashMap<String, Object>();
        try {
            getExceptionResult(ex, mv, attributes);
        } catch (Exception e) {
            logger.error("服务器内部异常:" + e.getMessage(), e);
            FastJsonJsonView view = new FastJsonJsonView();
            attributes.put("code", CommonConstants.SYS_EXCEPTION);
            attributes.put("text", CommonConstants.SYS_EXCEPTION_DESC);
            view.setAttributesMap(attributes);
            mv.setView(view);
        }

        return mv;
    }

    // 数据返回格式 {"data":, "msg(text)":, "code": }
    private void getExceptionResult(Exception ex, ModelAndView mv, Map<String, Object> attributes) {
        FastJsonJsonView view = new FastJsonJsonView();
        if (ex instanceof ServiceException) {
            attributes.put("code", ((ServiceException) ex).getCodeValue());
            attributes.put("text", ((ServiceException) ex).getMsg());
        } else if (ex instanceof RpcException) {
            attributes.put("code", CommonConstants.RPC_EXCEPTION);
            attributes.put("text", CommonConstants.RPC_EXCEPTION_DESC);
        } else if (ex instanceof UnauthorizedException) {
            attributes.put("code", CommonConstants.UNAUTHON_EXCEPTION);
            attributes.put("text", CommonConstants.UNAUTHON_EXCEPTION_DESC);
        } else if (ex instanceof AppException) {
            AppException e =(AppException) ex;
            attributes.put("code", e.getErrorCode());
            attributes.put("text", e.getErrorMsg());
            attributes.put("extInfo", e.getExtInfo());
        } else if (ex instanceof com.mytijian.base.result.AppException) {
        	com.mytijian.base.result.AppException e =(com.mytijian.base.result.AppException) ex;
            attributes.put("code", e.getErrorCode());
            attributes.put("text", e.getErrorMsg());
            attributes.put("extInfo", e.getExtInfo());
        }else {
            attributes.put("code", CommonConstants.SYS_EXCEPTION);
            attributes.put("text", ex.getMessage());
        }
        view.setAttributesMap(attributes);
        mv.setView(view);
    }
}
