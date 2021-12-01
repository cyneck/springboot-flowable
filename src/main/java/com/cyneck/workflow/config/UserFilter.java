package com.cyneck.workflow.config;

import com.cyneck.workflow.service.impl.UserCustomServiceCustomImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 业务用户过滤器
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/18 18:07
 **/
@Component
@Configuration
public class UserFilter extends OncePerRequestFilter {

    private static final PathMatcher pathMatcher = new AntPathMatcher();
    private static final String protectUrlPattern = "/api/**";

    @Resource
    UserCustomServiceCustomImpl userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
//        if(SpringContextHandler.getActiveProfile().equals("dev")){
//            //如果jwt令牌通过了检测, 那么就把request传递给后面的RESTful api
//            filterChain.doFilter(request, response);
//            return;
//        }
        filterChain.doFilter(request, response);
//        try {
//            if(isProtectedUrl(request)) {
//                String userId = request.getHeader("userId");
//                MyUserInfo userInfo =userService.findUser(userId);
//                //最关键的部分就是这里, 我们直接注入了
//                SecurityContextHolder.getContext().setAuthentication(
//                        new UsernamePasswordAuthenticationToken(
//                                userInfo,
//                                null,
//                                Arrays.asList(() -> "rest")));
                //如果jwt令牌通过了检测, 那么就把request传递给后面的RESTful api
//                filterChain.doFilter(request, response);
//            }
//        } catch (Exception e) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
//            return;
//        }

    }


    //我们只对地址 /api 开头的api检查jwt. 不然的话登录/login也需要jwt
    private boolean isProtectedUrl(HttpServletRequest request) {
        return pathMatcher.match(protectUrlPattern, request.getServletPath());
    }
}
