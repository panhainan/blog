package site.sixteen.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.sixteen.blog.entity.UserAuth;

/**
 * 用户授权信息Repository
 *
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    /**
     * 通过username(唯一)查找授权信息
     *
     * @param username
     * @return
     */
    UserAuth findUserAuthByUsername(String username);

}
