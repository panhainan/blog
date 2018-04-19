package site.sixteen.blog.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import site.sixteen.blog.entity.UserLog;
import site.sixteen.blog.service.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Slf4j
@Aspect
@Component
public class UserLogAop {

    @Autowired
    UserService userService;

    @Pointcut("execution(* site.sixteen.blog.controller.UserController.login(*,*))")
    public void performance() {
    }


    @After("performance()")
    public void loginLog() {
        log.info("记录登录日志...");
        UserLog userLog = new UserLog();
        userService.logUserLogin(userLog);
    }

}
