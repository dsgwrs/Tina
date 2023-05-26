package com.example.tina.filter;

import com.alibaba.fastjson.JSON;
import com.example.tina.common.Result;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

/**
 * 过滤器
 * 检查用户是否已经完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径适配器
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取本次请求地址
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);
        //配置不需要拦截的地址
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout"
        };
        //检查是否需要处理
        boolean check = check(urls, requestURI);
        //如果不需要，则放行
        if (check) {
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        //如果已经登陆，则放行
        if (request.getSession().getAttribute("employee") != null) {
            filterChain.doFilter(request, response);
            return;
        }
        response.getWriter().write(JSON.toJSONString(Result.error("NOLOGIN")));
    }

    /**
     * 路径匹配，检查是否放行
     *
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
