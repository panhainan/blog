package site.sixteen.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.sixteen.blog.entity.User;
import site.sixteen.blog.entity.UserAuth;
import site.sixteen.blog.entity.UserLog;
import site.sixteen.blog.enums.GenerateValidCodeResult;
import site.sixteen.blog.exception.UserPasswordException;
import site.sixteen.blog.exception.UserRegisterException;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public interface UserService {
    /**
     * 根据用户唯一标识（即用户名）获取用户信息
     * @param username
     * @return
     */
    User getUserByUsername(String username);

    /**
     * 用户注册
     *
     * @param userAuth 用户授权信息
     * @return uid 用户ID
     * @throws UserRegisterException
     */
    Long register(UserAuth userAuth) throws UserRegisterException;

    /**
     * 根据用户唯一标识（即用户名）获取用户授权信息
     *
     * @param username 手机号 邮箱 用户名或第三方应用的唯一标识
     * @return UserAuth
     */
    UserAuth getUserAuthByUsername(String username);

    /**
     * 获取当前用户
     * @return
     */
    User getCurrentUser();

    /**
     * 当前用户修改个人基本资料
     * @param user
     * @return
     */
    Long updateMyInfo(User user);

    /**
     * 修改密码
     * @param password
     * @param newPassword
     * @return
     * @throws UserPasswordException
     */
    Long updateMyPass(String password, String newPassword) throws UserPasswordException;

    /**
     * 记录用户登录日志
     * @param userLog
     */
    void logUserLogin(UserLog userLog);

    /**
     * 获取个人登录日志
     * @param pageable
     * @return
     */
    Page<UserLog> getMyLogs(Pageable pageable);

    /**
     * 生成邮箱验证码并发送邮箱验证码给用户
     * @param email
     * @return
     */
    GenerateValidCodeResult generateEmailValidCode(String email);

    /**
     * 检查验证码是否正确有效
     * @param email
     * @param validCode
     * @return
     */
    boolean validEmailCode(String email, String validCode);
}
