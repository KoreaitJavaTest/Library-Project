package com.library.application.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//인터셉터
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //토근을 복호화하고 유저의 토큰이맞는지 확인하는 클래스의 인스턴스
    private final BearerAuthInterceptor bearerAuthInterceptor;
    private final String uploadImgesPath;
    private final String uploadPdfPath;

    @Autowired
    public WebMvcConfig(BearerAuthInterceptor bearerAuthInterceptor, @Value("${custom.path.upload-imges}") String uploadImgesPath,
                        @Value("${custom.path.upload-pdf}")String uploadPdfPath) {
        this.bearerAuthInterceptor = bearerAuthInterceptor;
        this.uploadImgesPath=uploadImgesPath;
        this.uploadPdfPath = uploadPdfPath;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/check/book")
        .addPathPatterns("/library/book/*/lend")
        .addPathPatterns("/library/save")
        .addPathPatterns("/library/return")
        .addPathPatterns("/library/book/*")
        .addPathPatterns("/library/book/favorites/**")
        .addPathPatterns("/user-service/pwdCheck");
        //  user/login = 로그인 요청을 받으면 요청메소드를 처리전에 인터셉터에서 걸어둔 필터를 한번 거친다.
    }

        //인터셉터로 pdf 경로도 만들어보자!!
    /** Resource Handler
     *  PDF , IMG , text 수정 할것*/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/img/**")
                .addResourceLocations("file:/"+uploadImgesPath);
        registry
                .addResourceHandler("/pdf/**")
                .addResourceLocations("file:/"+uploadPdfPath);
        // img/filename , pdf/pdfFileName
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8"); // 파일 인코딩 설정
        multipartResolver.setMaxUploadSizePerFile(5 * 1024 * 1024); // 파일당 업로드 크기 제한 (5MB)
        return multipartResolver;
    }
}
