package site.sixteen.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.sixteen.blog.entity.UserLog;

import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public interface UserLogRepository extends JpaRepository<UserLog,Long>{

    /**
     * 获取用户登录日志
     * @param username
     * @param pageable
     * @return
     */
    Page<UserLog> findUserLogsByUsernameOrderByLogTimeDesc(String username, Pageable pageable);
}
