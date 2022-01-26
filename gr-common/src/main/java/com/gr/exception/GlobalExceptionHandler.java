package com.gr.exception;

import com.gr.constant.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.ServletException;


/**
 * 全局异常处理类
 *
 * @ExceptionHandler 注解描述的方法为异常处理方法
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(RRException.class)
    public R handleRRException(RRException e) {
        R r = new R();
        r.put("code", e.getCode());
        r.put("msg", e.getMessage());

        return r;
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(ResultException.class)
    public R resultException(ResultException e) {
        R r = new R();
        r.put("code", e.getCode());
        r.put("msg", e.getMessage());

        return r;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public R duplicateKeyException(DuplicateKeyException e) {
        return R.error("数据库中已存在该用户");
    }

    @ExceptionHandler(AuthorizationException.class)
    public R authorizationException(AuthorizationException e) {
        return R.error("没有权限，请联系管理员授权");
    }

    @ExceptionHandler(IllegalStateException.class)
    public R illegalStateException(IllegalStateException e) {
        return R.error("系统繁忙，请稍后重试");
    }


    @ExceptionHandler(ServletException.class)
    public R servletException(ServletException e) {
        log.error("servletException error {}", e.getMessage());
        return R.error("请求失败，请稍后重试");
    }

    @ExceptionHandler(StringIndexOutOfBoundsException.class)
    public R stringIndexOutOfBoundsException(StringIndexOutOfBoundsException e) {
        log.error("stringIndexOutOfBoundsException error {}", e.getMessage());
        return R.error("系统繁忙，请稍后重试");
    }

    /**
     * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("MaxUploadSizeExceededException error {}", e.getMessage());
        return R.error("文件大小超出100MB限制, 请压缩或降低文件质量! ");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public R dataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("dataIntegrityViolationException error {}", e.getMessage());
        return R.error("数据超长,写入失败");
    }

    @ExceptionHandler(NullPointerException.class)
    public R nullPointerException(NullPointerException e) {
        log.error("nullPointerException error {}", e.getMessage());
        e.printStackTrace();
        return R.error("未知异常，请联系系统管理员！");
    }

    @ExceptionHandler(Exception.class)
    public R exception(Exception e) {
        log.error("exception error {}", e.getMessage());
        return R.error();
    }

}
