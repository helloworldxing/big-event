package com.itheima.config;

import com.itheima.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor LoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        System.out.println("注册拦截器");
        //登陆和注册接口不拦截
        registry.addInterceptor(LoginInterceptor).excludePathPatterns("/user/login","/user/register","/user/update");
//        registry.addInterceptor(new LoginInterceptor())
//                .addPathPatterns("/")  // 确保路径正确
//                .excludePathPatterns("/login", "/error");
    }
}
