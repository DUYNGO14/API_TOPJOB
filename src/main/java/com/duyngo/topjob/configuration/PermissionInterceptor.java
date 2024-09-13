package com.duyngo.topjob.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.duyngo.topjob.domain.Permission;
import com.duyngo.topjob.domain.Role;
import com.duyngo.topjob.domain.User;
import com.duyngo.topjob.exception.PermissionException;
import com.duyngo.topjob.service.UserService;
import com.duyngo.topjob.util.SecurityUtil;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // check quyền hạn
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (email != null && !email.isEmpty()) {
            User user = this.userService.getUserByEmail(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream()
                            .anyMatch(
                                    item -> item.getApiPath().equals(path) &&
                                            item.getMethod().equals(httpMethod));
                    if (isAllow == false) {
                        throw new PermissionException("Bạn không có quyền truy cập endpoint này!");
                    }
                    System.out.println(">>> requestURI= " + requestURI);
                } else {
                    throw new PermissionException("Bạn không có quyền truy cập endpoint này!");
                }
            }
        }
        return true;
    }
}