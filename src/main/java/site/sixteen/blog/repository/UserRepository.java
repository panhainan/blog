package site.sixteen.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.sixteen.blog.entity.User;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public interface UserRepository extends JpaRepository<User,Long> {
    /**
     * 通过username(唯一)查找用户
     * @param username
     * @return
     */
    User findUserByUsername(String username);
}
