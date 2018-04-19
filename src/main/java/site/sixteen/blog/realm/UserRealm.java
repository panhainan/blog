package site.sixteen.blog.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import site.sixteen.blog.entity.UserAuth;
import site.sixteen.blog.service.UserService;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Slf4j
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("doGetAuthorizationInfo");
        return null;
    }

    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("doGetAuthenticationInfo...");
        //1. 把 AuthenticationToken 转换为 UsernamePasswordToken
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;

        //2. 从 UsernamePasswordToken 中来获取 username
        String username = usernamePasswordToken.getUsername();

        //3. 调用数据库的方法, 从数据库中查询 username 对应的用户记录
        log.info("从数据库中获取 username: {} 所对应的用户信息.", username);
        UserAuth userAuth = userService.getUserAuthByUsername(username);

        //4. 若用户不存在, 则可以抛出 UnknownAccountException 异常
        if (!username.equals(userAuth.getUsername())) {
            throw new UnknownAccountException("用户不存在!");
        }
        //6. 根据用户的情况, 来构建 AuthenticationInfo 对象并返回. 通常使用的实现类为: SimpleAuthenticationInfo
        //以下信息是从数据库中获取的.
        //1). principal: 认证的实体信息. 可以是 username, 也可以是数据表对应的用户的实体类对象.
        Object principal = userAuth.getUsername();
        //2). credentials: 密码.
        Object credentials = userAuth.getPassword();

        //3). realmName: 当前 realm 对象的 name. 调用父类的 getName() 方法即可
        String realmName = getName();
        //4). 盐值. ByteSource credentialsSalt = ByteSource.Util.bytes(username);

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, realmName);
        //new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName);

        return info;
    }
}