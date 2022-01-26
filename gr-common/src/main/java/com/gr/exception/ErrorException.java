package com.gr.exception;//package com.gr.exception;
//
//import org.springframework.boot.web.server.ErrorPage;
//import org.springframework.boot.web.server.ErrorPageRegistrar;
//import org.springframework.boot.web.server.ErrorPageRegistry;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//
///**
// * 自定义异常
// *
// * @author liangc
// * @data 2019/9/4 10:31
// */
//@Component
//public class ErrorException implements ErrorPageRegistrar {
//
//    @Override
//    public void registerErrorPages(ErrorPageRegistry registry) {
//
//        ErrorPage error400Page = new ErrorPage(HttpStatus.BAD_REQUEST, "/modules/error/404.html");
//        ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/modules/error/403.html");
//        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/modules/error/404.html");
//        ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/modules/error/500.html");
//
//        ErrorPage argsException = new ErrorPage(IllegalArgumentException.class, "/modules/error/404.html");
//
//        registry.addErrorPages(error400Page, error401Page, error404Page, error500Page, argsException);
//    }
//}
