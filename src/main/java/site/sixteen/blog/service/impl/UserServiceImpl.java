package site.sixteen.blog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import site.sixteen.blog.entity.User;
import site.sixteen.blog.entity.UserAuth;
import site.sixteen.blog.entity.UserLog;
import site.sixteen.blog.exception.UserPasswordException;
import site.sixteen.blog.exception.UserRegisterException;
import site.sixteen.blog.repository.UserAuthRepository;
import site.sixteen.blog.repository.UserLogRepository;
import site.sixteen.blog.repository.UserRepository;
import site.sixteen.blog.service.UserService;

import java.util.Date;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static Integer IDENTITY_TYPE_PHONE = 1;
    private static Integer IDENTITY_TYPE_EMAIL = 2;
    private static Integer IDENTITY_TYPE_USERNAME = 3;
    private static Integer IDENTITY_TYPE_QQ = 4;
    private static Integer IDENTITY_TYPE_WECHAT = 5;
    private static Integer IDENTITY_TYPE_SINA = 6;


    @Autowired
    UserAuthRepository userAuthRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserLogRepository userLogRepository;

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public Long register(UserAuth userAuth) throws UserRegisterException {
        if (checkUsernameIsExisted(userAuth.getUsername())) {
            throw new UserRegisterException("用户名已经存在！");
        }
        userAuth.setRegisterSource(IDENTITY_TYPE_USERNAME);
        userAuth.setDisabled(false);
        Date newDate = new Date();
        userAuth.setCreateTime(newDate);
        userAuth.setUpdateTime(newDate);
        userAuthRepository.save(userAuth);
        User user = new User();
        user.setUsername(userAuth.getUsername());
        user.setCreateTime(new Date());
        userRepository.save(user);
        return 1L;
    }

    @Override
    public User getCurrentUser() {
        Subject currentSubject = SecurityUtils.getSubject();
        String username = String.valueOf(currentSubject.getPrincipal());
        User user = getUserByUsername(username);
        return user;
    }

    @Override
    public Long updateMyInfo(User user) {
        User dbUser = getCurrentUser();
        dbUser.setNickname(user.getNickname());
        dbUser.setGender(user.getGender());
        dbUser.setBirthday(user.getBirthday());
        dbUser.setSignature(user.getSignature());
        if (!StringUtils.isEmpty(user.getMobile())) {
            dbUser.setMobile(user.getMobile());
            dbUser.setMobileBindTime(new Date());
        }
        if (!StringUtils.isEmpty(user.getEmail())) {
            dbUser.setEmail(user.getEmail());
            dbUser.setEmailBindTime(new Date());
        }
        if (!StringUtils.isEmpty(user.getFace())) {
            dbUser.setFace(user.getFace());
        }
        dbUser.setUpdateTime(new Date());
        userRepository.saveAndFlush(dbUser);
        return 1L;
    }

    @Override
    public Long updateMyPass(String password, String newPassword) throws UserPasswordException {
        UserAuth dbUserAuth = getCurrentUserAuth();
        if (!dbUserAuth.getPassword().equals(password)) {
            throw new UserPasswordException("原密码不正确！");
        }
        dbUserAuth.setPassword(newPassword);
        userAuthRepository.save(dbUserAuth);
        return 1L;
    }

    @Override
    public void logUserLogin(UserLog userLog) {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()){
            return ;
        }else{
            Session session = subject.getSession(true);
            log.info("{}", session);
            userLog.setUsername(String.valueOf(subject.getPrincipal()));
            userLog.setLogTime(new Date());
            userLog.setIp(session.getHost());
            userLogRepository.save(userLog);
        }
    }

    @Override
    public Page<UserLog> getMyLogs(Pageable pageable) {
        Subject subject = SecurityUtils.getSubject();
        String username = String.valueOf(subject.getPrincipal());
        return userLogRepository.findUserLogsByUsernameOrderByLogTimeDesc(username,pageable);
    }

    private UserAuth getCurrentUserAuth() {
        Subject currentSubject = SecurityUtils.getSubject();
        String username = String.valueOf(currentSubject.getPrincipal());
        UserAuth userAuth = getUserAuthByUsername(username);
        return userAuth;
    }

    private boolean checkUsernameIsExisted(String username) {
        UserAuth userAuth = userAuthRepository.findUserAuthByUsername(username);
        if (userAuth != null) {
            return true;
        }
        return false;
    }

    @Override
    public UserAuth getUserAuthByUsername(String username) {
        return userAuthRepository.findUserAuthByUsername(username);
    }
}